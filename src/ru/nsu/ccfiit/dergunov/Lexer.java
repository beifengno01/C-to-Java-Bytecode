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

package ru.nsu.ccfiit.dergunov;

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
                Token token = new Token(Token.TokenType.NAME);

                StringBuilder name = new StringBuilder();

                name.append(c);

                try
                {
                    while(true)
                    {
                        c = buffer.pickChar();

                        if(Character.isAlphabetic(c) || Character.isDigit(c))
                        {
                            buffer.getChar();
                            name.append(c);
                        }
                        else if(c == ' ')
                        {
                            token.setValue(name.toString());
                            return token;
                        }
                        else
                        {
                            return endToken;
                        }
                    }
                }
                catch(IOException ex)
                {
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
                        c = buffer.pickChar();

                        if(Character.isDigit(c))
                        {
                            buffer.getChar();
                            number.append(c);
                        }
                        else if(c == ' ')
                        {
                            token.setValue(number.toString());
                            return token;
                        }
                        else
                        {
                            return endToken;
                        }
                    }
                }
                catch(IOException ex)
                {
                    token.setValue(number.toString());
                    return token;
                }
            }

            switch (c)
            {
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
            char c = buffer.pickChar();

            if(c == ' ' || c == '\n' || c == '\r')
            {
                buffer.getChar();
                continue;
            }

            if(c == '/')
            {
                char n = buffer.pickCharTwo();

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
                            n = buffer.pickChar();
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

    private Token openBracketToken = new Token(Token.TokenType.OPENBRACKET);
    private Token closeBracketToken = new Token(Token.TokenType.CLOSEBRACKET);
    private Token multiplicationToken = new Token(Token.TokenType.MULTIPLICATION);
    private Token divisionToken = new Token(Token.TokenType.DIVISION);
    private Token plusToken = new Token(Token.TokenType.PLUS);
    private Token minusToken = new Token(Token.TokenType.MINUS);
    private Token semicolonToken = new Token(Token.TokenType.SEMICOLON);
    private Token openBraceToken = new Token(Token.TokenType.OPENBRACE);
    private Token closeBraceToken = new Token(Token.TokenType.CLOSEBRACE);
    private Token endToken = new Token(Token.TokenType.END);

    private Buffer buffer;
}