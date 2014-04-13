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

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class ParserTest
{
    @Test
    public void parseExpressionTest()
    {
        Reader reader = new StringReader("//Hello World!\n" +
                "int main()\n" +
                "{\n" +
                "int a = 5;\n" +
                "int b = 6;\n" +
                "a = a + b - b / a * b;\n" +
                "/*comment\n" +
                "comment*/\n" +
                "return 0;\n" +
                "}\n");
        Buffer buffer = new Buffer(reader, 256);
        Lexer lexer = new Lexer(buffer);
        Parser parser = new Parser(lexer);

        try
        {
            ParseTreeItem tree = parser.parseProgram();
            assertEquals(ParseTreeItem.ParseTreeItemType.PROGRAM, tree.type);
        }
        catch (Exception ex)
        {
            assertEquals(true, false);
        }
    }
}
