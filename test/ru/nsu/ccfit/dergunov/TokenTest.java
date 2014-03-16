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

import static org.junit.Assert.assertEquals;

public class TokenTest
{
    @Test
    public void getValueTest()
    {
        Token token = new Token(Token.TokenType.NAME);

        token.setValue("name1");

        assertEquals("name1", token.getValue());
    }

    @Test
    public void getTokenTypeTest()
    {
        Token token = new Token(Token.TokenType.NAME);

        assertEquals(Token.TokenType.NAME, token.getTokenType());
    }
}
