package com.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gmall.bean.UmsMember;
import com.gmall.service.UmsMemberService;
import com.gmall.util.CookieUtil;
import com.gmall.util.HttpclientUtil;
import com.gmall.util.JwtUtil;
import io.jsonwebtoken.Jwt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UmsMemberService umsMemberService;

    /**
     * 验证用户token信息
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String currentIp) {

        //创建封装了验证信息，以及用户信息的Map
        Map<String, String> map = new HashMap<>();
        //对token信息进行解码
        Map<String, Object> decode = JwtUtil.decode(token, "2020-gmall-ted", currentIp);
        if (decode != null) {
            //如果验证成功,封装成功的信息
            map.put("status", "success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));

        } else {
            map.put("status", "fail");
        }

        return JSON.toJSONString(map);
    }

    /**
     * 登录页面
     * @param ReturnUrl
     * @param model
     * @return
     */
    @RequestMapping("index.html")
    public String index(String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "index";
    }

    /**
     * 使用ajax向前台发送用户的token数据
     * @param umsMember
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request, HttpServletResponse response) {

        UmsMember userLogin = umsMemberService.login(umsMember);
        String token = makeToken(userLogin, request);

        return token;
    }

    /**
     * 第三方登录页面
     * @return
     */
    @RequestMapping("vlogin")
    public String vlogin(String code ,HttpServletRequest request,HttpServletResponse response) throws IOException {

        StringBuffer requestURL = request.getRequestURL();

        //根据code得到用户access_token信息
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "806370490");
        map.put("client_secret", "44918749c51bbb5cab0641ddc6122748");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://192.168.1.6:8085/vlogin");
        map.put("code",code);
        String token = HttpclientUtil.doPost("https://api.weibo.com/oauth2/access_token", map);
        //将得到的token，转为Map类型
        Map <String,Object> tokenMap = JSON.parseObject(token, Map.class);
        String access_token = (String) tokenMap.get("access_token");
        String uid = (String) tokenMap.get("uid");

        //根据token信息和uid 使用API查询用户信息
        String userInfo = HttpclientUtil.doGet
                ("https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid);

        Map<String,Object> userInfoMap = JSON.parseObject(userInfo, Map.class);

        //将用户数据存入数据库
        UmsMember umsMember = new UmsMember();
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceType(2);
        umsMember.setSourceUid(Long.parseLong((String) userInfoMap.get("idstr")));
        umsMember.setNickname((String) userInfoMap.get("screen_name"));
        umsMember.setCity((String) userInfoMap.get("location"));
        umsMember.setUsername((String) userInfoMap.get("screen_name"));
        String gender = (String) userInfoMap.get("gender");
        String genderDB = "";
        if (StringUtils.isNotBlank(gender)) {
            if (gender.equals("m")) {
                //为男性
                genderDB = "1";
            } else if (gender.equals("f")) {
                //为女性
                genderDB = "2";
            } else {
                //为未知
                genderDB = "0";
            }
        }

        umsMember.setGender(Integer.parseInt(genderDB));

        //检查用户是否已经登录过
        UmsMember checkUser = umsMemberService.checkUserFormOauth(umsMember.getSourceUid());
        if (checkUser == null) {
            //如果未登录过，将数据写入数据库
            umsMember = umsMemberService.addUserFromOauth(umsMember);
        } else {
            umsMember = checkUser;
        }


        //将用户的memberId和nickname制成token
        String token1 = makeToken(umsMember, request);

//        String cookie = CookieUtil.getCookieValue(request, "oldToken", true);
//        System.out.println("得到的cookie："+cookie);
        return "redirect:http://192.168.1.6:8083/index?token="+token1;



    }



    /**
     * 制作用户的token
     * @param umsMember
     * @return
     */
    public String makeToken(UmsMember umsMember,HttpServletRequest request) {

        String token = "";
        if (umsMember != null) {
            //如果用户信息不为空
            //使用jwt生成token
            Map<String, Object> map = new HashMap<>();
            String memberId = umsMember.getId();
            String nickname = umsMember.getNickname();
            map.put("memberId", memberId);
            map.put("nickname", nickname);

            //盐值        使用ip
            String ip = request.getRemoteAddr();
            token = JwtUtil.encode("2020-gmall-ted", map, ip);
            //将token放入到缓存中
            umsMemberService.addUserToken(token, memberId);
            //将token写入Cookie
//            CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
        } else {
            token = "fail";
        }

        return token;
    }






}
