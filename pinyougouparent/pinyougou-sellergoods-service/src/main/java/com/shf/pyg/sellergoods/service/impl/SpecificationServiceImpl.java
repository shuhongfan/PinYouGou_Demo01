package com.shf.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.shf.pyg.entity.PageResult;
import com.shf.pyg.mapper.TbSpecificationMapper;
import com.shf.pyg.mapper.TbSpecificationOptionMapper;
import com.shf.pyg.pojo.TbSpecification;
import com.shf.pyg.pojo.TbSpecificationExample;
import com.shf.pyg.pojo.TbSpecificationOption;
import com.shf.pyg.pojo.TbSpecificationOptionExample;
import com.shf.pyg.pojogroup.Specification;
import com.shf.pyg.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 * @return
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 * @return
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
//		存储规格实体
		specificationMapper.insert(specification.getSpecification());

//		存储规格选项列表
		for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
//			设置规格ID
			specificationOption.setSpecId(specification.getSpecification().getId());
			specificationOptionMapper.insert(specificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
//		修改规格
		specificationMapper.updateByPrimaryKey(specification.getSpecification());

//		删除此规格的所有规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
//		指定规格id
		criteria.andSpecIdEqualTo(specification.getSpecification().getId());
//		删除
		specificationOptionMapper.deleteByExample(example);


//		重新添加
		for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
			tbSpecificationOption.setSpecId(specification.getSpecification().getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
//		查询规格
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
//		查询规格选项列表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
//		根据规格ID查询
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);

//		封装组合实体类
		Specification specification = new Specification();
		specification.setSpecification(tbSpecification);
		specification.setSpecificationOptionList(specificationOptionList);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
//			删除规格
			specificationMapper.deleteByPrimaryKey(id);

//			删除此规格的选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		TbSpecificationExample.Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
