package com.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.gmall.annotations.LoginRequired;
import com.gmall.util.CookieUtil;
import com.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //该判断可以排除静态资源也被拦截的情况
        if (handler instanceof HandlerMethod) {
            //判断方法上是否具有需要拦截的注解
            HandlerMethod mh = (HandlerMethod) handler;
            StringBuffer u = request.getRequestURL();
            LoginRequired annotation = mh.getMethodAnnotation(LoginRequired.class);
            if (annotation == null) {
                //未标记注解，直接放行
                return true;
            }

            String token = "";
            //获取从Cookie中获取的数据
            String oldToken = CookieUtil.getCookieValue(request,"oldToken", true);

            if (StringUtils.isNotBlank(oldToken)) {
                token = oldToken;
            }

            //获取从请求参数中获取的数据
            String newToken = request.getParameter("token");

            if (StringUtils.isNotBlank(newToken)) {
                token = newToken;
            }
            //如果Cookie和请求参数中都没有数据，拒绝访问
//            if (StringUtils.isBlank(token)) {
                //重定向到登录页面
//                response.sendRedirect("http://192.168.1.6:8085/index.html?ReturnUrl="+request.getRequestURL());
//                return true;
//            }
            //获取请求中的ip信息
            String ip = request.getRemoteAddr();

            //验证用户token信息
            //验证用户方法，如果认证成功返回success。使用httpclient发送跨域请求
            String successMapStr = HttpclientUtil.doGet("http://192.168.1.6:8085/verify?token=" + token+"&currentIp="+ip);
            Map<String,String> map = JSON.parseObject(successMapStr, Map.class);
            String success = map.get("status");

            //标记了注解
            //判断是否必须验证通过
            boolean mustSuccess = annotation.mustSuccess();
            if (mustSuccess) {
                //必须验证通过才可以放行
                if (!success.equals("success")) {
                    //重定向到登录页面
                    response.sendRedirect("http://192.168.1.6:8085/index.html?ReturnUrl="+request.getRequestURI());
                    return false;
                }

            }
                //不需要验证通过也可一放行，但是需要验证用户是否登录，用于购物车功能的不同分支
                if (success.equals("success")) {
                    //如果登录成功，向请求域中添加用户数据
                    request.setAttribute("memberId",map.get("memberId"));
                    request.setAttribute("nickname", map.get("nickname"));
                    //更新Cookie中的数据
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 24*7, true);
                }
                //否则不在请求域中添加用户信息

        }
        return  true;
    }
}
