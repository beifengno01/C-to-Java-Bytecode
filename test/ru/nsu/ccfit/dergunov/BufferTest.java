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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class BufferTest
{
    @Test
    public void getCharTest() throws IOException
    {
        Reader reader = new StringReader("qwerty");

        Buffer buffer = new Buffer(reader, 1);

        char c = buffer.getChar();
        assertEquals(c, 'q');

        c = buffer.getChar();
        assertEquals(c, 'w');

        c = buffer.getChar();
        assertEquals(c, 'e');

        c = buffer.getChar();
        assertEquals(c, 'r');

        c = buffer.getChar();
        assertEquals(c, 't');

        c = buffer.getChar();
        assertEquals(c, 'y');

        try
        {
            buffer.getChar();
            assertEquals(true, false);
        }
        catch (IOException ex)
        {

        }
    }

    @Test
    public void pickCharTest() throws IOException
    {
        Reader reader = new StringReader("qwerty");

        Buffer buffer = new Buffer(reader, 1);

        char c = buffer.peekChar();
        assertEquals(c, 'q');
        buffer.getChar();

        c = buffer.peekChar();
        assertEquals(c, 'w');
        buffer.getChar();

        c = buffer.peekChar();
        assertEquals(c, 'e');
        buffer.getChar();

        c = buffer.peekChar();
        assertEquals(c, 'r');
        buffer.getChar();

        c = buffer.peekChar();
        assertEquals(c, 't');
        buffer.getChar();

        c = buffer.peekChar();
        assertEquals(c, 'y');
        buffer.getChar();

        try
        {
            buffer.peekChar();
            assertEquals(true, false);
        }
        catch (IOException ex)
        {

        }
    }

    @Test
    public void pickCharTwoTest() throws IOException
    {
        Reader reader = new StringReader("qwerty");

        Buffer buffer = new Buffer(reader, 2);

        char c = buffer.peekCharTwo();
        assertEquals(c, 'w');
        buffer.getChar();

        c = buffer.peekCharTwo();
        assertEquals(c, 'e');
        buffer.getChar();

        c = buffer.peekCharTwo();
        assertEquals(c, 'r');
        buffer.getChar();

        c = buffer.peekCharTwo();
        assertEquals(c, 't');
        buffer.getChar();

        c = buffer.peekCharTwo();
        assertEquals(c, 'y');
        buffer.getChar();

        c = buffer.peekCharTwo();
        assertEquals(c, 0);
        buffer.getChar();

        try
        {
            buffer.peekCharTwo();
            assertEquals(true, false);
        }
        catch(IOException ex)
        {

        }
    }
}
