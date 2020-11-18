package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {

	public List<TbBrand> findAllBrands();
	
	public PageResult findPageBrands(int pageNum,int pageSize);
	
	public void addBrand(TbBrand brand);
	
	public TbBrand findOne(long id);
	
	public void update(TbBrand brand);
	
	public void delete(Long[] ids);
	
	public PageResult findPage(TbBrand brand,int pageNum,int pageSize);
	
	public List<Map> selectBrandOptionList();
}
