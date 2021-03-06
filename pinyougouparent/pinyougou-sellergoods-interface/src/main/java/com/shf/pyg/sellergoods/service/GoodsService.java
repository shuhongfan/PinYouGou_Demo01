package com.shf.pyg.sellergoods.service;

import com.shf.pyg.entity.PageResult;
import com.shf.pyg.pojo.TbGoods;
import com.shf.pyg.pojo.TbItem;
import com.shf.pyg.pojogroup.Goods;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);


	/**
	 * 修改状态
	 * @param ids
	 * @param status
	 */
	public void updateStatus(Long[] ids,String status);

	/**
	 * 根据spu_id数组查询sku列表
	 * @param goodsIds
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIds(Long[] goodsIds);

	/**
	 *  更改上架状态
	 * @param ids 主键数组
	 * @param marketable 上架状态
	 * @param sellerId 商家ID
	 */
	public void updateMarketable(Long []ids ,String marketable,String sellerId);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids,String sellerId);
}
