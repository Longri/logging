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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static de.longri.logging.LongriLogger.CONFIG_PARAMS;
import static org.junit.jupiter.api.Assertions.*;

class LongriLoggerConfigurationTest {


    @Test
    void init() throws IOException {
        synchronized (CONFIG_PARAMS) {
            // test with rolling log files
            File basDir = new File("./Test/logs");

            //delete this Folder and check the creation
            deleteDirectoryRecursion(basDir);
            File currentLogFile = new File("./Test/logs/logTest.log");
            assertFalse(currentLogFile.exists());

            // create some logfiles for check file rowling


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            String logDate = formatter.format(LocalDateTime.now().minusDays(1));
            String logDate1 = formatter.format(LocalDateTime.now());
            String logDate2 = formatter.format(LocalDateTime.now().minusDays(3));
            String logDate3 = formatter.format(LocalDateTime.now().minusDays(4));
            String logDate4 = formatter.format(LocalDateTime.now().minusDays(5));


            File file = createFileWithCreationDate("./Test/logs/logTest.log", LocalDateTime.now().minusDays(1));
            Files.writeString(file.toPath(), "TestString", StandardOpenOption.APPEND);
            File file1 = new File("./Test/logs/logTest_" + logDate + ".log.zip");
            File file2 = createFileWithCreationDate("./Test/logs/logTest_" + logDate2 + ".log.zip", LocalDateTime.now().minusDays(3));
            File file3 = createFileWithCreationDate("./Test/logs/logTest_" + logDate3 + ".log.zip", LocalDateTime.now().minusDays(4));
            File file4 = createFileWithCreationDate("./Test/logs/logTest_" + logDate4 + ".log.zip", LocalDateTime.now().minusDays(5));


            InputStream inputStream = LongriLoggerConfigurationTest.class.getClassLoader().getResourceAsStream("logger/testLongriLogger.properties");
            CONFIG_PARAMS.init(inputStream);

            assertTrue(currentLogFile.exists());

            assertTrue(file.exists());
            assertTrue(file1.exists());
            assertTrue(file2.exists());
            assertTrue(file3.exists());
            assertFalse(file4.exists());

            String[] list = basDir.list();

            assertEquals(4, list.length);
            assertFalse(new File("./Test/logs/GUI_3.log.zip").exists());

            //write int Logfile and check file
            for (PrintStream stream : CONFIG_PARAMS.outputChoice) {
                stream.print("TestString@PrintStream");
                stream.flush();
            }

            String content = Files.readString(currentLogFile.toPath());
            assertEquals("TestString@PrintStream", content);


            //check zipped File content. delete current log file, extract and read
            currentLogFile.delete();
            unzip(file1.getAbsolutePath(), "./Test/logs/");

            assertTrue(currentLogFile.exists());
            assertEquals("TestString", Files.readString(currentLogFile.toPath()));


            //re initial Logger
            LongriLoggerConfiguration.setConfigurationFile(LongriLoggerConfigurationTest.class.getClassLoader().getResourceAsStream("logger/testLongriLogger.properties"));
            LongriLoggerFactory factory = ((LongriLoggerFactory) LoggerFactory.getILoggerFactory());
            factory.reset();
            LongriLoggerInit.init();
            content = Files.readString(currentLogFile.toPath());
            assertEquals("TestString", content);
        }
    }

    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void deleteDirectoryRecursion(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete " + file);
            }
        }
    }

    public File createFileWithCreationDate(String filePath, LocalDateTime creationDate) throws IOException {
        File newFile = new File(filePath);
        newFile.getParentFile().mkdirs();
        if (!newFile.exists()) newFile.createNewFile();
        BasicFileAttributeView attributes = Files.getFileAttributeView(newFile.toPath(), BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.toInstant(ZoneOffset.UTC).toEpochMilli());
        attributes.setTimes(time, time, time);
        return newFile;
    }
}