package com.example.attendance;

public class AttendanceUser {
    String date,punchintime, punchoutime,punchinimage,punchoutimage;;

    public AttendanceUser() {
    }

    public AttendanceUser(String date, String punchintime, String punchoutime, String punchinimage, String punchoutimage) {
        this.date = date;
        this.punchintime = punchintime;
        this.punchoutime = punchoutime;
        this.punchinimage = punchinimage;
        this.punchoutimage = punchoutimage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPunchintime() {
        return punchintime;
    }

    public void setPunchintime(String punchintime) {
        this.punchintime = punchintime;
    }

    public String getPunchoutime() {
        return punchoutime;
    }

    public void setPunchoutime(String punchoutime) {
        this.punchoutime = punchoutime;
    }

    public String getPunchinimage() {
        return punchinimage;
    }

    public void setPunchinimage(String punchinimage) {
        this.punchinimage = punchinimage;
    }

    public String getPunchoutimage() {
        return punchoutimage;
    }

    public void setPunchoutimage(String punchoutimage) {
        this.punchoutimage = punchoutimage;
    }
}
