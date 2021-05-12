package com.work.movierec.controller;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.work.movierec.result.Result;
//import com.work.seckill.service.SeckillUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.work.seckill.redis.RedisService;
import com.work.movierec.vo.LoginVo;


@Controller
@RequestMapping("/login")
@EnableAutoConfiguration
public class LoginController {



//    @Autowired
//    SeckillUserService userService;

//    @Autowired
//    RedisService redisService;

    @RequestMapping("/tologin")
    public String toLogin() {
        return "login";
    }


    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/dologin")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //登录
//        userService.login(response, loginVo);
        return Result.success(true);
    }

}
