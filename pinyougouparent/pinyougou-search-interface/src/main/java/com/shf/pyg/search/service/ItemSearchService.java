package com.shf.pyg.search.service;


import java.util.List;
import java.util.Map;

/**
 * 搜索业务接口
 */
public interface ItemSearchService {
    public Map search(Map searchMap);

    /**
     * 导入数据
     */
    public void importList(List list);
}
