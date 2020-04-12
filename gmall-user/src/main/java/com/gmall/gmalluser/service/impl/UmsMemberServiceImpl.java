package com.gmall.gmalluser.service.impl;

import com.gmall.bean.UmsMember;
import com.gmall.gmalluser.mapper.UmsMemberMapper;
import com.gmall.service.UmsMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class UmsMemberServiceImpl {


    private UmsMemberMapper umsMemberMapper;


    public UmsMember selById(String id) {
        return umsMemberMapper.selectByPrimaryKey(id);
    }
}
