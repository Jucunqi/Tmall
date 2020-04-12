package com.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gmall.bean.UmsMember;
import com.gmall.user.mapper.UmsMemberMapper;
import com.gmall.service.UmsMemberService;
import com.gmall.user.mapper.UmsMemberMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import tk.mybatis.mapper.entity.Example;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Resource
    private UmsMemberMapper umsMemberMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UmsMember selById(String id) {
        return umsMemberMapper.selectByPrimaryKey(id);
    }

    /**
     * 从数据库中查询用户信息
     * @param umsMember
     * @return
     */
    @Override
    public UmsMember selUserInfo(UmsMember umsMember) {

        List<UmsMember> select = umsMemberMapper.select(umsMember);

        //如果查询到的信息不为空，将用户数据返回
        if (select != null && select.size() > 0) {
            return select.get(0);
        } else {
            return null;
        }
    }

    /**
     * 验证用户名，密码信息，使用缓存的方式
     * @param umsMember
     * @return
     */
    @Override
    public UmsMember login(UmsMember umsMember) {

        //先到缓存中查询用户信息
        String umsMemberStr = stringRedisTemplate.opsForValue().get("user:" + umsMember.getPassword() + ":info");
        if (StringUtils.isNotBlank(umsMemberStr)) {

            //如果缓存中有用户信息，直接返回
            return JSON.parseObject(umsMemberStr, UmsMember.class);
        }
        //如果缓存中没有用户信息，去数据库中查询
        UmsMember umsMember1 = selUserInfo(umsMember);
        if (umsMember1 != null) {

            //将用户信息放入到缓存中
            stringRedisTemplate.opsForValue().set("user:" + umsMember.getPassword() + ":info",JSON.toJSONString(umsMember1),60*60, TimeUnit.SECONDS);
        }

        return umsMember1;
    }

    /**
     * 向缓存中添加用户的token信息
     * @param token
     * @param memberId
     */
    @Override
    public void addUserToken(String token, String memberId) {
        stringRedisTemplate.opsForValue().set("user:" + memberId + ":token", token, 60 * 60 * 2, TimeUnit.SECONDS);
    }

    /**
     * 新增第三方用户信息
     * @param umsMember
     * @return
     */
    @Override
    public UmsMember addUserFromOauth(UmsMember umsMember) {

       umsMemberMapper.insertSelective(umsMember);
       return umsMember;
    }

    /**
     * 检查用户是否已经登陆过
     * @param sourceUid
     * @return
     */
    @Override
    public UmsMember checkUserFormOauth(Long sourceUid) {

        Example example = new Example(UmsMember.class);
        example.createCriteria().andEqualTo("sourceUid", sourceUid);
        return umsMemberMapper.selectOneByExample(example);
    }


}
