package com.shf.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shf.pyg.entity.PageResult;
import com.shf.pyg.entity.Result;
import com.shf.pyg.page.service.ItemPageService;
import com.shf.pyg.pojo.TbGoods;
import com.shf.pyg.pojo.TbItem;
import com.shf.pyg.pojogroup.Goods;
import com.shf.pyg.search.service.ItemSearchService;
import com.shf.pyg.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemSearchService itemSearchService;

	@Reference
	private ItemPageService itemPageService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
//		获取商家ID
		String sellerID = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getGoods().setSellerId(sellerID);
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
//		获取商家ID
		String sellerID = SecurityContextHolder.getContext().getAuthentication().getName();
//		首先判断商品是否是该商家的商品
		Goods goods2 = goodsService.findOne(goods.getGoods().getId());
		if (!goods2.getGoods().getSellerId().equals(sellerID) || !goods.getGoods().getSellerId().equals(sellerID)){
			return new Result(false,"非法操作");
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		// 获取商家 ID
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			goodsService.delete(ids,sellerId);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
//		获取商家ID
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);

		return goodsService.findPage(goods, page, rows);		
	}

//	上下架
	@RequestMapping("/updateMarketabel")
	public Result updateMarketable(Long[] ids,String markettable){
		try {
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goodsService.updateMarketable(ids,markettable,sellerId);
//			上架
			if (markettable.equals("1")){
//				查询上架的SKU列表
				List<TbItem> itemList = goodsService.findItemListByGoodsIds(ids);
				if(itemList.size()>0){
					//				导入
					itemSearchService.importList(itemList);
					for (Long id : ids) {
						itemPageService.genItemHtml(id);
					}
				}
			}  else {
//				下架 删除索引数据
				itemSearchService.deleteByGoodsIds(ids);
			}
			return new Result(true,"上架成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"上架失败");
		}
	}

	/**
	 * 生成静态页
	 * @param goodsId
	 */
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		itemPageService.genItemHtml(goodsId);
	}
}
