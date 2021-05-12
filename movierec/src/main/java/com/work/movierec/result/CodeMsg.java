package com.work.movierec.result;

public class CodeMsg {


    private int code;
    private String msg;

    //通用 50X
    public static CodeMsg SERVER_ERROR = new CodeMsg(500, "服务端异常");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(501, "请求非法");

    //登录模块 51X
    public static CodeMsg SESSION_ERROR = new CodeMsg(510, "Session不存在或者已经失效");
    public static CodeMsg STUNO_ERROR = new CodeMsg(511, "学号格式错误");
    public static CodeMsg STUNO_NOT_EXIST = new CodeMsg(512, "学号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(513, "密码错误");
    public static final CodeMsg PERMISSION_DENIED = new CodeMsg(514, "用户无权限使用该功能");

//    //抢票模块 52X
//    public static CodeMsg SECKILL_OVER = new CodeMsg(520, "门票已卖完");
//    public static CodeMsg SECKILL_FAIL = new CodeMsg(521, "抢票失败");
//    public static CodeMsg REPEATE_SECKILL = new CodeMsg(522, "不能重复抢票");
//    public static CodeMsg VERIFYCODE_ERROR = new CodeMsg(523, "验证码错误！");

    private CodeMsg() {
    }

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    @Override
    public String toString() {
        return "CodeMsg [code=" + code + ", msg=" + msg + "]";
    }


}
