package com.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.UmsMemberReceiveAddress;
import com.gmall.service.UmsMemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UmsMemberReceiveAddressController {

    @Reference
    private UmsMemberReceiveAddressService umsMemberReceiveAddressService;

    @RequestMapping("addr/{mid}")
    public List<UmsMemberReceiveAddress> getAddr(@PathVariable String mid){
       return  umsMemberReceiveAddressService.selByMid(mid);

    }
}
