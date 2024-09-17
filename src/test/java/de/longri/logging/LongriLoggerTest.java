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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


public class LongriLoggerTest {

    static {
        //initial Logger
        try {
            InputStream inputStream;
            inputStream = LongriLoggerTest.class.getClassLoader().getResourceAsStream("logger/testLongriLogger.properties");
            LongriLoggerConfiguration.setConfigurationFile(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static String A_KEY = LongriLogger.LOG_KEY_PREFIX + "a";
    static PrintStream original = System.out;
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PrintStream replacement = new PrintStream(bout);

    @BeforeAll
    static public void before() {
        System.setProperty(A_KEY, "info");
    }

    @AfterAll
    static public void after() {
        System.clearProperty(A_KEY);
        System.clearProperty(LongriLogger.CACHE_OUTPUT_STREAM_STRING_KEY);
        System.clearProperty(LongriLogger.SHOW_THREAD_ID_KEY);
        System.clearProperty(LongriLogger.SHOW_THREAD_NAME_KEY);
        System.setErr(original);
    }

    @Test
    public void offLevel() {
        System.setProperty(A_KEY, "off");
        LongriLogger.init();
        LongriLogger longriLogger = new LongriLogger("a");
        assertEquals("off", longriLogger.recursivelyComputeLevelString());
        assertFalse(longriLogger.isErrorEnabled());
    }

    @Test
    public void loggerNameWithNoDots_WithLevel() {
        LongriLogger.init();
        LongriLogger longriLogger = new LongriLogger("a");

        assertEquals("info", longriLogger.recursivelyComputeLevelString());
    }


    @Test
    public void loggerNameWithNoDots_WithNoSetLevel() {
        LongriLogger longriLogger = new LongriLogger("x");
        assertNull(longriLogger.recursivelyComputeLevelString());
    }

    @Test
    public void loggerNameWithOneDot_NoSetLevel() {
        LongriLogger longriLogger = new LongriLogger("x.y");
        assertNull(longriLogger.recursivelyComputeLevelString());
    }

}
