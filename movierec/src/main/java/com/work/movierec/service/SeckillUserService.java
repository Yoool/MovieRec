//package com.work.seckill.service;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletResponse;
//
//import com.work.seckill.dao.SeckillUserDao;
//import com.work.seckill.domain.SeckillUser;
//import com.work.seckill.util.UUIDUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.work.seckill.exception.GlobalException;
//import com.work.seckill.redis.SeckillUserKey;
//import com.work.seckill.result.CodeMsg;
//import com.work.seckill.redis.RedisService;
//import com.work.seckill.util.MD5Util;
//import com.work.seckill.vo.LoginVo;
//
//import java.util.List;
//
//@Service
//public class SeckillUserService {
//
//
//    public static final String COOKIE_NAME_TOKEN = "token";
//
//    @Autowired
//    SeckillUserDao seckillUserDao;
//
//    @Autowired
//    RedisService redisService;
//
//    public List<SeckillUser> getUserList(){
//        return seckillUserDao.getUserList();
//    }
//
//    public SeckillUser getById(long id) {
//        // 取缓存
////        redisService
//        return seckillUserDao.getById(id);
//    }
//
//    public SeckillUser getByStuno(String stuno) {
//        return seckillUserDao.getByStuno(stuno);
//    }
//
//
//    public SeckillUser getByToken(HttpServletResponse response, String token) {
//        if (StringUtils.isEmpty(token)) return null;
//        SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
//        if (user != null) addCookie(response, token, user);
//        return user;
//    }
//
//
//    public boolean login(HttpServletResponse response, LoginVo loginVo) {
//        if (loginVo == null) {
//            throw new GlobalException(CodeMsg.SERVER_ERROR);
//        }
//        String stuno = loginVo.getStuno();
//        String formPass = loginVo.getPassword();
////        //判断学号是否存在
////        SeckillUser user = getByStuno(stuno);
////        if (user == null) {
////            throw new GlobalException(CodeMsg.STUNO_NOT_EXIST);
////        }
////        //验证密码
////        String dbPass = user.getPassword();
////        String saltDB = user.getSalt();
////        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
////        if (!calcPass.equals(dbPass)) {
////            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
////        }
////        //生成cookie
////        String token = UUIDUtil.uuid();
////        addCookie(response, token, user);
//        return true;
//    }
//
//    private void addCookie(HttpServletResponse response, String token, SeckillUser user) {
//        redisService.set(SeckillUserKey.token, token, user);
//        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
//        //有效期跟token保持一致
//        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
////        网站根目录
////        cookie.setPath("/");
//        response.addCookie(cookie);
//    }
//
//}
