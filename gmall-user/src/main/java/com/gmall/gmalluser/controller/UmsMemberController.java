package com.gmall.gmalluser.controller;

import com.gmall.bean.UmsMember;
import com.gmall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Id;
import java.util.List;

@RestController
public class UmsMemberController {

    @Autowired
    private UmsMemberService umsMemberService;

    @GetMapping("/get/{id}")
    public UmsMember getAll(@PathVariable String id){
        return umsMemberService.selById(id);
    }
}
