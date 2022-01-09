package com.shf.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.shf.pyg.entity.PageResult;
import com.shf.pyg.mapper.TbSpecificationOptionMapper;
import com.shf.pyg.mapper.TbTypeTemplateMapper;
import com.shf.pyg.pojo.TbSpecificationOption;
import com.shf.pyg.pojo.TbSpecificationOptionExample;
import com.shf.pyg.pojo.TbTypeTemplate;
import com.shf.pyg.pojo.TbTypeTemplateExample;
import com.shf.pyg.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 * @return
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 * @return
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		TbTypeTemplateExample.Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
//		更新redis缓存
		saveToRedis();
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据模板ID查询规格列表
	 * @param id
	 * @return
	 */
	@Override
	public List<Map> findSpecList(Long id) {
//		根据ID查询模板
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
//		得到规格列表  字符串转JSON数组
		List<Map> specList = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);

//		查询规格选项列表
		for (Map spec : specList) {
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
//			添加查询条件,根据规格ID查询
			criteria.andSpecIdEqualTo(new Long((Integer) spec.get("id")));
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
			spec.put("options",options);
		}
		return specList;
	}

	/**
	 * 将品牌列表和规格列表放入缓存
	 */
	private void saveToRedis(){
//		查询所有的模板
		List<TbTypeTemplate> tbTypeTemplates = typeTemplateMapper.selectByExample(null);
		for (TbTypeTemplate tbTypeTemplate : tbTypeTemplates) {
	//		1.品牌列表缓存
			List<Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);

//			2.规格列表缓存
			List<Map> specList = findSpecList(tbTypeTemplate.getId());
			redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(), specList);
		}
		System.out.println("将品牌列表与规格列表放入缓存");
	}
}
