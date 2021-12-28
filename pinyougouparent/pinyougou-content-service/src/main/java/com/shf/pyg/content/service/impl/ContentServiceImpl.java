package com.shf.pyg.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.shf.pyg.content.service.ContentService;
import com.shf.pyg.entity.PageResult;
import com.shf.pyg.mapper.TbContentMapper;
import com.shf.pyg.pojo.TbContent;
import com.shf.pyg.pojo.TbContentExample;
import com.shf.pyg.pojo.TbContentExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//删除此广告所属的分类的广告
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//删除此广告所属的分类的广告(修改后分类)
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());

		//广告修改前ID
		Long oldCategoryId= contentMapper.selectByPrimaryKey(  content.getId()).getCategoryId();
		if(oldCategoryId.longValue()!=content.getCategoryId().longValue()){
			//如果修改前后修改后的分类不一致
			redisTemplate.boundHashOps("content").delete(oldCategoryId);
		}

		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//清除缓存
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId);

			contentMapper.deleteByPrimaryKey(id);
		}		
	}

	@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findByContentCategoryId(Long categoryId) {

		//先到缓存中查询
		List<TbContent> contentList=  (List<TbContent>)redisTemplate.boundHashOps("content").get(categoryId);

		if(contentList!=null){//如果缓存中有数据
			System.out.println("从缓存中提取数据");
			return 	contentList;
		}else{
			TbContentExample example=new TbContentExample();
			example.setOrderByClause("sort_order");//排序

			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			criteria.andStatusEqualTo("1");//状态

			contentList = contentMapper.selectByExample(example);
			System.out.println("从数据库中查询广告并放入缓存");
			//放入缓存
			redisTemplate.boundHashOps("content").put(categoryId,contentList);
			return contentList;
		}


	}

	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
//		指定条件 分类ID
		criteria.andCategoryIdEqualTo(categoryId);
//		指定条件 有效
		criteria.andStatusEqualTo("1");

//		排序
		example.setOrderByClause("sort_order");
		return contentMapper.selectByExample(example);
	}

}
