package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand/")
public class BrandControoler {

	@Reference
	private BrandService brandService;
	
	@RequestMapping("findAll")
	public List<TbBrand> findAll(){
		return brandService.findAllBrands();
	}
	
	@RequestMapping("findPage")
	public PageResult findPage(int pageNum,int pageSize){
		return brandService.findPageBrands(pageNum, pageSize);
	}
	
	@RequestMapping("add")
	public Result add(@RequestBody TbBrand brand){
		try {
			brandService.addBrand(brand);
			return new Result(true,"新增成功");
		} catch (Exception e) {
			return new Result(false,"新增失败");
		}
	}
	
	@RequestMapping("findOne")
	public TbBrand findOne(long id){
		return brandService.findOne(id);
	}
	
	@RequestMapping("update")
	public Result update(@RequestBody TbBrand brand){
		try {
			brandService.update(brand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			return new Result(false,"修改失败");
		}
	}
	
	@RequestMapping("delete")
	public Result delete(Long[] ids){
		try {
			brandService.delete(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			return new Result(false,"删除失败");
		}
	}
	
	
	@RequestMapping("search")
	public PageResult search(@RequestBody TbBrand brand,int pageNum,int pageSize){
		return brandService.findPage(brand, pageNum, pageSize);
	}
	
	@RequestMapping("selectBrandOptionList")
	public List<Map> selectBrandOptionList(){
		return brandService.selectBrandOptionList();
	}
}
