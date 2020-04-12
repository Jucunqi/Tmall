package com.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.UmsMemberReceiveAddress;
import com.gmall.service.UmsMemberReceiveAddressService;
import com.gmall.user.mapper.UmsMemberReceiveAddressMapper;

import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {

    @Resource
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
    @Override
    public List<UmsMemberReceiveAddress> selByMid(String mid) {

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", mid);
        return umsMemberReceiveAddressMapper.selectByExample(example);
    }

    @Override
    public UmsMemberReceiveAddress selAddressInfoByAddressId(String addressId) {

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id",addressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress = umsMemberReceiveAddressMapper.selectOneByExample(example);
        return umsMemberReceiveAddress;
    }
}
