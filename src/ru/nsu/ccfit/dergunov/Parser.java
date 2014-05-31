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

public class Parser
{
    public Parser(Lexer l)
    {
        lexer = l;
    }

    public ParseTreeItem parseProgram() throws Exception
    {
        ParseTreeItem item = new ParseTreeItem();
        item.type = ParseTreeItem.ParseTreeItemType.PROGRAM;

        lastToken = lexer.getToken();

        while(true)
        {
            ParseTreeItem itemMethod = parseMethod();

            if(itemMethod == null)
            {
                break;
            }

            item.childrens.add(itemMethod);
        }

        return item;
    }

    public ParseTreeItem parseMethod() throws Exception
    {
        ParseTreeItem item = new ParseTreeItem();
        item.type = ParseTreeItem.ParseTreeItemType.METHOD;

        ParseTreeItem typeItem = parseType();
        if(typeItem == null)
        {
            return null;
        }
        item.childrens.add(typeItem);

        if(lastToken.getTokenType() == Token.TokenType.NAME)
        {
            item.value = lastToken.getValue();

            lastToken = lexer.getToken();
        }
        else
        {
            throw new Exception("Method name expected");
        }

        if(lastToken.getTokenType() == Token.TokenType.OPENBRACKET)
        {
            lastToken = lexer.getToken();
        }
        else
        {
            throw new Exception("Open bracket expected");
        }

        item.childrens.add(parseArgList());

        if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACKET)
        {
            lastToken = lexer.getToken();
        }
        else
        {
            throw new Exception("Close bracket expected");
        }

        if(lastToken.getTokenType() == Token.TokenType.OPENBRACE)
        {
            lastToken = lexer.getToken();
        }
        else
        {
            throw new Exception("Open brace expected");
        }

        item.childrens.add(parseBody());

