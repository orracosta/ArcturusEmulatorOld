package com.habboproject.server.utilities;

import com.habboproject.server.config.CometSettings;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class FilterUtil {
    public static String process(String string) {
        String result = string;

        for (Map.Entry<String, String> chars : CometSettings.strictFilterCharacters.entrySet()) {
            result = result.replace(chars.getKey(), chars.getValue());
        }

        result = result
                .replace(".", "")
                .replace(" ", "")
                .replace("$", "s")
                .replace("µ", "u")
                .replace("4", "a")
                .replace("0", "o")
                .replace("3", "e")
                .replace("@", "a")
                .replace("1", "i")
                .replace("é", "e")
                .replace("$", "s")
                .replace("ç", "c")
                .replace("_", "")
                .replace("", "")
                .replace("/", "")
                .replace("\\", "")
                .replace("ß", "b")
                .replace("#", "")
                .replace("~", "")
                .replace("&", "")
                .replace("^", "")
                .replace("!", "")
                .replace("?", "")
                .replace(":", "")
                .replace(";", "")
                .replace("%", "")
                .replace("¨", "")
                .replace("'", "")
                .replace("`", "")
                .replace("+", "")
                .replace("=", "")
                .replace("(", "")
                .replace("*", "")
                .replace(")", "")
                .replace("|", "")
                .replace("}", "")
                .replace("{", "")
                .replace("°", "")
                .replace("[", "")
                .replace("<", "")
                .replace(">", "")
                .replace("]", "")
                .replace("ê", "e")
                .replace("â", "a")
                .replace("ä", "a")
                .replace(",", "")
                .replace("Ä", "a")
                .replace("ö", "o")
                .replace("ô", "o")
                .replace("ò", "o")
                .replace("ü", "u")
                .replace("û", "u")
                .replace("è", "e")
                .replace("ë", "e")
                .replace("è", "e")
                .replace("ø", "o")
                .replace("₱", "p")
                .replace("Ö", "o")
                .replace("Ä", "a")
                .replace("Ë", "e")
                .replace("Ã", "a")
                .replace("Õ", "o")
                .replace("õ", "o")
                .replace("ì", "i")
                .replace("`", "")
                .replace("Ù", "u")
                .replace("Ü", "u")
                .replace("Û", "u")
                .replace("Ð", "D")
                .replace("я", "r")
                .replace("ω", "w")
                .replace("ð", "d")
                .replace("Я", "r")
                .replace("Ω", "o")
                .replace("¥", "")
                .replace("÷", "")
                .replace("Ø", "o")
                .replace("ª", "")
                .replace("†", "")
                .replace("º", "")
                .replace("í", "i");

        result = result.replaceAll("[^\\p{L}\\p{Nd}]+", "");

        return StringUtils.stripAccents(result);
    }

    public static String escapeJSONString(String str) {
        return str.replace("\"", "\\\"");
    }
}
