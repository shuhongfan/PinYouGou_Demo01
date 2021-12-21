package com.shf.pyg.sellergoods.service;

import com.shf.pyg.entity.PageResult;
import com.shf.pyg.pojo.TbBrand;

import java.util.List;

/**
 * 品牌业务接口
 */
public interface BrandService {
    /**
     * 查询品牌列表
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param PageNum
     * @param PageSize
     * @return
     */
    public PageResult findPage(int PageNum,int PageSize);

    /**
     * 增加品牌
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页条件查询
     * @param PageNum
     * @param PageSize
     * @return
     */
    public PageResult findPage(TbBrand brand,int PageNum,int PageSize);
}
