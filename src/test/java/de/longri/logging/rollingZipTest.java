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

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static de.longri.logging.LongriLoggerConfigurationTest.deleteDirectoryRecursion;
import static org.junit.jupiter.api.Assertions.*;

public class rollingZipTest {

    static final String CRLF_LF = "\n";

    @Test
    void zipTo() throws IOException {

        String expected = "Execution optimizations have been disabled for 1 invalid unit(s) of work during this build to ensure correctness." + CRLF_LF +
                "Please consult deprecation warnings for more details." + CRLF_LF +
                "BUILD SUCCESSFUL in 381ms" + CRLF_LF;


        RollingFileHandle lfth = new RollingFileHandle(new File("./Test/zipTest/"), "testFile.txt");
        assertTrue(lfth.HANDLE.exists());
        assertEquals(expected, Files.readString(lfth.HANDLE.toPath(), StandardCharsets.UTF_8));

        File zfth = new File("./Test/zipTest/testFile.zip");
        assertTrue(zfth.delete());
        assertFalse(zfth.exists());
        lfth.zipTo(zfth);
        assertTrue(zfth.exists());

        //extract and check

        File extractFolder = new File("./Test/zipTest/extract/");
        File extractFile = new File("./Test/zipTest/extract/testFile.txt");
        deleteDirectoryRecursion(extractFolder);
        assertFalse(extractFile.exists());
        unzip(zfth, extractFolder);
        assertTrue(extractFile.exists());
        assertEquals(expected, Files.readString(extractFile.toPath(), StandardCharsets.UTF_8));
    }

    public static void unzip(File zipFilePath, File destDirFile) {
        String destDir = destDirFile.getAbsolutePath();
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


}
