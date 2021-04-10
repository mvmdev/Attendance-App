package com.example.attendance;

public class Attendance {
    public String name, punchintime, punchoutime, punchinimage,punchoutimage;
public  Attendance(){

}

    public Attendance(String name, String punchintime, String punchoutime, String punchinimage, String punchoutimage) {
        this.name = name;
        this.punchintime = punchintime;
        this.punchoutime = punchoutime;
        this.punchinimage = punchinimage;
        this.punchoutimage = punchoutimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
