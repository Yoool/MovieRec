package com.work.movierec.util;

public class CALCUtil {
    /**
     * input a calculate string, calcuate the value
     * the number between 0-9
     * round the middle vlaue to int
     * */
    public static int calc(String cal) {

        String[] car=new String[cal.length()];
        for (int i = 0; i < car.length; i++) {
            car[i]=""+cal.charAt(i);
        }

        System.err.println(car);

        //do * and /
        for (int i = 0; i < car.length; i++) {
            if(car[i].equals("*"))
            {
                car[i+1]= ""+(Integer.parseInt(car[i-1]) * Integer.parseInt(car[i+1]));
                car[i-1]="";
                car[i]="";

            }else if(car[i].equals("/"))
            {
                car[i+1]= ""+(Integer)(Integer.parseInt(car[i-1]) / Integer.parseInt(car[i+1]));
                car[i-1]="";
                car[i]="";
            }
            else {
                continue;
            }
        }

        //do + and -
        int index=0;
        for (int i = 0; i < car.length; i++) {
            index=i;
            if(car[i].equals("+"))
            {
                while(car[++index].equals("")){}
                car[index]= ""+(Integer.parseInt(car[i-1]) + Integer.parseInt(car[index]));
                car[i-1]="";
                car[i]="";

            }else if(car[i].equals("-"))
            {
                while(car[++index].equals("")){}
                car[index]= ""+(Integer.parseInt(car[i-1]) - Integer.parseInt(car[index]));
                car[i-1]="";
                car[i]="";
            }
            else {
                continue;
            }
        }
        return Integer.parseInt(car[car.length-1]);

    }

}
