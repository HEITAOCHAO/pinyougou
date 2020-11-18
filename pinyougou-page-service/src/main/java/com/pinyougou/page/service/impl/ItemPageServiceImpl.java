package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;

@org.springframework.stereotype.Service
public class ItemPageServiceImpl implements ItemPageService {

	private String pagedir="d:\\item\\";

	@Autowired  private FreeMarkerConfig freeMarkerConfig;
	@Autowired	private TbGoodsMapper goodsMapper;
	@Autowired 	private TbGoodsDescMapper goodsDescMapper;
	@Autowired 	private TbItemCatMapper itemCatMapper;
	@Autowired 	private TbItemMapper itemMapper;
	@Override
	public boolean getItemHtml(Long goodsId) {
		try {
			//1.创建配置类
			Configuration config=freeMarkerConfig.getConfiguration();
			//2.设置模板所在的目录 
			Template template=config.getTemplate("item.ftl");
			
			Map<String,Object> map=new HashMap<>();
			//1.加载商品表数据
			TbGoods goods=goodsMapper.selectByPrimaryKey(goodsId);
			map.put("goods", goods);
			//2.加载商品扩展表数据			
			map.put("goodsDesc", goodsDescMapper.selectByPrimaryKey(goodsId));
			//3.商品分类
			TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
			TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
			TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
			map.put("itemCat1", itemCat1.getName());
			map.put("itemCat2", itemCat2.getName());
			map.put("itemCat3", itemCat3.getName());
			//4.SKU列表		
			TbItemExample example=new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");
			criteria.andGoodsIdEqualTo(goodsId);
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example);
			map.put("itemList", itemList);
			
			
			Writer out=new FileWriter(pagedir+goodsId+".html");
			template.process(map, out);
			out.close();
			return true;
		} catch (Exception e) {
			e.getStackTrace();
			return false;
		}
		
	}
	@Override
	public boolean deleteItemHtml(Long[] goodsIds) {
		try {
			for(Long id:goodsIds) {
				new File(pagedir+id+".html").delete();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}

}
