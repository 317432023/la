package com.jeetx.service.dao;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.common.model.page.QueryResult;
import com.jeetx.util.GenericsUtil;

@Repository
public abstract class DaoSupport<T> implements DAO<T>{
	public static Logger log = Logger.getLogger(DaoSupport.class);
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	public Session getSession() {
		// 事务必须是开启的(Required)，否则获取不到
		return sessionFactory.getCurrentSession();
	}
	
	protected Class<T> entityClass = GenericsUtil.getSuperClassGenricType(this.getClass());
	
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public void executeUpdate(String sql) {
		getSession().createSQLQuery(sql).executeUpdate();
	}
	
	//----------------------------------基本方法-----------------------------------------//
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public void save(Object entity) {
		 getSession().saveOrUpdate(entity);		
	}
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public void update(Object entity) {
		getSession().saveOrUpdate(entity);
	}	
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public void delete(Object entity) {
		getSession().delete(entity);
	}
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public T find(Serializable entityId) {
		return (T) getSession().get(entityClass, entityId);
	}
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public void clear() {
		getSession().clear();
	}
	
	//-------------------------------分页方法---------------------------------------//
	
	public List<T> findByHql(String hql) {
		List<T> list = getSession().createQuery(hql).list();
		return list;
	}
	
	public List<Object[]> findBySql(String sql) {
		return getSession().createSQLQuery(sql).list();
	}
	
	public List<T> findByHql(String hql,int firstindex, int maxresult) {
		List<T> list = getSession().createQuery(hql).setFirstResult(firstindex).setMaxResults(maxresult).list();
		return list;
	}

	public Object getSum(String param,Object[] queryParams,String wherejpql) {
		Query query = getSession().createQuery("select sum("+ param+ ") from "+ getEntityName(this.entityClass)+ " o "+(wherejpql==null || "".equals(wherejpql.trim())? "": "where 1=1 "+ wherejpql));
		setQueryParams(query, queryParams);
		return query.uniqueResult();
	}
	
	public long getCount() {
		return (Long)getSession().createQuery("select count("+ getCountField(this.entityClass) +") from "+ getEntityName(this.entityClass)+ " o").uniqueResult();
	}
	
	public List<Object[]> createNativeQuery(String sql) {
		List<Object[]> list = getSession().createSQLQuery(sql).list();
		return list;
	}
	
	public long getCountByJql(String jql) {
		return (Long)getSession().createSQLQuery(jql).uniqueResult();
	}
	
	public long getCount(Object[] queryParams,String wherejpql) {
		Query query = getSession().createQuery("select count("+ getCountField(this.entityClass)+ ") from "+ getEntityName(this.entityClass)+ " o "+(wherejpql==null || "".equals(wherejpql.trim())? "": "where 1=1 "+ wherejpql));
		setQueryParams(query, queryParams);
		return (Long)query.uniqueResult();
	}
	
	protected static <E> String getCountField(Class<E> clazz){
		String out = "o";
		try {
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
			for(PropertyDescriptor propertydesc : propertyDescriptors){
				Method method = propertydesc.getReadMethod();
				if(method!=null && method.isAnnotationPresent(EmbeddedId.class)){					
					PropertyDescriptor[] ps = Introspector.getBeanInfo(propertydesc.getPropertyType()).getPropertyDescriptors();
					out = "o."+ propertydesc.getName()+ "." + (!ps[1].getName().equals("class")? ps[1].getName(): ps[0].getName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return out;
	}
	/**
	 * 获取实体的名称
	 * @param <E>
	 * @param clazz 实体类
	 * @return
	 */
	protected static <E> String getEntityName(Class<E> clazz){
		String entityname = clazz.getSimpleName();
		//annotation---注解
		Entity entity = clazz.getAnnotation(Entity.class);
		if(entity.name()!=null && !"".equals(entity.name().trim())){
			entityname = entity.name();
		}
		return entityname;
	}
	
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<T> getScrollData(int firstindex, int maxresult) {
		return getScrollData(firstindex,maxresult,null,null,null);
	}
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<T> getScrollData() {
		return getScrollData(-1, -1);
	}
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<T> getScrollData(int firstindex, int maxresult, LinkedHashMap<String, String> orderby) {
		return getScrollData(firstindex,maxresult,null,null,orderby);
	}
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<T> getScrollData(int firstindex, int maxresult, String wherejpql, Object[] queryParams) {
		return getScrollData(firstindex,maxresult,wherejpql,queryParams,null);
	}
	
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<T> getScrollData(int firstindex, int maxresult
			, String wherejpql, Object[] queryParams,LinkedHashMap<String, String> orderby) {
		QueryResult<T> qr = new QueryResult<T>();
		String entityname = getEntityName(this.entityClass);
		
		//System.out.println("select o from "+ entityname+ " o "+(wherejpql==null || "".equals(wherejpql.trim())? "": "where 1=1 "+ wherejpql)+ buildOrderby(orderby));
		Query query = getSession().createQuery("select o from "+ entityname+ " o "+(wherejpql==null || "".equals(wherejpql.trim())? "": "where 1=1 "+ wherejpql)+ buildOrderby(orderby));
		
		setQueryParams(query, queryParams);
		
		/**获取所有的记录**/
		if(firstindex!=-1 && maxresult!=-1) {
			query.setFirstResult(firstindex).setMaxResults(maxresult);
		}
		
		qr.setResultData(query.list());
		
		query = getSession().createQuery("select count("+ getCountField(this.entityClass)+ ") from "+ entityname+ " o "+(wherejpql==null || "".equals(wherejpql.trim())? "": "where 1=1 "+ wherejpql));
		setQueryParams(query, queryParams);
		qr.setResultCount((Long)query.uniqueResult());
		return qr;
	}
	
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Object[]> getScrollData(int firstindex, int maxresult, String sql) {
		QueryResult<Object[]> qr = new QueryResult<Object[]>();
		
		Query query = getSession().createSQLQuery(sql);
		/**获取所有的记录**/
		if(firstindex!=-1 && maxresult!=-1) {
			query.setFirstResult(firstindex).setMaxResults(maxresult);
		}
		//System.out.println(query.getResultList().size());
		qr.setResultData(query.list());
		
		query = getSession().createSQLQuery("select count(*) from ("+sql+") tt");
		qr.setResultCount(((BigInteger)query.uniqueResult()).longValue());
		return qr;
	}
	
	/**
	 * 设置参数
	 * @param query
	 * @param queryParams 参数数组
	 */
	protected static void setQueryParams(Query query, Object[] queryParams){
		if(queryParams!=null && queryParams.length>0){
			for(int i=0; i<queryParams.length; i++){
				query.setParameter(i, queryParams[i]);
			}
		}
	}
	/**
	 * 封装order by语句，建立排序语句
	 * order by o.id desc, o.date desc
	 * @param orderby <key, value>,如<id, desc>,<date,asc>
	 * @return
	 */
	protected static String buildOrderby(LinkedHashMap<String, String> orderby){
		StringBuffer orderbyql = new StringBuffer("");
		if(orderby!=null && orderby.size()>0){
			orderbyql.append(" order by ");
			for(String key : orderby.keySet()){
				orderbyql.append("o.").append(key).append(" ").append(orderby.get(key)).append(",");
			}
			orderbyql.deleteCharAt(orderbyql.length()-1);//删除最后一个字符
		}
		return orderbyql.toString();
	}
	
}
