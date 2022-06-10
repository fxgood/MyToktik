package com.njust.intercerptor;

import com.njust.base.BaseInfoProperties;
import com.njust.exceptions.GraceException;
import com.njust.grace.result.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//对用户的登录状态进行检查(修改个人信息,修改头像,修改背景图之前的检查)
public class UserTokenInterceptor extends BaseInfoProperties implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String redisToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
            if(StringUtils.isBlank(redisToken)){
                GraceException.display(ResponseStatusEnum.UN_LOGIN);   //抛异常
            }else{
                if(!redisToken.equals(userToken)){
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);  //抛异常
                }
            }
        }else{
            GraceException.display(ResponseStatusEnum.UN_LOGIN);    //抛异常
        }
        return true;    //请求放行
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
