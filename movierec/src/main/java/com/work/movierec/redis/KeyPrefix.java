package com.work.movierec.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();

}
