/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of Logging.
 *
 * Logging is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Logging is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Logging. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri.logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class RollingFileHandle {

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    static File getFth(File baseDir, String filename) {
        if (baseDir == null) throw new RuntimeException("baseDir can't be NULL");
        if (filename == null) throw new RuntimeException("filename can't be NULL");
        if (filename.isEmpty()) throw new RuntimeException("filename can't be empty");


        if (filename.contains(baseDir.getAbsolutePath())) {
            filename = filename.replace(baseDir.getAbsolutePath(), "");
        }

        return new File(baseDir, filename);
    }

     final File HANDLE;
     final File BASE_DIR;

    public RollingFileHandle(File baseDir, String filename) throws RuntimeException {
        HANDLE = getFth(baseDir, filename);
        BASE_DIR = baseDir;

        if (baseDir.exists() && !baseDir.isDirectory()) throw new RuntimeException("Base dir can't be a file");

        if (!baseDir.exists()) {
            //create base dir if not exist
            baseDir.mkdirs();
            sleep(200);
            if (!baseDir.exists()) throw new RuntimeException("Can't create base dir");
        }
    }

    /**
     * @param datePattern
     * @param age         days for keep, before rolling
     * @param keep
     * @return
     */
    public boolean rolling(String datePattern, int age, int keep) {
        boolean rolling = false;
        if (!HANDLE.exists()) {
            return rolling;
        }

        try {
            BasicFileAttributes attr = Files.readAttributes(HANDLE.toPath(), BasicFileAttributes.class);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime creationTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
            LocalDateTime keepDate = creationTime.plusDays(age - 1);

            if (!isDateBefore(keepDate, now)) {
                return rolling;
            }
        } catch (Exception e) {
            return rolling;
        }

        //create new storage name from pattern
        sleep(500);
        String newName = getFileNameFromPattern(datePattern);
        File movedFile = new File(BASE_DIR, newName);

        if (extension(movedFile).equals("zip")) {

            try {
                this.zipTo(movedFile);
            } catch (IOException  e) {
                throw new RuntimeException(e);
            }

            // delete the current log file, this will then create new later
            sleep(500);
            HANDLE.delete();
        } else {
            this.moveTo(movedFile);
        }
        sleep(500);
        if (!movedFile.exists())
            throw new RuntimeException("can't moving rolling file from '" + this + "' to '" + movedFile + "'");

        keep(datePattern, age, keep);
        return true;
    }

    private String extension(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos < 1) return null;
        return name.substring(pos + 1);
    }

    private void moveTo(File dest) {
        File rnf = new File(HANDLE.getAbsolutePath());
        File nf = new File(dest.getAbsolutePath());
        rnf.renameTo(nf);
    }

    boolean isDateBefore(LocalDateTime ldt1, LocalDateTime ldt2) {
        // get only date, without time
        LocalDate ld1 = ldt1.toLocalDate();
        LocalDate ld2 = ldt2.toLocalDate();
        return ld1.isBefore(ld2);
    }

    void keep(String datePattern, int age, int keep) {
        String searchSplit = datePattern.startsWith("/") ? datePattern.replaceFirst("/", "") : datePattern;
        String[] search;
        int posStart = searchSplit.indexOf("%d{");
        int posEnd = searchSplit.indexOf("}", posStart) + 1;
        if (posStart >= 0 && posEnd > posStart) {
            String dateFormatSubStr = searchSplit.substring(posStart, posEnd);
            searchSplit = searchSplit.replace(dateFormatSubStr, "@#@#@#");
            search = searchSplit.split("@#@#@#");
        } else {
            search = new String[]{searchSplit};
        }


        File[] rollingFiles = BASE_DIR.listFiles(new FilenameFilter() {
            /**
             * Tests if a specified file should be included in a file list.
             *
             * @param dir  the directory in which the file was found.
             * @param name the name of the file.
             * @return {@code true} if and only if the name should be
             * included in the file list; {@code false} otherwise.
             */
            @Override
            public boolean accept(File dir, String name) {
                for (String s : search) {
                    if (!name.contains(s)) return false;
                }
                return true;
            }
        });

        if (rollingFiles.length > keep) {
            //sort files at date
            Arrays.sort(rollingFiles, Comparator.comparingLong(File::lastModified).reversed());

            //delete all file's they're over rolling

            for (int i = keep; i < rollingFiles.length; i++) {
                rollingFiles[i].delete();
            }
        }
    }


    @Override
    public String toString() {
        return super.toString();
    }

    static String getFileNameFromPattern(String pattern) {
        int posStart = pattern.indexOf("%d{");
        int posEnd = pattern.indexOf("}", posStart) + 1;
        if (posStart >= 0 && posEnd > posStart) {
            String dateFormatSubStr = pattern.substring(posStart + 3, posEnd - 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatSubStr);
            String dateStr = formatter.format(LocalDate.now().minusDays(1));// file is from yesterday
            pattern = pattern.replace("%d{" + dateFormatSubStr + "}", dateStr);
        }
        return pattern;
    }

    public void zipTo(File targetFileHandle) throws IOException {
        FileOutputStream fos = new FileOutputStream(targetFileHandle);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        zipFile(this.HANDLE, this.HANDLE.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
//        if (fileToZip.isHidden()) {
//            return;
//        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

}
