package com.shf.pyg.pojogroup;

import com.shf.pyg.pojo.TbSpecification;
import com.shf.pyg.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 规格组合实体类
 */
public class Specification implements Serializable {
//    规格
    private TbSpecification specification;
//    规格选项列表
    private List<TbSpecificationOption> specificationOptionList;

    public Specification() {
    }

    public Specification(TbSpecification specification, List<TbSpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "specification=" + specification +
                ", specificationOptionList=" + specificationOptionList +
                '}';
    }
}
