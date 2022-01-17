package com.shf.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.shf.pyg.entity.PageResult;
import com.shf.pyg.mapper.TbSeckillGoodsMapper;
import com.shf.pyg.pojo.TbSeckillGoods;
import com.shf.pyg.pojo.TbSeckillGoodsExample;
import com.shf.pyg.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillGoods> page=   (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id){
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
		
		if(seckillGoods!=null){			
						if(seckillGoods.getTitle()!=null && seckillGoods.getTitle().length()>0){
				criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(seckillGoods.getSmallPic()!=null && seckillGoods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(seckillGoods.getSellerId()!=null && seckillGoods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(seckillGoods.getStatus()!=null && seckillGoods.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(seckillGoods.getIntroduction()!=null && seckillGoods.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
		
		Page<TbSeckillGoods> page= (Page<TbSeckillGoods>)seckillGoodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询正在参与秒杀的商品
	 * @return
	 */
	@Override
	public List<TbSeckillGoods> findList() {
		//从缓存中
		List<TbSeckillGoods>  seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

//		缓存中没有数据 查询数据库
		if(seckillGoodsList==null || seckillGoodsList.size()==0){
			TbSeckillGoodsExample example=new TbSeckillGoodsExample();
			TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");//已审核
			criteria.andStockCountGreaterThan(0);//库存大于0
			criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
			criteria.andEndTimeGreaterThan(new Date());//截止时间大于当前时间

			seckillGoodsList = seckillGoodsMapper.selectByExample(example);

//			放入缓存
			for(TbSeckillGoods seckillGoods:seckillGoodsList){
				redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
			}
			System.out.println("从数据库中查询放入缓存");
		}else{
			System.out.println("从缓存查询");
		}
		return seckillGoodsList;
	}

	/**
	 * 根据ID查询商品（从redis）
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOneFromRedis(Long id) {
		return (TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(id);
	}

}
