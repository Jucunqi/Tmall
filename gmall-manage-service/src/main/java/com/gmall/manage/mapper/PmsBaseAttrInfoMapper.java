package com.gmall.manage.mapper;

import com.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    /**
     * 通过attrValueId查询attr信息
     * @param attrValueId
     * @return
     */
    List<PmsBaseAttrInfo> selectAttrList(@Param("attrValueId") String attrValueId);
}
