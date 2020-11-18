package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String, Object> map = new HashMap<>();

		/*
		 * Query query=new SimpleQuery(); //添加查询条件 Criteria criterion=new
		 * Criteria("item_keywords").is(searchMap.get("keywords"));
		 * query.addCriteria(criterion); ScoredPage<TbItem> page =
		 * solrTemplate.queryForPage(query, TbItem.class); map.put("row",
		 * page.getContent());
		 */
		//根据关键字搜索列表
		map.putAll(searchList(searchMap));
		//查询分类列表  
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);

		//
		String str = (String) searchMap.get("category");
		if(!"".equals(str)) {
			map.putAll(searchBrandAndSpecList(str));
		}else {
			if(categoryList.size()!=0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		
		
		return map;
	}
	
	/**
	 * 根据关键字搜索列表
	 * @param keywords
	 * @return
	 */
	private Map searchList(Map searchMap){
		Map map=new HashMap();
		String keywords=(String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replaceAll(" ", ""));
		HighlightQuery query=new SimpleHighlightQuery();
		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀 
		highlightOptions.setSimplePostfix("</em>");//高亮后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项
		//按照关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//按分类筛选
		if(!"".equals(searchMap.get("category"))) {
			Criteria category=new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(category);
			query.addFilterQuery(filterQuery);
		}
		
		/*if(!"".equals(searchMap.get("category"))){			
			Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}*/
		
		
		//按品牌筛选
		if(!"".equals(searchMap.get("brand"))) {
			Criteria brand=new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery= new SimpleFilterQuery(brand);
			query.addFilterQuery(filterQuery);
		}
		
		//过滤规格
		if(searchMap.get("spec")!=null) {
			Map<String,String> specMap=(Map<String, String>) searchMap.get("spec");
			for(String key:specMap.keySet()) {
				Criteria spec=new Criteria("item_spec_"+key).is(specMap.get(key));
				FilterQuery filterQuery= new SimpleFilterQuery(spec);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//过滤价格
		if(!"".equals(searchMap.get("price"))) {
			String str=(String) searchMap.get("price");
			String min=str.split("-")[0];
			String max=str.split("-")[1];
			Criteria price=null;
			if(!max.equals("*")) {
				price=new Criteria("item_price").between(min, max);
			}else {
				price=new Criteria("item_price").greaterThanEqual(min);
			}
			FilterQuery filterQuery=new SimpleFilterQuery(price);
			query.addFilterQuery(filterQuery);
		}
		
		//分页
		Integer pageNo = (Integer)searchMap.get("pageNo") ;    ///当前页
		if(pageNo==null) {
			pageNo=1;
		}
		Integer pageSize=(Integer)searchMap.get("pageSize");    //每页显示
		if(pageSize==null) {
			pageSize=20;
		}
		query.setOffset((pageNo-1)*pageSize);
		query.setRows(pageSize);
		
		//排序
		String sortField=(String) searchMap.get("sortField");   //排序字段
		String sortValue=(String) searchMap.get("sort");				//排序模式  asc升  desc降
		
		if(sortValue!=null&&!"".equals(sortValue)) {
			Sort sort=null;
			if(sortValue.equals("ASC")) {
				sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
			}else {
				sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
			}
			query.addSort(sort);
		}
		
		
		//获取高亮结果集
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		for(HighlightEntry<TbItem> h: page.getHighlighted()){//循环高亮入口集合
			TbItem item = h.getEntity();//获取原实体类			
			if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
			}			
		}		
		map.put("rows",page.getContent());
		
		map.put("totalPages", page.getTotalPages());  //返回总页数
		map.put("total", page.getTotalElements());    //返回总记录数
		
		return map;
	}

	
	/**
	 * 查询分类列表  
	 * @param searchMap
	 * @return
	 */
	private List<String> searchCategoryList(Map searchMap) {
		List<String> list=new ArrayList<>();
		Query query=new SimpleQuery();
		//按照关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		
		for(GroupEntry<TbItem> entry:content) {
			list.add(entry.getGroupValue());
		}
		return list;
	}
	
	
	
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询品牌和规格列表
	 * @param category 分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String category){
		Map map=new HashMap();
		
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if(typeId!=null) {
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
		}
		
		return map;
	}

	/**
	 * 导入数据
	 * @param list
	 */
	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List<Long> goodsIdList) {
		Query query=new SimpleQuery();
		Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
		
	}
}
