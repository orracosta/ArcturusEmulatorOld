package com.eu.habbo.util.figure;

import gnu.trove.map.hash.THashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class FigureUtil 
{
    public static THashMap<String, String> getFigureBits(String looks) 
    {
        THashMap<String, String> bits = new THashMap<String, String>();
        String[] sets = looks.split("\\.");
        
        for(String set : sets)
        {
            String[] setBits = set.split("-", 2);
            bits.put(setBits[0], setBits.length > 1 ? setBits[1] : "");
        }
        
        return bits;
    }
    
    
    public static String mergeFigures(String figure1, String figure2)
    {
        return mergeFigures(figure1, figure2, null, null);
    }
    
    public static String mergeFigures(String figure1, String figure2, String[] limitFigure1)
    {
        return mergeFigures(figure1, figure2, limitFigure1, null);
    }
    
    public static String mergeFigures(String figure1, String figure2, String[] limitFigure1, String[] limitFigure2)
    {
        THashMap<String, String> figureBits1 = getFigureBits(figure1);
        THashMap<String, String> figureBits2 = getFigureBits(figure2);

        String finalLook = "";

        for (Map.Entry<String, String> keys : figureBits1.entrySet()) 
        {
            if(limitFigure1 == null || ArrayUtils.contains(limitFigure1, keys.getKey())) 
            {
                finalLook = finalLook + keys.getKey() + "-" + keys.getValue() + ".";
            }
        }

        for (Map.Entry<String, String> keys : figureBits2.entrySet()) 
        {
            if(limitFigure2 == null || ArrayUtils.contains(limitFigure2, keys.getKey())) 
            {
                finalLook = finalLook + keys.getKey() + "-" + keys.getValue() + ".";
            }
        }

        if(finalLook.endsWith(".")) 
        {
            finalLook = finalLook.substring(0, finalLook.length() - 1);
        }
        
        return finalLook;
    }
}
