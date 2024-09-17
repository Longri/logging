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
package org.slf4j;

import de.longri.logging.LongriLogger;
import de.longri.logging.OutputChoice;
import de.longri.logging.StringBuilderPrintStream;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static de.longri.logging.LongriLogger.CONFIG_PARAMS;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SLF4J_LoggerTest {

    @BeforeAll
    static void init() {
        Logger log = LoggerFactory.getLogger("InitTest");

        synchronized (CONFIG_PARAMS) {
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);
        }
    }

    @BeforeEach
    void beforeEach() {
        synchronized (CONFIG_PARAMS) {
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);
        }
    }


    @Test
    @Order(1)
    void isLogLevelEnabled() {

        //trace
        System.setProperty("longriLogger.logLevel:LevelTest", "trace");
        Logger log = LoggerFactory.getLogger("LevelTest");
        assertTrue(log.isTraceEnabled());
        assertTrue(log.isDebugEnabled());
        assertTrue(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());

        //debug
        System.setProperty("longriLogger.logLevel:LevelTest", "debug");
        log = LoggerFactory.getLogger("LevelTest");
        assertTrue(log.isTraceEnabled());
        assertTrue(log.isDebugEnabled());
        assertTrue(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());
        LoggerFactory.reset();
        log = LoggerFactory.getLogger("LevelTest");
        assertFalse(log.isTraceEnabled());
        assertTrue(log.isDebugEnabled());
        assertTrue(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());

        //info
        System.setProperty("longriLogger.logLevel:LevelTest", "info");
        log = LoggerFactory.getLogger("LevelTest");
        assertFalse(log.isTraceEnabled());
        assertTrue(log.isDebugEnabled());
        assertTrue(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());
        LoggerFactory.reset();
        log = LoggerFactory.getLogger("LevelTest");
        assertFalse(log.isTraceEnabled());
        assertFalse(log.isDebugEnabled());
        assertTrue(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());

        //warn
        System.setProperty("longriLogger.logLevel:LevelTest", "warn");
        log = LoggerFactory.getLogger("LevelTest");
        assertFalse(log.isTraceEnabled());
        assertFalse(log.isDebugEnabled());
        assertTrue(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());
        LoggerFactory.reset();
        log = LoggerFactory.getLogger("LevelTest");
        assertFalse(log.isTraceEnabled());
        assertFalse(log.isDebugEnabled());
        assertFalse(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());

        //error
        System.setProperty("longriLogger.logLevel:LevelTest", "error");
        log = LoggerFactory.getLogger("LevelTest");
        assertFalse(log.isTraceEnabled());
        assertFalse(log.isDebugEnabled());
        assertFalse(log.isInfoEnabled());
        assertTrue(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());
        LoggerFactory.reset();
        log = LoggerFactory.getLogger("LevelTest");
        assertFalse(log.isTraceEnabled());
        assertFalse(log.isDebugEnabled());
        assertFalse(log.isInfoEnabled());
        assertFalse(log.isWarnEnabled());
        assertTrue(log.isErrorEnabled());

    }

    @Test
    @Order(2)
    void getName() {
        Logger log = LoggerFactory.getLogger("GetNameTest");
        assertEquals("GetNameTest", log.getName());
    }

    @Test
    @Order(3)
    void trace() throws IOException, InterruptedException {
        synchronized (CONFIG_PARAMS) {
            Thread.sleep(1000);
            OutputChoice debugChoice = new OutputChoice();
            StringBuilderPrintStream sbs = new StringBuilderPrintStream();
            debugChoice.add(sbs);

            LoggerFactory.reset();

            System.setProperty("longriLogger.logLevel:TraceTest", "trace");
            System.setProperty(LongriLogger.SHOW_DATE_TIME_KEY, "false");
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_THREAD_NAME_KEY, true);
            CONFIG_PARAMS.setProperty(LongriLogger.LEVEL_IN_BRACKETS_KEY, false);

            Logger log = LoggerFactory.getLogger("TraceTest");
            CONFIG_PARAMS.setOutputChoice(debugChoice);

            log.trace("TraceString");
            assertEquals("[Test worker] TRACE TraceTest - TraceString\n", sbs.toString());
            sbs.clear();

            Object o = "Object";
            log.trace("Trace {} String", o);
            assertEquals("[Test worker] TRACE TraceTest - Trace Object String\n", sbs.toString());
            sbs.clear();

            StringBuilder o2 = new StringBuilder("StringBuilder");
            log.trace("Trace {} String {}", o, o2);
            assertEquals("[Test worker] TRACE TraceTest - Trace Object String StringBuilder\n", sbs.toString());
            sbs.clear();

            Object o3 = new Object();
            log.trace("Trace {} String {} | {}", o, o2, o3);
            assertEquals("[Test worker] TRACE TraceTest - Trace Object String StringBuilder | " + o3 + "\n", sbs.toString());
            sbs.clear();

            Throwable throwable = new RuntimeException("RuntimeEx", new Exception("Cause"));
            log.trace("Trace Throwable", throwable);
            String expected = "[Test worker] TRACE TraceTest - Trace Throwable";
            assertTrue(sbs.toString().startsWith(expected));
            Thread.sleep(1000);
        }
    }

    @Test
    @Order(4)
    void debug() throws IOException, InterruptedException {
        synchronized (CONFIG_PARAMS) {
            Thread.sleep(1000);
            OutputChoice debugChoice = new OutputChoice();
            StringBuilderPrintStream sbs = new StringBuilderPrintStream();
            debugChoice.add(sbs);
            System.setProperty("longriLogger.logLevel:DebugTest", "debug");
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_DATE_TIME_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_THREAD_NAME_KEY, true);
            CONFIG_PARAMS.setProperty(LongriLogger.LEVEL_IN_BRACKETS_KEY, false);
            Thread.currentThread().setName("Test worker");
            LoggerFactory.reset();
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);
            Logger log = LoggerFactory.getLogger("DebugTest");
            CONFIG_PARAMS.setOutputChoice(debugChoice);

            log.debug("debugString");
            assertEquals("[Test worker] DEBUG DebugTest - debugString\n", sbs.toString());
            sbs.clear();

            Object o = "Object";
            log.debug("debug {} String", o);
            assertEquals("[Test worker] DEBUG DebugTest - debug Object String\n", sbs.toString());
            sbs.clear();

            StringBuilder o2 = new StringBuilder("StringBuilder");
            log.debug("debug {} String {}", o, o2);
            assertEquals("[Test worker] DEBUG DebugTest - debug Object String StringBuilder\n", sbs.toString());
            sbs.clear();

            Object o3 = new Object();
            log.debug("debug {} String {} | {}", o, o2, o3);
            assertEquals("[Test worker] DEBUG DebugTest - debug Object String StringBuilder | " + o3 + "\n", sbs.toString());
            sbs.clear();

            Throwable throwable = new RuntimeException("RuntimeEx", new Exception("Cause"));
            log.debug("debug Throwable", throwable);
            String expected = "[Test worker] DEBUG DebugTest - debug Throwable";
            assertTrue(sbs.toString().startsWith(expected));
            Thread.sleep(1000);
        }
    }

    @Test
    @Order(5)
    void info() throws IOException, InterruptedException {

        synchronized (CONFIG_PARAMS) {
            Thread.sleep(1000);
            OutputChoice debugChoice = new OutputChoice();
            StringBuilderPrintStream sbs = new StringBuilderPrintStream();
            debugChoice.add(sbs);
            System.setProperty("longriLogger.logLevel:InfoTest", "trace");
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_DATE_TIME_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_THREAD_NAME_KEY, true);
            CONFIG_PARAMS.setProperty(LongriLogger.LEVEL_IN_BRACKETS_KEY, false);
            Thread.currentThread().setName("Test worker");
            LoggerFactory.reset();
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);

            Logger log = LoggerFactory.getLogger("InfoTest");
            CONFIG_PARAMS.setOutputChoice(debugChoice);

            log.info("TraceString");
            assertEquals("[Test worker] INFO InfoTest - TraceString\n", sbs.toString());
            sbs.clear();

            Object o = "Object";
            log.info("Trace {} String", o);
            assertEquals("[Test worker] INFO InfoTest - Trace Object String\n", sbs.toString());
            sbs.clear();

            StringBuilder o2 = new StringBuilder("StringBuilder");
            log.info("Trace {} String {}", o, o2);
            assertEquals("[Test worker] INFO InfoTest - Trace Object String StringBuilder\n", sbs.toString());
            sbs.clear();

            Object o3 = new Object();
            log.info("Trace {} String {} | {}", o, o2, o3);
            assertEquals("[Test worker] INFO InfoTest - Trace Object String StringBuilder | " + o3 + "\n", sbs.toString());
            sbs.clear();

            Throwable throwable = new RuntimeException("RuntimeEx", new Exception("Cause"));
            log.info("Trace Throwable", throwable);
            String expected = "[Test worker] INFO InfoTest - Trace Throwable";
            assertTrue(sbs.toString().startsWith(expected));
            Thread.sleep(1000);
        }
    }

    @Test
    @Order(6)
    void warn() throws IOException, InterruptedException {
        synchronized (CONFIG_PARAMS) {
            Thread.sleep(1000);
            OutputChoice debugChoice = new OutputChoice();
            StringBuilderPrintStream sbs = new StringBuilderPrintStream();
            debugChoice.add(sbs);
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_DATE_TIME_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_THREAD_NAME_KEY, true);
            CONFIG_PARAMS.setProperty(LongriLogger.LEVEL_IN_BRACKETS_KEY, false);
            Thread.currentThread().setName("Test worker");
            System.setProperty("longriLogger.logLevel:WarnTest", "warn");
            LoggerFactory.reset();
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);

            Logger log = LoggerFactory.getLogger("WarnTest");
            CONFIG_PARAMS.setOutputChoice(debugChoice);

            log.warn("WarnString");
            assertEquals("[Test worker] WARN WarnTest - WarnString\n", sbs.toString());
            sbs.clear();

            Object o = "Object";
            log.warn("Trace {} String", o);
            assertEquals("[Test worker] WARN WarnTest - Trace Object String\n", sbs.toString());
            sbs.clear();

            StringBuilder o2 = new StringBuilder("StringBuilder");
            log.warn("Trace {} String {}", o, o2);
            assertEquals("[Test worker] WARN WarnTest - Trace Object String StringBuilder\n", sbs.toString());
            sbs.clear();

            Object o3 = new Object();
            log.warn("Trace {} String {} | {}", o, o2, o3);
            assertEquals("[Test worker] WARN WarnTest - Trace Object String StringBuilder | " + o3 + "\n", sbs.toString());
            sbs.clear();

            Throwable throwable = new RuntimeException("RuntimeEx", new Exception("Cause"));
            log.warn("Trace Throwable", throwable);
            String expected = "[Test worker] WARN WarnTest - Trace Throwable";
            assertTrue(sbs.toString().startsWith(expected));
            Thread.sleep(1000);
        }
    }

    @Test
    @Order(7)
    void error() throws IOException, InterruptedException {
        synchronized (CONFIG_PARAMS) {
            Thread.sleep(1000);
            OutputChoice debugChoice = new OutputChoice();
            StringBuilderPrintStream sbs = new StringBuilderPrintStream();
            debugChoice.add(sbs);
            System.setProperty("longriLogger.logLevel:ErrorTest", "error");
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_DATE_TIME_KEY, false);
            CONFIG_PARAMS.setProperty(LongriLogger.SHOW_THREAD_NAME_KEY, true);
            CONFIG_PARAMS.setProperty(LongriLogger.LEVEL_IN_BRACKETS_KEY, false);
            Thread.currentThread().setName("Test worker");
            LoggerFactory.reset();
            CONFIG_PARAMS.setProperty(LongriLogger.LOG_COLORFUL_KEY, false);

            Logger log = LoggerFactory.getLogger("ErrorTest");
            CONFIG_PARAMS.setOutputChoice(debugChoice);

            log.error("ErrorString");
            assertEquals("[Test worker] ERROR ErrorTest - ErrorString\n", sbs.toString());
            sbs.clear();

            Object o = "Object";
            log.error("Error {} String", o);
            assertEquals("[Test worker] ERROR ErrorTest - Error Object String\n", sbs.toString());
            sbs.clear();

            StringBuilder o2 = new StringBuilder("StringBuilder");
            log.error("Error {} String {}", o, o2);
            assertEquals("[Test worker] ERROR ErrorTest - Error Object String StringBuilder\n", sbs.toString());
            sbs.clear();

            Object o3 = new Object();
            log.error("Error {} String {} | {}", o, o2, o3);
            assertEquals("[Test worker] ERROR ErrorTest - Error Object String StringBuilder | " + o3 + "\n", sbs.toString());
            sbs.clear();

            Throwable throwable = new RuntimeException("RuntimeEx", new Exception("Cause"));
            log.error("Error Throwable", throwable);
            String expected = "[Test worker] ERROR ErrorTest - Error Throwable";
            assertTrue(sbs.toString().startsWith(expected));
            Thread.sleep(1000);
        }
    }

    @Test
    @Order(8)
    void initializationTest() {
        Logger logger = LoggerFactory.getLogger("Test");
        assertInstanceOf(LongriLogger.class, logger);
    }


}