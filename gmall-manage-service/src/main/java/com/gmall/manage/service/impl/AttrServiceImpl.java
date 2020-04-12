package com.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.PmsBaseAttrInfo;
import com.gmall.bean.PmsBaseAttrValue;
import com.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> selAttrInfoByCatalog3Id(String catalog3Id) {
        Example example = new Example(PmsBaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        //查询出所有属性列表
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectByExample(example);

        //遍历属性列表，给属性值集合赋值
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            //创建查询条件：根据属性列表id查询属性值集合
            Example example1 = new Example(PmsBaseAttrValue.class);
            example1.createCriteria().andEqualTo("attrId", pmsBaseAttrInfo.getId());
            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.selectByExample(example1);
            //将查询到的属性值集合，赋值到属性列表中
            pmsBaseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }

        return pmsBaseAttrInfos;

    }

    /**
     * 新增商品信息
     * @param pmsBaseAttrInfo
     * @return
     */
    @Override
    public int insAttr(PmsBaseAttrInfo pmsBaseAttrInfo) {
        //创建返回值对象
        int  index = 0;

        String id = pmsBaseAttrInfo.getId();
        if(!StringUtils.isEmpty(id)){
            //如果id不为空，进行修改
            //修改属性名
            pmsBaseAttrInfoMapper.updateByPrimaryKey(pmsBaseAttrInfo);

            //修改属性值
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue attrValue : attrValueList) {
                pmsBaseAttrValueMapper.updateByPrimaryKey(attrValue);
            }
        }else {
            //如果id为空，进行新增

            //对传入参数进行非空验证
            String attrName = pmsBaseAttrInfo.getAttrName();
            if (attrName != null && !attrName.equals("")) {
                index = pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
                //        String id = pmsBaseAttrInfo.getId();
                //        System.out.println("插入数据后查询到的id："+id);
            }

            List<PmsBaseAttrValue> list = pmsBaseAttrInfo.getAttrValueList();
            if (list != null && list.size() > 0) {
                //循环遍历集合，进行数据添加
                for (PmsBaseAttrValue value : list) {
                    PmsBaseAttrValue attrValue = new PmsBaseAttrValue();
                    attrValue.setAttrId(pmsBaseAttrInfo.getId());
                    attrValue.setValueName(value.getValueName());
                    index += pmsBaseAttrValueMapper.insertSelective(attrValue);
                }
            }
        }
        return index;
    }

    /**
     * 通过商品属性id查询商品属性值
     * @param attrId
     * @return
     */
    @Override
    public List<PmsBaseAttrValue> selAttrValueByAttrId(String attrId) {
        Example example = new Example(PmsBaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId", attrId);
        return pmsBaseAttrValueMapper.selectByExample(example);
    }

    /**
     * 通过attrValueId查询attr信息
     * @param valudId
     * @return
     */
    @Override
    public List<PmsBaseAttrInfo> getAttrList(String valudId) {
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectAttrList(valudId);
        return pmsBaseAttrInfos;
    }
}
