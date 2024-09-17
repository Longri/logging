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

public class AnsiColor {

    public final String ansiString;
    public final int R;
    public final int G;
    public final int B;

    public AnsiColor(String ansiString, int r, int g, int b) {
        this.ansiString = ansiString;
        this.R = r;
        this.G = g;
        this.B = b;
    }

    @Override
    public String toString() {
        return this.ansiString;
    }

    public static final AnsiColor D_RESET = new AnsiColor("\u001B[0m", 184, 186, 192);
    public static final AnsiColor D_ERROR = new AnsiColor("\u001B[31m", 237, 85, 101);
    public static final AnsiColor D_WARN = new AnsiColor("\u001B[33m", 148, 127, 0);
    public static final AnsiColor D_INFO = new AnsiColor("\u001B[36m", 49, 161, 161);
    public static final AnsiColor D_TRACE = new AnsiColor("\u001B[32m", 49, 161, 161);

    public static final AnsiColor B_RESET = new AnsiColor("\u001B[0m", 45, 45, 45);
    public static final AnsiColor B_ERROR = new AnsiColor("\u001B[31m", 182, 0, 0);
    public static final AnsiColor B_WARN = new AnsiColor("\u001B[33m", 158, 117, 0);
    public static final AnsiColor B_INFO = new AnsiColor("\u001B[36m", 63, 134, 134);
    public static final AnsiColor B_TRACE = new AnsiColor("\u001B[32m", 2, 179, 117);


    public static AnsiColor getAnsiColor(final String ansi, boolean darkTheme) {
        if (darkTheme) {
            if (D_ERROR.ansiString.equals(ansi)) return D_ERROR;
            if (D_WARN.ansiString.equals(ansi)) return D_WARN;
            if (D_INFO.ansiString.equals(ansi)) return D_INFO;
            if (D_TRACE.ansiString.equals(ansi)) return D_TRACE;
            return D_RESET;
        } else {
            if (B_ERROR.ansiString.equals(ansi)) return B_ERROR;
            if (B_WARN.ansiString.equals(ansi)) return B_WARN;
            if (B_INFO.ansiString.equals(ansi)) return B_INFO;
            if (B_TRACE.ansiString.equals(ansi)) return B_TRACE;
            return B_RESET;
        }
    }

}
