package fr.triedge.uchuu.utils;

public class Utils {

    public static boolean isValid(String value){
        return value != null && !value.equals("");
    }

    public static boolean isValid(String... values){
        for (String s : values){
            if (!isValid(s))
                return false;
        }
        return true;
    }

}
