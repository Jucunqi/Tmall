package com.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.PmsBaseCatalog1;
import com.gmall.bean.PmsBaseCatalog2;
import com.gmall.bean.PmsBaseCatalog3;
import com.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.gmall.service.PmsBaseCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PmsBaseCatalogServiceImpl implements PmsBaseCatalogService {
    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    /**
     * 查询出所有一级类目
     * @return
     */
    @Override
    public List<PmsBaseCatalog1> selAllCataLog1() {

        return pmsBaseCatalog1Mapper.selectAll();
    }

    /**
     * 根据一级菜单id查询二级菜单
     * @param catalog1Id
     * @return
     */
    @Override
    public List<PmsBaseCatalog2> selAllCatalog2ByCatalog1Id(String catalog1Id) {

        Example example = new Example(PmsBaseCatalog2.class);
        example.createCriteria().andEqualTo("catalog1Id",catalog1Id);
        return pmsBaseCatalog2Mapper.selectByExample(example);

    }

    /**
     * 根据二级菜单id查询三级菜单
     * @param catalog2Id
     * @return
     */
    @Override
    public List<PmsBaseCatalog3> selAllCatalog3ByCatalog2Id(String catalog2Id) {

        Example example = new Example(PmsBaseCatalog3.class);
        example.createCriteria().andEqualTo("catalog2Id", catalog2Id);
        return pmsBaseCatalog3Mapper.selectByExample(example);
    }
}
