//package com.work.seckill.config;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.work.seckill.domain.SeckillUser;
//import com.work.seckill.service.SeckillUserService;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//@Service
//public class UserArgumentResolver implements HandlerMethodArgumentResolver {
//
//    @Autowired
//    SeckillUserService userService;
//
//    public boolean supportsParameter(MethodParameter parameter) {
//        return parameter.getParameterType() == SeckillUser.class;
//    }
//
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
//                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//
//        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
//
//        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
//        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
//        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
//            return null;
//        }
//        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
//        return userService.getByToken(response, token);
//    }
//
//    private String getCookieValue(HttpServletRequest request, String cookieName) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null || cookies.length <= 0) {
//            return null;
//        }
//        // ��������cookie���Ƿ��ж�Ӧcookie
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(cookieName)) {
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
//
//}
