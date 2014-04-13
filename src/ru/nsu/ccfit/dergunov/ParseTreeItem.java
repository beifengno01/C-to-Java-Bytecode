/*
 * C to Java Bytecode
 * Copyright (C) 2014  Alexander Dergunov
 * dergunov.alexander@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.nsu.ccfit.dergunov;

import java.util.ArrayList;

public class ParseTreeItem
{
    public ParseTreeItemType type;
    public String value;
    public ArrayList<ParseTreeItem> childrens = new ArrayList<>();

    public enum ParseTreeItemType
    {
        PROGRAM,
        METHOD,
        TYPE,
        ARGLIST,
        BODY,
        DEFINE,
        INITIALIZE,
        PRINT,
        RETURN,
        MULTIPLICATION,
        DIVISION,
        PLUS,
        MINUS,
        NUMBER,
        NAME
    }
}
