package com.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.UmsMember;
import com.gmall.service.UmsMemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UmsMemberController {

    @Reference
    private UmsMemberService umsMemberService;

    @GetMapping("/get/{id}")
    public UmsMember getAll(@PathVariable String id){
        return umsMemberService.selById(id);
    }
}
