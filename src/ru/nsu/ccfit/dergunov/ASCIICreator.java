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

import java.util.HashMap;
import java.util.Map;

public class ASCIICreator
{
    public ASCIICreator(ParseTreeItem item)
    {
        head = item;
    }

    public String create() throws Exception
    {
        createProgram(head);

        return string.toString();
    }

    private void createProgram(ParseTreeItem item) throws Exception
    {
        for(ParseTreeItem method : item.childrens)
        {
            for(ParseTreeItem it : method.childrens)
            {
                if(it.type == ParseTreeItem.ParseTreeItemType.TYPE)
                {
                    methodType.put(method.value, getShortType(it.value));
                    break;
                }
            }
        }

        string.append(
                ".source                  MainClass.java\n" +
                ".class                   public MainClass\n" +
                ".super                   java/lang/Object\n" +
                "\n" +
                "\n" +
                ".method                  public <init>()V\n" +
                "   .limit stack          1\n" +
                "   .limit locals         1\n" +
                "   aload_0               \n" +
                "   invokespecial         java/lang/Object/<init>()V\n" +
                "   return                \n" +
                ".end method              \n"
        );

        for(ParseTreeItem method : item.childrens)
        {
            createMethod(method);
        }
    }

    private void createMethod(ParseTreeItem item) throws Exception
    {
        HashMap<Integer, Var> vars = new HashMap<>();

        string.append("method                  public static ");
        string.append(item.value);
        if(item.value.equals("main"))
        {
            //TODO: what if real argc/argv ?
            string.append("([Ljava/lang/String;)V\n");
            Var mainArg = new Var();
            mainArg.name = "args";
            mainArg.type = "void";
            vars.put(vars.size(), mainArg);
        }
        else
        {
            string.append("(");
            String type = "undefined";
            for(ParseTreeItem it : item.childrens)
            {
                if(it.type == ParseTreeItem.ParseTreeItemType.TYPE)
                {
                    if(!methodType.containsKey(it.value))
                    {
                        throw new Exception("METHOD NOT DEFINED");
                    }
                    type = methodType.get(it.value);
                }
                if(it.type == ParseTreeItem.ParseTreeItemType.ARGLIST)
                {
                    for(ParseTreeItem arg : it.childrens)
                    {
                        if(arg.type == ParseTreeItem.ParseTreeItemType.DEFINE)
                        {
                            if(arg.childrens.size() == 2
                                    && arg.childrens.get(0).type == ParseTreeItem.ParseTreeItemType.TYPE
                                    && arg.childrens.get(1).type == ParseTreeItem.ParseTreeItemType.NAME)
                            {
                                Var varArg = new Var();
                                varArg.name = arg.childrens.get(0).value;
                                varArg.type = arg.childrens.get(1).value;
                                vars.put(vars.size(), varArg);
                            }
                            else
                            {
                                throw new Exception("WRONG ARG LIST");
                            }
                        }
                        else
                        {
                            throw new Exception("WRONG ARG LIST");
                        }
                        //TODO: ADD ARG LIST
                    }
                }
            }
            if(type.equals("undefined"))
            {
                throw new Exception("METHOD NOT DEFINED");
            }
            string.append(")");
            string.append(type);
            string.append("\n");
        }

        string.append("   .limit stack          ");
        string.append(10); //TODO: CALC STACK SIZE
        string.append("\n");
        string.append("   .limit locals         ");
        string.append(10); //TODO: CALC LOCALS LIMIT
        string.append("\n");

        for(ParseTreeItem it : item.childrens)
        {
            switch (it.type)
            {
                case TYPE:
                {
                    break;
                }
                case ARGLIST:
                {
                    break;
                }
                case BODY:
                {
                    createBody(vars, it);
                    break;
                }
            }
        }

        string.append(".end method\n");
    }

