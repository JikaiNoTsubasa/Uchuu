package fr.triedge.uchuu.utils;

public class Utils {

    public static boolean isValid(String value){
        return value != null && !value.equals("");
    }

    public static boolean isValid(Integer value){
        return value!=null && value > 0;
    }

    public static boolean isValid(String... values){
        for (String s : values){
            if (!isValid(s))
                return false;
        }
        return true;
    }

    public static String minsToHoursDisplay(int mins){
        int hours   = mins / 60;
        int minutes = mins % 60;
        return String.format("%02d", hours)+"h"+String.format("%02d", minutes);
    }

}
