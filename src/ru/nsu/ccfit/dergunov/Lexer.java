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

import java.io.IOException;

public class Lexer
{
    public Lexer(Buffer b)
    {
        buffer = b;
    }

    public Token getToken()
    {
        try
        {
            removeComments();

            char c = buffer.getChar();

            if(Character.isAlphabetic(c))
            {
                StringBuilder name = new StringBuilder();

                name.append(c);

                try
                {
                    while(true)
                    {
                        c = buffer.peekChar();

                        if(Character.isAlphabetic(c) || Character.isDigit(c))
                        {
                            buffer.getChar();
                            name.append(c);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                catch(IOException ex)
                {

                }

                if(name.toString().equals("print"))
                {
                    return printToken;
                }
                else if(name.toString().equals("return"))
                {
                    return returnToken;
                }
                else if(name.toString().equals("int"))
                {
                    return intToken;
                }
                else if(name.toString().equals("double"))
                {
                    return doubleToken;
                }
                else if(name.toString().equals("void"))
                {
                    return voidToken;
                }
                else
                {
                    Token token = new Token(Token.TokenType.NAME);
                    token.setValue(name.toString());
                    return token;
                }
            }

            if(Character.isDigit(c))
            {
                Token token = new Token(Token.TokenType.NUMBER);

                StringBuilder number = new StringBuilder();

                number.append(c);

                try
                {
                    while(true)
                    {
                        c = buffer.peekChar();

                        if(Character.isDigit(c))
                        {
                            buffer.getChar();
                            number.append(c);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                catch(IOException ex)
                {

                }

                token.setValue(number.toString());
                return token;
            }

            switch (c)
            {
                case ',':
                {
                    return commaToken;
                }
                case '(':
                {
                    return openBracketToken;
                }
                case ')':
                {
                    return closeBracketToken;
                }
                case '*':
                {
                    return multiplicationToken;
                }
                case '/':
                {
                    return divisionToken;
                }
                case '+':
                {
                    return plusToken;
                }
                case '-':
                {
                    return minusToken;
                }
                case ';':
                {
                    return semicolonToken;
                }
                case '{':
                {
                    return openBraceToken;
                }
                case '}':
                {
                    return closeBraceToken;
                }
                case '=':
                {
                    return equalsToken;
                }
            }
        }
        catch (IOException ex)
        {
            return endToken;
        }

        return endToken;
    }

    private void removeComments() throws IOException
    {
        while(true)
        {
            char c = buffer.peekChar();

            if(c == ' ' || c == '\n' || c == '\r')
            {
                buffer.getChar();
                continue;
            }

            if(c == '/')
            {
                char n = buffer.peekCharTwo();

                if(n == '/')
                {
                    buffer.getChar();
                    buffer.getChar();

                    while(true)
                    {
                        c = buffer.getChar();

                        if(c == '\n')
                        {
                            break;
                        }
                    }
                    continue;
                }
                else if(n == '*')
                {
                    buffer.getChar();
                    buffer.getChar();

                    while(true)
                    {
                        c = buffer.getChar();

                        if(c == '*')
                        {
                            n = buffer.peekChar();
                            if(n == '/')
                            {
                                buffer.getChar();
                                break;
                            }
                        }
                    }
                    continue;
                }
            }

            break;
        }
    }

    private Token commaToken = new Token(Token.TokenType.COMMA);
    private Token openBracketToken = new Token(Token.TokenType.OPENBRACKET);
    private Token closeBracketToken = new Token(Token.TokenType.CLOSEBRACKET);
    private Token multiplicationToken = new Token(Token.TokenType.MULTIPLICATION);
    private Token divisionToken = new Token(Token.TokenType.DIVISION);
    private Token plusToken = new Token(Token.TokenType.PLUS);
    private Token minusToken = new Token(Token.TokenType.MINUS);
    private Token semicolonToken = new Token(Token.TokenType.SEMICOLON);
    private Token openBraceToken = new Token(Token.TokenType.OPENBRACE);
    private Token closeBraceToken = new Token(Token.TokenType.CLOSEBRACE);
    private Token equalsToken = new Token(Token.TokenType.EQUALS);
    private Token printToken = new Token(Token.TokenType.PRINT);
    private Token returnToken = new Token(Token.TokenType.RETURN);
    private Token intToken = new Token(Token.TokenType.INT);
    private Token doubleToken = new Token(Token.TokenType.DOUBLE);
    private Token voidToken = new Token(Token.TokenType.VOID);
    private Token endToken = new Token(Token.TokenType.END);

    private Buffer buffer;
}
