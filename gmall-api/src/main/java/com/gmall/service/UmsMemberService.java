package com.gmall.service;

import com.gmall.bean.UmsMember;


public interface UmsMemberService {

   UmsMember selById(String id);

   /**
    * 查询用户信息，用于用户登录
    * @param umsMember
    * @return
    */
   UmsMember selUserInfo(UmsMember umsMember);

   /**
    * 验证用户是否登录成功
    * @param umsMember
    * @return
    */
   UmsMember login(UmsMember umsMember);

   void addUserToken(String token, String memberId);

   /**
    * 新增第三用户信息
    * @param umsMember
    * @return
    */
   UmsMember addUserFromOauth(UmsMember umsMember);

   /**
    * 检查用户是否已经登录过
    * @param sourceUid
    * @return
    */
   UmsMember checkUserFormOauth(Long sourceUid);



}
