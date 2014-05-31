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
            Method m = new Method();
            m.type = "";
            m.args = "";

            if(method.value.equals("main"))
            {
                m.args = "[Ljava/lang/String;";
            }

            for(ParseTreeItem it : method.childrens)
            {
                if(it.type == ParseTreeItem.ParseTreeItemType.TYPE)
                {
                    if(method.value.equals("main"))
                    {
                        m.type = "V";
                    }
                    else
                    {
                        m.type = getShortType(it.value);
                    }
                }
                if(it.type == ParseTreeItem.ParseTreeItemType.ARGLIST && !method.value.equals("main"))
                {
                    for(ParseTreeItem arg : it.childrens)
                    {
                        if(arg.type == ParseTreeItem.ParseTreeItemType.DEFINE)
                        {
                            if(arg.childrens.size() == 2
                                    && arg.childrens.get(0).type == ParseTreeItem.ParseTreeItemType.TYPE
                                    && arg.childrens.get(1).type == ParseTreeItem.ParseTreeItemType.NAME)
                            {
                                m.args = m.args + getShortType(arg.childrens.get(0).value);
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
                    }
                }
            }

            if(m.type.equals(""))
            {
                throw new Exception("WRONG FUNCTION RETURN TYPE");
            }

            methods.put(method.value, m);
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
                ".end method              \n\n"
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
            string.append("([Ljava/lang/String;)V\n");
            Var mainArg = new Var();
            mainArg.name = "args";
            mainArg.type = "[Ljava/lang/String;";
            vars.put(vars.size(), mainArg);
        }
        else
        {
            string.append("(");
            string.append(methods.get(item.value).args);
            string.append(")");
            string.append(methods.get(item.value).type);
            string.append("\n");

            for(ParseTreeItem it : item.childrens)
            {
                if(it.type == ParseTreeItem.ParseTreeItemType.TYPE)
                {
                    if(!methods.containsKey(item.value))
                    {
                        throw new Exception("METHOD NOT DEFINED");
                    }
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
                                varArg.type = getShortType(arg.childrens.get(0).value);
                                varArg.name = arg.childrens.get(1).value;
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
                    }
                }
            }
        }

        methodString = new StringBuilder();
        stackCount = new StackSizeCounter();
        localsCount = 0;

        for(ParseTreeItem it : item.childrens)
        {
            switch (it.type)
            {
                case BODY:
                {
                    createBody(vars, it);
                    break;
                }
            }
        }

        for(Map.Entry<Integer, Var> var : vars.entrySet())
        {
            localsCount += getVarSizeByType(var.getValue().type);
        }

        string.append("   .limit stack          ");
        string.append(stackCount.getStackSize());
        string.append("\n");
        string.append("   .limit locals         ");
        string.append(localsCount);
        string.append("\n");

        string.append(methodString);

        string.append(".end method\n\n");
    }

    private void createBody(HashMap<Integer, Var> vars, ParseTreeItem item) throws Exception
    {
        for(ParseTreeItem it : item.childrens)
        {
            boolean isBreak = false;

            switch(it.type)
            {
                case DEFINE:
                {
                    Var var = new Var();

                    for(ParseTreeItem child : it.childrens)
                    {
                        switch(child.type)
                        {
                            case TYPE:
                            {
                                var.type = getShortType(child.value);
                                break;
                            }
                            case NAME:
                            {
                                var.name = child.value;
                                break;
                            }
                        }
                    }

                    vars.put(vars.size(), var);
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
                            case FUNCTION:
                            {
                                calculate(vars, child);
                                break;
                            }
                        }
                    }

                    Integer varId = findVar(vars, name);

                    methodString.append("   istore                ");
                    stackCount.sub(getVarSize(vars, varId));
                    methodString.append(varId);
                    methodString.append("\n");

                    break;
                }
                case FUNCTION:
                {
                    calculate(vars, it);
                    break;
                }
                case PRINT:
                {
                    if(it.childrens.size() == 0)
                    {
                        throw new Exception("EMPTY PRINT LIST");
                    }
                    methodString.append("   getstatic             java/lang/System/out Ljava/io/PrintStream;\n");
                    calculate(vars, it.childrens.get(0));
                    methodString.append("   invokevirtual         java/io/PrintStream/println(");
                    methodString.append("I"); // TODO: add double
                    methodString.append(")V\n");
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
                            case NUMBER:
                            case NAME:
                            case FUNCTION:
                            {
                                calculate(vars, it);
                                isReturnVal = true;
                                methodString.append("   ireturn\n");
                                break;
                            }
                        }
                    }
                    if(!isReturnVal)
                    {
                        methodString.append("   return\n");
                    }

                    isBreak = true;
                    break;
                }
                case WHILE:
                {
                    if(it.childrens.size() == 0)
                    {
                        throw new Exception("EMPTY CONDITION");
                    }
                    ++labelCount;
                    int startLabel = labelCount;
                    methodString.append("Label");
                    methodString.append(startLabel);
                    methodString.append(":\n");

                    calculate(vars, it.childrens.get(0));

                    int endLabel = labelCount;
                    if(it.childrens.size() > 1)
                    {
                        createBody(vars, it.childrens.get(1));
                    }

                    methodString.append("   goto                  Label");
                    methodString.append(startLabel);
                    methodString.append("\n");

                    methodString.append("Label");
                    methodString.append(endLabel);
                    methodString.append(":\n");

                    break;
                }
                case IF:
                {
                    if(it.childrens.size() == 0)
                    {
                        throw new Exception("EMPTY CONDITION");
                    }
                    calculate(vars, it.childrens.get(0));

                    int endLabel = labelCount;
                    if(it.childrens.size() > 1)
                    {
                        createBody(vars, it.childrens.get(1));
                    }
                    methodString.append("Label");
                    methodString.append(endLabel);
                    methodString.append(":\n");

                    break;
                }
            }

            if(isBreak)
            {
                break;
            }
        }
    }

    private void calculate(HashMap<Integer, Var> vars, ParseTreeItem item) throws Exception
    {
        if(item.type == ParseTreeItem.ParseTreeItemType.FUNCTION)
        {
            for(ParseTreeItem it : item.childrens)
            {
                calculate(vars, it);
            }

            methodString.append("   invokestatic          MainClass/");
            methodString.append(item.value);
            methodString.append("(");
            String methodArgs = methods.get(item.value).args;
            methodString.append(methodArgs);
            for(int i = 0; i < methodArgs.length(); ++i)
            {
                stackCount.sub(getVarSizeByType(String.valueOf(methodArgs.charAt(i))));
            }
            methodString.append(")");
            String methodType = methods.get(item.value).type;
            methodString.append(methodType);
            stackCount.add(getVarSizeByType(methodType));
            methodString.append("\n");
            return;
        }

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
                methodString.append("   iload                 ");
                stackCount.add(getVarSize(vars, item.value));
                methodString.append(findVar(vars, item.value));
                methodString.append("\n");
                break;
            }
            case NUMBER:
            {
                methodString.append("   bipush                ");
                stackCount.add(getVarSizeByType("I")); //TODO: add double
                methodString.append(item.value);
                methodString.append("\n");
                break;
            }
            case MULTIPLICATION:
            {
                methodString.append("   imul                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case DIVISION:
            {
                methodString.append("   idiv                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case PLUS:
            {
                methodString.append("   iadd                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case MINUS:
            {
                methodString.append("   isub                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case LESS:
            {
                methodString.append("   isub                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                ++labelCount;
                methodString.append("   ifge                  Label");
                methodString.append(labelCount);
                methodString.append("\n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case GREATER:
            {
                methodString.append("   isub                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                ++labelCount;
                methodString.append("   ifle                  Label");
                methodString.append(labelCount);
                methodString.append("\n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case LESSEQUALS:
            {
                methodString.append("   isub                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                ++labelCount;
                methodString.append("   ifgt                  Label");
                methodString.append(labelCount);
                methodString.append("\n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case GREATEREQUALS:
            {
                methodString.append("   isub                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                ++labelCount;
                methodString.append("   iflt                  Label");
                methodString.append(labelCount);
                methodString.append("\n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case DOUBLEEQUALS:
            {
                methodString.append("   isub                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                ++labelCount;
                methodString.append("   ifeq                  Label");
                methodString.append(labelCount);
                methodString.append("\n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                break;
            }
            case NOTEQUALS:
            {
                methodString.append("   isub                  \n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
                ++labelCount;
                methodString.append("   ifne                  Label");
                methodString.append(labelCount);
                methodString.append("\n");
                stackCount.sub(getVarSizeByType("I")); //TODO: add double
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

    private int getVarSize(HashMap<Integer, Var> vars, int id) throws Exception
    {
        return getVarSizeByType(vars.get(id).type);
    }

    private int getVarSize(HashMap<Integer, Var> vars, String name) throws Exception
    {
        return getVarSizeByType(vars.get(findVar(vars, name)).type);
    }

    private int getVarSizeByType(String type)
    {
        if(type.equals("V"))
        {
            return 0;
        }
        else if(type.equals("D"))
        {
            return 2;
        }

        return 1;
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

    private class Method
    {
        public String type;
        public String args;
    }

    private HashMap<String, Method> methods = new HashMap<>();
    private ParseTreeItem head;
    private StringBuilder string = new StringBuilder();
    private int labelCount = 0;

    int localsCount = 0;
    StackSizeCounter stackCount = new StackSizeCounter();
    StringBuilder methodString = new StringBuilder();
}

class StackSizeCounter
{
    public int getStackSize()
    {
        return max;
    }

    public void add(int size)
    {
        count += size;
        if (count > max)
        {
            max = count;
        }
    }

    public void sub(int size)
    {
        count -= size;
    }

    private int count = 0;
    private int max = 0;
}
