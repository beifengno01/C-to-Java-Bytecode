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

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class LexerTest
{
    @Test
    public void getTokenTest()
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
        Token token;

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("int" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("main" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.OPENBRACKET ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.CLOSEBRACKET ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.OPENBRACE ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("int" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("a" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.EQUALS ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NUMBER ,token.getTokenType());
        assertEquals("5" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.SEMICOLON ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("int" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("b" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.EQUALS ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NUMBER ,token.getTokenType());
        assertEquals("6" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.SEMICOLON ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("a" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.EQUALS ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("a" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.PLUS ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("b" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.MINUS ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("b" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.DIVISION ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("a" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.MULTIPLICATION ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("b" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.SEMICOLON ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NAME ,token.getTokenType());
        assertEquals("return" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.NUMBER ,token.getTokenType());
        assertEquals("0" ,token.getValue());

        token = lexer.getToken();
        assertEquals(Token.TokenType.SEMICOLON ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.CLOSEBRACE ,token.getTokenType());

        token = lexer.getToken();
        assertEquals(Token.TokenType.END ,token.getTokenType());
    }
}
