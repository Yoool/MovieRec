package com.work.movierec.redis;

public class SeckillUserKey extends BasePrefix {
    //token默认过期时间7天
    public static final int TOKEN_EXPIRE = 3600 * 24 * 7;

    private SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "token");
    public static SeckillUserKey getById = new SeckillUserKey(0, "id");
}
