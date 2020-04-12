package com.gmall.service;

import com.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberReceiveAddressService {

    /**
     * 根据用户id查询地址集合
     * @param mid
     * @return
     */
    List<UmsMemberReceiveAddress> selByMid(String mid);

    /**
     * 根据地址id查询地址对象
     * @param addressId
     * @return
     */
    UmsMemberReceiveAddress selAddressInfoByAddressId(String addressId);
}
