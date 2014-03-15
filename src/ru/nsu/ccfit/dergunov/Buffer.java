/*
 * Computer Graphics: Puzzle
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

import java.io.IOException;
import java.io.Reader;

public class Buffer
{
    public Buffer(Reader r, int bufSize)
    {
        reader = r;
        buf = new char[bufSize + 1];
        size = bufSize;
    }

    public char getChar() throws IOException
    {
        read();

        ++returned;

        return buf[returned - 1];
    }

    public char pickChar() throws IOException
    {
        read();

        return buf[returned];
    }

    public char pickCharTwo() throws IOException
    {
        read();

        if(returned + 1 == readed)
        {
            int r = reader.read(buf, size, 1);

            if(r == 1)
            {
                ++readed;
            }
            else
            {
                return 0;
            }
        }

        return buf[returned + 1];
    }

    private void read() throws IOException
    {
        if(readed == returned)
        {
            readed = reader.read(buf, 0, size);
            returned = 0;

            if(readed == 0)
            {
                throw new IOException();
            }
        }
    }

    private Reader reader = null;
    private char[] buf = null;
    private int size = 0;
    private int readed = 0;
    private int returned = 0;
}
