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

import java.io.Reader;
import java.io.StringReader;

public class CtoJavaBytecode
{
    public static void main(String[] args)
    {
        try
        {
            Reader reader = new StringReader("//Hello World!\n" +
                    "int main()\n" +
                    "{\n" +
                    "int a;\n" +
                    "int b;\n" +
                    "a = 5;\n" +
                    "b = 6;\n" +
                    "a = a + b - b / a * b;\n" +
                    "/*comment\n" +
                    "comment*/\n" +
                    "print(a);\n" +
                    "return;\n" +
                    "}\n");
            Buffer buffer = new Buffer(reader, 256);
            Lexer lexer = new Lexer(buffer);
            Parser parser = new Parser(lexer);
            ASCIICreator creator = new ASCIICreator(parser.parseProgram());
            System.out.println(creator.create());
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
