package com.gmall.gmalluser.service.impl;

import com.gmall.bean.UmsMemberReceiveAddress;
import com.gmall.gmalluser.mapper.UmsMemberReceiveAddressMapper;
import com.gmall.service.UmsMemberReceiveAddressService;
import org.springframework.stereotype.Service;
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
        return null;
    }
}
