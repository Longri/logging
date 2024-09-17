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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StringPrintStream extends PrintStream {

    public static final String LINE_SEP = System.getProperty("line.separator");
    PrintStream other;
    List<String> stringList = new ArrayList<>();

    public StringPrintStream(PrintStream ps) {
        super(ps);
        other = ps;
    }

    public void print(String s) {
        other.print(s);
        stringList.add(s);
    }

    public void println(String s) {
        other.println(s);
        stringList.add(s);
    }

    public void println(Object o) {
        other.println(o);
        stringList.add(o.toString());
    }
}