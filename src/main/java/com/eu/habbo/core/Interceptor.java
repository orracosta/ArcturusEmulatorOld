package com.eu.habbo.core;

import java.io.OutputStream;
import java.io.PrintStream;

public class Interceptor extends PrintStream
{
    public Interceptor(OutputStream out)
    {
        super(out, true);
    }
    @Override
    public void print(String s)
    {
        if (s.equalsIgnoreCase("java.lang.nullpointerexception"))
        {
            System.out.println("Found silly stacktrace!");
            new Throwable().printStackTrace();
        }

        super.print(s);
    }
}