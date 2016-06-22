package com.example.calendarquickstart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by abirshukla on 5/26/16.
 */
public class htmlCodeJ {
    public static int count = 0;
    public static String htmlCodeCA = "";
    public static ArrayList<String> quotes = new ArrayList<String>();
    public static ArrayList<String> dates = new ArrayList<String>();
    public static boolean notify = true;
    public static int noteHour = 11;
    public static int noteMin = 31;
    public static String[] preQ = {"A man who fears suffering is already suffering from what he fears.","Find something more important than you are and dedicate your life to it.","The mind is its own place, and in itself can make a Heav’n of Hell, a Hell of Heav’n","My life has been full of terrible misfortunes, most of which never happened.","Everything that irritates us about others can lead us to an understanding of ourselves.","You never change things by fighting the existing reality. To change something, build a new model that makes the existing model obsolete.","Don't let yesterday use up too much of today.","Everything that irritates us about others can lead us to an understanding of ourselves.","Nothing in life is to be feared. It is only to be understood.","I was brought up to believe that how I saw myself was more important than how others saw me.","And in the end, it's not the years in your life that count. \n" +
            "It's the life in your years"};
    public static String[] preA = {"Michel de Montaigne","Daniel Dennett","John Milton","Michel de Montaigne","Carl Jung","Buckminster Fuller","Cherokee","Carl Jung","Marie Curie","Anwar Sadat","Abraham Lincoln"};
    public static void setHtmlCodeCA(String c) {
        htmlCodeCA = c;
    }
    public static int getRandomQuote() {
        Random random = new Random();
        int index = random.nextInt(9 - 1) + 0;
        while (true) {
            if(quotes.contains(preQ[index]) == false) {
                System.out.println("Random Quote: "+preQ[index]);
                return index;
            }
        }
    }
    public static void addToCircle(String quote) {
        Calendar c = Calendar.getInstance();
        int da = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String monthString = "";
        switch (month) {
            case 0:  monthString = "January";
                break;
            case 1:  monthString = "February";
                break;
            case 2:  monthString = "March";
                break;
            case 3:  monthString = "April";
                break;
            case 4:  monthString = "May";
                break;
            case 5:  monthString = "June";
                break;
            case 6:  monthString = "July";
                break;
            case 7:  monthString = "August";
                break;
            case 8:  monthString = "September";
                break;
            case 9: monthString = "October";
                break;
            case 10: monthString = "November";
                break;
            case 11: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }
        String date = monthString+" "+da+", "+year;
        if (count < 10) {
            quotes.add(quote);
            dates.add(date);
            count++;
        }
        else {
            dates.add(0,date);
            quotes.add(0,quote);
        }
    }
    public static String getHtmlCodeCA() {
        return htmlCodeCA;
    }

    public static void main (String args[]) {
        String a = "Heyabir whatabir isabir up";
        String arr[] = a.split("abir");
        for (int i = 0; i < arr.length;i++) {
            System.out.println("Index "+i+": "+arr[i].trim());
        }

    }



}
