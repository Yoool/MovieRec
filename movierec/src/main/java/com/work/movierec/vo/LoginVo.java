package com.work.movierec.vo;


import javax.validation.constraints.NotNull;


public class LoginVo {

    @NotNull
    private String stuno;

    @NotNull
    private String password;

    public String getStuno() {
        return stuno;
    }
    public void setStuno(String stuno) {
        this.stuno = stuno;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "LoginVo [stuno=" + stuno + ", password=" + password + "]";
    }
}
