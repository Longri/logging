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

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

public class StringBuilderPrintStream extends PrintStream {

    StringBuilder sb = new StringBuilder();

    public StringBuilderPrintStream() throws IOException {
        super(Files.createTempFile("tmp", ".file").toFile());
    }

    @Override
    public void print(String s) {
        synchronized (sb) {
            sb.append(s);
        }
    }

    @Override
    public void println(String s) {
        synchronized (sb) {
            sb.append(s).append("\n");
        }
    }

    @Override
    public void println(Object x) {
        synchronized (sb) {
            sb.append(x.toString()).append("\n");
        }
    }

    @Override
    public String toString() {
        synchronized (sb) {
            return sb.toString();
        }
    }

    public void clear() {
        synchronized (sb) {
            sb = new StringBuilder();
        }

    }
}
