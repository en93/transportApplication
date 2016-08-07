package com.example.ian.transport;


import java.util.Date;
import java.util.Scanner;

/**
 * Created by Ian on 1/09/2015.
 */
public class BusArrival {
    private String routeNum;
    private String timeRaw;
    private String timeFormat;
    private Date date;

    public BusArrival(String routeNum, String time){
        this.routeNum = routeNum;
        this.timeRaw = time;
        date = new Date();
        timeFormat = format(time);
    }

    private String format(String time) {
        Scanner scanner = new Scanner(timeRaw);
        scanner.useDelimiter(":");

        int hours24 = scanner.nextInt();
        date.setHours(hours24);
        String hours12;
        String minutes = scanner.next();
        date.setMinutes(Integer.parseInt(minutes));
        String suffix;
        if(hours24>12){
            hours12 = "" + (hours24-12);
            suffix = "PM";
        }else {
            hours12 = "" + hours24;
            suffix = "AM";
        }
        if(hours12.length() == 1){
            hours12 = "0" + hours12;
        }

        return ""+hours12+":"+minutes+suffix;
    }

    public String getRouteNum() {
        return routeNum;
    }

    public void setRouteNum(String routeNum) {
        this.routeNum = routeNum;
    }

    public String getTime() {
        return timeFormat;
    }

    public void setTime(String time) {
        timeFormat = format(time);
    }

    public Date getDate(){
        return date;
    }
}
