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

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public static int getNextLevelXP(int currentLevel){
        int x = currentLevel + 1;
        int a = 10;
        int b = 0;
        return (x*x) + (a*x) + b;
    }

    public static float getNextLevelPercent(int currentXP, int currentLevel){
        int nextXP = getNextLevelXP(currentLevel);
        return currentXP * 100 / nextXP;
    }

    public static float getPercentage(long start,long end,long val){
        if (val > end)
            return 100f;
        if (val <= start)
            return 0f;
        end = end - start;
        val = val - start;
        float res = (float)val/end;
        return res * 100f;
    }

}