    private void createBody(HashMap<Integer, Var> vars, ParseTreeItem item) throws Exception
    {
        for(ParseTreeItem it : item.childrens)
        {
            switch(it.type)
            {
                case DEFINE:
                {
                    Var var = new Var();
                    vars.put(vars.size(), var);

                    for(ParseTreeItem child : it.childrens)
                    {
                        switch(child.type)
                        {
                            case TYPE:
                            {
                                var.type = child.value;
                                break;
                            }
                            case NAME:
                            {
                                var.name = child.value;
                                break;
                            }
                        }
                    }
                    break;
                }
                case INITIALIZE:
                {
                    String name = it.value;
                    if(it.childrens.size() == 0)
                    {
                        throw new Exception("VAR INITIALIZE LIST IS EMPTY");
                    }
                    for(ParseTreeItem child : it.childrens)
                    {
                        switch(child.type)
                        {
                            case MULTIPLICATION:
                            case DIVISION:
                            case MINUS:
                            case PLUS:
                            case NUMBER:
                            case NAME:
                            {
                                calculate(vars, child);
                                break;
                            }
                        }
                    }

                    Integer varId = findVar(vars, name);

                    string.append("   istore                ");
                    string.append(varId);
                    string.append("\n");
                    break;
                }
                case PRINT:
                {
                    Integer varId = findVar(vars, it.value);

                    string.append("   getstatic             java/lang/System/out Ljava/io/PrintStream;\n");
                    string.append("   iload                 ");
                    string.append(varId);
                    string.append("\n");
                    string.append("   invokevirtual         java/io/PrintStream/println(");
                    Var var = vars.get(varId);
                    string.append(getShortType(var.type));
                    string.append(")V\n");
                    break;
                }
                case RETURN:
                {
                    boolean isReturnVal = false;
                    for(ParseTreeItem child : it.childrens)
                    {
                        switch (child.type)
                        {
                            case MULTIPLICATION:
                            case DIVISION:
                            case MINUS:
                            case PLUS:
                            {
                                calculate(vars, it);
                                isReturnVal = true;
                                string.append("   ireturn\n");
                                break;
                            }
                        }
                    }
                    if(!isReturnVal)
                    {
                        string.append("   return\n");
                    }
                    return;
                }
            }
        }
    }

    private void calculate(HashMap<Integer, Var> vars, ParseTreeItem item) throws Exception
    {
        if(item.childrens.size() > 2)
        {
            return;
        }

        if(item.childrens.size() >= 1)
        {
            calculate(vars, item.childrens.get(0));
        }
        if(item.childrens.size() >= 2)
        {
            calculate(vars, item.childrens.get(1));
        }

        switch (item.type)
        {
            case NAME:
            {
                string.append("   iload                 ");
                string.append(findVar(vars, item.value));
                string.append("\n");
                break;
            }
            case NUMBER:
            {
                string.append("   bipush                ");
                string.append(item.value);
                string.append("\n");
                break;
            }
            case MULTIPLICATION:
            {
                string.append("   imul                  \n");
                break;
            }
            case DIVISION:
            {
                string.append("   idiv                  \n");
                break;
            }
            case PLUS:
            {
                string.append("   iadd                  \n");
                break;
            }
            case MINUS:
            {
                string.append("   isub                  \n");
                break;
            }
        }
    }

    private Integer findVar(HashMap<Integer, Var> vars, String name) throws Exception
    {
        Integer varId = null;
        Var var = null;

        for(Map.Entry<Integer, Var> varEntry : vars.entrySet())
        {
            var = varEntry.getValue();
            if(var.name.equals(name))
            {
                varId = varEntry.getKey();
                break;
            }
        }

        if(var == null || varId == null)
        {
            throw new Exception("VAR NOT FOUND");
        }

        return varId;
    }

    private String getShortType(String type) throws Exception
    {
        switch(type)
        {
            case "int":
            {
                return "I";
            }
            case "double":
            {
                return "D";
            }
            case "void":
            {
                return "V";
            }
            default:
            {
                throw new Exception("WRONG TYPE");
            }
        }
    }

    private class Var
    {
        public String name;
        public String type;
    }

    private HashMap<String, String> methodType = new HashMap<>();
    private ParseTreeItem head;
    private StringBuilder string = new StringBuilder();
}
