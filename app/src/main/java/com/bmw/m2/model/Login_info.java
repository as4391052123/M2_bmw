package com.bmw.m2.model;

/**
 * Created by admin on 2017/6/17.
 */

public class Login_info {

    private static Login_info instance;

    private String hk_ip;
    private int hk_port;
    private String hk_password;
    private String hk_account;
    private boolean hk_isYingJieMa;

    public static Login_info getInstance(){
        if(instance == null){
            synchronized (Login_info.class){
                instance = new Login_info();
            }
        }
        return instance;
    }

    private Login_info(){
        setHk_ip("172.169.10.65");
        setHk_port(8000);
        setHk_account("admin");
        setHk_password("bmw12345");
    }


    public String getHk_ip() {
        return hk_ip;
    }

    public void setHk_ip(String hk_ip) {
        this.hk_ip = hk_ip;
    }

    public int getHk_port() {
        return hk_port;
    }

    public void setHk_port(int hk_port) {
        this.hk_port = hk_port;
    }

    public String getHk_password() {
        return hk_password;
    }

    public void setHk_password(String hk_password) {
        this.hk_password = hk_password;
    }

    public String getHk_account() {
        return hk_account;
    }

    public void setHk_account(String hk_account) {
        this.hk_account = hk_account;
    }

    public boolean isHk_isYingJieMa() {
        return hk_isYingJieMa;
    }

    public void setHk_isYingJieMa(boolean hk_isYingJieMa) {
        this.hk_isYingJieMa = hk_isYingJieMa;
    }

    public boolean isShowFirstName() {
        return true;
    }
}