        if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACE)
        {
            lastToken = lexer.getToken();
        }
        else
        {
            throw new Exception("Close brace expected");
        }

        return item;
    }

    public ParseTreeItem parseArgList() throws Exception
    {
        ParseTreeItem item = new ParseTreeItem();
        item.type = ParseTreeItem.ParseTreeItemType.ARGLIST;

        while(true)
        {
            if(lastToken.getTokenType() == Token.TokenType.COMMA)
            {
                lastToken = lexer.getToken();
            }

            ParseTreeItem itemType = parseType();
            if(itemType == null)
            {
                break;
            }

            if(lastToken.getTokenType() == Token.TokenType.NAME)
            {
                ParseTreeItem itemName = new ParseTreeItem();
                itemName.type = ParseTreeItem.ParseTreeItemType.NAME;
                itemName.value = lastToken.getValue();

                lastToken = lexer.getToken();

                ParseTreeItem defineItem = new ParseTreeItem();
                defineItem.type = ParseTreeItem.ParseTreeItemType.DEFINE;
                defineItem.childrens.add(itemType);
                defineItem.childrens.add(itemName);

                item.childrens.add(defineItem);
            }
            else
            {
                throw new Exception("Var name expected");
            }
        }

        return item;
    }

    public ParseTreeItem parseBody() throws Exception
    {
        ParseTreeItem item = new ParseTreeItem();
        item.type = ParseTreeItem.ParseTreeItemType.BODY;

        while(true)
        {
            ParseTreeItem itemCommand = parseCommand();

            if(itemCommand == null)
            {
                break;
            }

            item.childrens.add(itemCommand);

            if(lastToken.getTokenType() == Token.TokenType.SEMICOLON)
            {
                lastToken = lexer.getToken();
            }
            //else
            //{
                //throw new Exception("SEMICOLON EXPECTED");
                //TODO: add semicolon check (bug with if, while)
            //}
        }

        return item;
    }

    public ParseTreeItem parseCommand() throws Exception
    {
        ParseTreeItem item = new ParseTreeItem();

        ParseTreeItem itemType = parseType();
        if(itemType != null)
        {
            item.type = ParseTreeItem.ParseTreeItemType.DEFINE;
            item.childrens.add(itemType);

            if(lastToken.getTokenType() == Token.TokenType.NAME)
            {
                ParseTreeItem itemName = new ParseTreeItem();
                itemName.type = ParseTreeItem.ParseTreeItemType.NAME;
                itemName.value = lastToken.getValue();

                item.childrens.add(itemName);

                lastToken = lexer.getToken();

                if(lastToken.getTokenType() == Token.TokenType.EQUALS)
                {
                    lastToken = lexer.getToken();

                    ParseTreeItem itemExpr = parseExpression();

                    if(itemExpr != null)
                    {
                        item.childrens.add(itemExpr);
                    }
                    else
                    {
                        throw new Exception();
                    }
                }
            }
            else
            {
                throw new Exception("Var name expected");
            }
        }
        else
        {
            if(lastToken.getTokenType() == Token.TokenType.NAME)
            {
                item.type = ParseTreeItem.ParseTreeItemType.INITIALIZE;
                item.value = lastToken.getValue();

                lastToken = lexer.getToken();

                if(lastToken.getTokenType() == Token.TokenType.EQUALS)
                {
                    lastToken = lexer.getToken();

                    ParseTreeItem itemExpr = parseExpression();

                    if(itemExpr != null)
                    {
                        item.childrens.add(itemExpr);
                    }
                    else
                    {
                        throw new Exception();
                    }
                }
                else if(lastToken.getTokenType() == Token.TokenType.OPENBRACKET)
                {
                    item.type = ParseTreeItem.ParseTreeItemType.FUNCTION;

                    lastToken = lexer.getToken();

                    while(true)
                    {
                        ParseTreeItem newItem = parseExpression();

                        if(newItem == null)
                        {
                            break;
                        }
                        else
                        {
                            item.childrens.add(newItem);

                            if(lastToken.getTokenType() == Token.TokenType.COMMA)
                            {
                                lastToken = lexer.getToken();
                                if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACKET)
                                {
                                    throw new Exception("Var name expected");
                                }
                            }
                        }
                    }

                    if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACKET)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Close bracket expected");
                    }
                }
            }
            else
            {
                if(lastToken.getTokenType() == Token.TokenType.PRINT)
                {
                    item.type = ParseTreeItem.ParseTreeItemType.PRINT;

                    lastToken = lexer.getToken();

                    if(lastToken.getTokenType() == Token.TokenType.OPENBRACKET)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Open bracket expected");
                    }

                    item.childrens.add(parseExpression());

                    if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACKET)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Close bracket expected");
                    }
                }
                else if(lastToken.getTokenType() == Token.TokenType.WHILE ||
                        lastToken.getTokenType() == Token.TokenType.IF)
                {
                    if(lastToken.getTokenType() == Token.TokenType.WHILE)
                    {
                        item.type = ParseTreeItem.ParseTreeItemType.WHILE;
                    }
                    else
                    {
                        item.type = ParseTreeItem.ParseTreeItemType.IF;
                    }

                    lastToken = lexer.getToken();

                    if(lastToken.getTokenType() == Token.TokenType.OPENBRACKET)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Open bracket expected");
                    }

                    ParseTreeItem itemExpr = parseBooleanExpression();

                    if(itemExpr != null)
                    {
                        item.childrens.add(itemExpr);
                    }
                    else
                    {
                        throw new Exception();
                    }

                    if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACKET)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Close bracket expected");
                    }

                    if(lastToken.getTokenType() == Token.TokenType.OPENBRACE)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Open brace expected");
                    }

                    item.childrens.add(parseBody());

                    if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACE)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Close brace expected");
                    }
                }
                else
                {
                    if(lastToken.getTokenType() == Token.TokenType.RETURN)
                    {
                        item.type = ParseTreeItem.ParseTreeItemType.RETURN;

                        lastToken = lexer.getToken();

                        ParseTreeItem itemExpr = parseExpression();

                        if(itemExpr != null)
                        {
                            item.childrens.add(itemExpr);
                        }
                    }
                    else
                    {
                        item = null;
                    }
                }
            }
        }

        return item;
    }

    public ParseTreeItem parseType() throws Exception
    {
        ParseTreeItem item = new ParseTreeItem();
        item.type = ParseTreeItem.ParseTreeItemType.TYPE;

        switch(lastToken.getTokenType())
        {
            case INT:
            {
                item.value = "int";
                break;
            }
            case DOUBLE:
            {
                item.value = "double";
                break;
            }
            case VOID:
            {
                item.value = "void";
                break;
            }
            default:
            {
                return null;
            }
        }

        lastToken = lexer.getToken();
        return item;
    }

    private ParseTreeItem parseBooleanExpression() throws Exception
    {
        ParseTreeItem item = parseExpression();

        while(true)
        {
            if((lastToken.getTokenType() == Token.TokenType.LESS) ||
                    (lastToken.getTokenType() == Token.TokenType.GREATER) ||
                    (lastToken.getTokenType() == Token.TokenType.LESSEQUALS) ||
                    (lastToken.getTokenType() == Token.TokenType.GREATEREQUALS) ||
                    (lastToken.getTokenType() == Token.TokenType.DOUBLEEQUALS) ||
                    (lastToken.getTokenType() == Token.TokenType.NOTEQUALS))
            {
                ParseTreeItem newItem = new ParseTreeItem();
                if(lastToken.getTokenType() == Token.TokenType.LESS)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.LESS;
                }
                else if(lastToken.getTokenType() == Token.TokenType.GREATER)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.GREATER;
                }
                else if(lastToken.getTokenType() == Token.TokenType.LESSEQUALS)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.LESSEQUALS;
                }
                else if(lastToken.getTokenType() == Token.TokenType.GREATEREQUALS)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.GREATEREQUALS;
                }
                else if(lastToken.getTokenType() == Token.TokenType.DOUBLEEQUALS)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.DOUBLEEQUALS;
                }
                else if(lastToken.getTokenType() == Token.TokenType.NOTEQUALS)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.NOTEQUALS;
                }

                newItem.childrens.add(item);
                item = newItem;

                lastToken = lexer.getToken();
                item.childrens.add(parseExpression());
            }
            else
            {
                break;
            }
        }

        return item;
    }

    private ParseTreeItem parseExpression() throws Exception
    {
        ParseTreeItem item = parseTerm();

        while(true)
        {
            if((lastToken.getTokenType() == Token.TokenType.PLUS) ||
                    (lastToken.getTokenType() == Token.TokenType.MINUS))
            {
                ParseTreeItem newItem = new ParseTreeItem();
                if(lastToken.getTokenType() == Token.TokenType.PLUS)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.PLUS;
                }
                else if(lastToken.getTokenType() == Token.TokenType.MINUS)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.MINUS;
                }

                newItem.childrens.add(item);
                item = newItem;

                lastToken = lexer.getToken();
                item.childrens.add(parseTerm());
            }
            else
            {
                break;
            }
        }

        return item;
    }

    private ParseTreeItem parseTerm() throws Exception
    {
        ParseTreeItem item = parseFactor();

        while(true)
        {
            if((lastToken.getTokenType() == Token.TokenType.MULTIPLICATION) ||
                    (lastToken.getTokenType() == Token.TokenType.DIVISION))
            {
                ParseTreeItem newItem = new ParseTreeItem();
                if(lastToken.getTokenType() == Token.TokenType.MULTIPLICATION)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.MULTIPLICATION;
                }
                else if(lastToken.getTokenType() == Token.TokenType.DIVISION)
                {
                    newItem.type = ParseTreeItem.ParseTreeItemType.DIVISION;
                }

                newItem.childrens.add(item);
                item = newItem;

                lastToken = lexer.getToken();
                item.childrens.add(parseFactor());
            }
            else
            {
                break;
            }
        }

        return item;
    }

    private ParseTreeItem parseFactor() throws Exception
    {
        switch(lastToken.getTokenType())
        {
            case PLUS:
            {
                return parseAtom();
            }
            case MINUS:
            {
                ParseTreeItem item = new ParseTreeItem();

                item.type = ParseTreeItem.ParseTreeItemType.MINUS;

                ParseTreeItem leftItem = new ParseTreeItem();
                leftItem.type = ParseTreeItem.ParseTreeItemType.NUMBER;
                leftItem.value = "0";

                lastToken = lexer.getToken();

                item.childrens.add(leftItem);
                item.childrens.add(parseAtom());

                return item;
            }
            default:
            {
                return parseAtom();
            }
        }
    }

    private ParseTreeItem parseAtom() throws Exception
    {
        ParseTreeItem item = new ParseTreeItem();

        switch(lastToken.getTokenType())
        {
            case OPENBRACKET:
            {
                lastToken = lexer.getToken();
                item = parseExpression();

                if(lastToken.getTokenType() != Token.TokenType.CLOSEBRACKET)
                {
                    throw new Exception("Close bracket expected");
                }

                lastToken = lexer.getToken();
                break;
            }
            case NUMBER:
            {
                item.type = ParseTreeItem.ParseTreeItemType.NUMBER;
                item.value = lastToken.getValue();

                lastToken = lexer.getToken();
                break;
            }
            case NAME:
            {
                item.type = ParseTreeItem.ParseTreeItemType.NAME;
                item.value = lastToken.getValue();

                lastToken = lexer.getToken();

                if(lastToken.getTokenType() == Token.TokenType.OPENBRACKET)
                {
                    item.type = ParseTreeItem.ParseTreeItemType.FUNCTION;

                    lastToken = lexer.getToken();

                    while(true)
                    {
                        ParseTreeItem newItem = parseExpression();

                        if(newItem == null)
                        {
                            break;
                        }
                        else
                        {
                            item.childrens.add(newItem);

                            if(lastToken.getTokenType() == Token.TokenType.COMMA)
                            {
                                lastToken = lexer.getToken();
                                if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACKET)
                                {
                                    throw new Exception("Var name expected");
                                }
                            }
                        }
                    }

                    if(lastToken.getTokenType() == Token.TokenType.CLOSEBRACKET)
                    {
                        lastToken = lexer.getToken();
                    }
                    else
                    {
                        throw new Exception("Close bracket expected");
                    }
                }
                break;
            }
            default:
            {
                item = null;

                break;
            }
        }

        return item;
    }

    private Lexer lexer;
    private Token lastToken;
}
