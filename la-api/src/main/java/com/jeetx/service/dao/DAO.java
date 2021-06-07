package com.jeetx.service.dao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import com.jeetx.common.model.page.QueryResult;
/** 
 * 实体操作辅助类
 * @param <T> 实体类
 */
public interface DAO<T> {	
	//----------------------------------基本方法-----------------------------------------//
	
	public void executeUpdate(String sql);
	/**
	 * 保存实体
	 * @param entity 实体id
	 */
	public void save(Object entity);
	/**
	 * 更新实体
	 * @param entity 实体id
	 */
	public void update(Object entity);
	/**
	 * 删除实体
	 */
	public void delete(Object entity);
	/**
	 * 获取实体
	 * @param <T>
	 * @param entityClass 实体类
	 * @param entityId 实体id
	 * @return
	 */
	public T find(Serializable entityId);
	/**
	 * 清除一级缓存的数据
	 */
	public void clear();
	
	//---------------------------------分页方法-------------------------------------//
	
	/**
	 * 获取记录总数
	 * @param entityClass 实体类
	 * @return
	 */
	public List<T> findByHql(String hql);
	
	public List<Object[]> findBySql(String sql);
	
	public List<T> findByHql(String hql,int firstindex, int maxresult);
	
	public long getCountByJql(String jql);

	public long getCount();
	
	public long getCount(Object[] queryParams,String wherejpql);
	
	public List<Object[]> createNativeQuery(String sql);
	
	public QueryResult<T> getScrollData(int firstindex, int maxresult);
	
	public QueryResult<T> getScrollData();
	
	public QueryResult<T> getScrollData(int firstindex, int maxresult, LinkedHashMap<String, String> orderby);
	
	public QueryResult<T> getScrollData(int firstindex, int maxresult, String wherejpql, Object[] queryParams);
	
	public QueryResult<T> getScrollData(int firstindex, int maxresult, String wherejpql, Object[] queryParams,LinkedHashMap<String, String> orderby);
	
	public QueryResult<Object[]> getScrollData(int firstindex, int maxresult, String sql) ;
	
	public Object getSum(String param,Object[] queryParams,String wherejpql);
}
