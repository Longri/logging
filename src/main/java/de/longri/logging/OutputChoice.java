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

/**
 * This class encapsulates the user's choice of output target.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class OutputChoice extends ArrayList<PrintStream> {

public OutputChoice(){}


    void add(OutputChoiceType outputChoiceType) {
        if (outputChoiceType == OutputChoiceType.FILE) {
            throw new IllegalArgumentException();
        }
        if (outputChoiceType == OutputChoiceType.SYS_OUT) {
            this.add(System.out);
        } else if (outputChoiceType == OutputChoiceType.SYS_ERR) {
            this.add(System.err);
        }
    }

    public boolean add(PrintStream printStream) {
        return super.add(printStream);
    }
}
