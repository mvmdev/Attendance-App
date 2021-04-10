package com.example.attendance;

public class User {
    public String  name,email,mobileno,scholarno,branch;
    public User(){

    }
    public User(String name, String email, String mobileno, String scholarno, String branch) {
        this.name = name;
        this.email = email;
        this.mobileno = mobileno;
        this.scholarno = scholarno;
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getScholarno() {
        return scholarno;
    }

    public void setScholarno(String scholarno) {
        this.scholarno = scholarno;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }


}
