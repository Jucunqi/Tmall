package com.gmall.service;

import com.gmall.bean.PmsBaseCatalog1;
import com.gmall.bean.PmsBaseCatalog2;
import com.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * 处理所有类目表中的数据
 */
public interface PmsBaseCatalogService {
    /**
     * 查询所有一级菜单
     * @return
     */
    List<PmsBaseCatalog1> selAllCataLog1();

    /**
     * 根据一级菜单id查询所有二级菜单
     * @param catalog1Id
     * @return
     */
    List<PmsBaseCatalog2> selAllCatalog2ByCatalog1Id(String catalog1Id);

    /**
     * 根据二级菜单id查询所有三级菜单
     * @param catalog2Id
     * @return
     */
    List<PmsBaseCatalog3> selAllCatalog3ByCatalog2Id(String catalog2Id);
}
