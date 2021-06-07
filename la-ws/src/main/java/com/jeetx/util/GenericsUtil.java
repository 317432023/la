package com.jeetx.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jeetx.service.dao.DaoSupport;
/**
 * 泛型工具类
 *
 */
public class GenericsUtil {
	private static Logger logger = Logger.getLogger(GenericsUtil.class);
	/**  
     * 通过反射,获得指定类的父类的泛型参数的实际类型. 如BuyerServiceBean extends DaoSupport<Buyer>  
     *  
     * @param clazz clazz �?��反射的类,该类必须继承范型父类
     * @param index 泛型参数�?��索引,�?�?��.  
     * @return 范型参数的实际类�? 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回<code>Object.class</code>
     */  
    @SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class<? extends DaoSupport> clazz, int index) {    
        Type genType = clazz.getGenericSuperclass();//得到泛型父类 DaoSupport<Buyer>
        //如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class   
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;   
        }   
        //返回表示此类型实际类型参数的Type对象的数�?数组里放的都是对应类型的Class, 如BuyerServiceBean extends DaoSupport<Buyer,Contact>就返回Buyer和Contact类型   
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();//取得真实的类型参数Buyer        
        if (index >= params.length || index < 0) { 
        	 throw new RuntimeException("你输入的索引"+ (index<0 ? "不能小于0" : "超出了参数的总数"));
        }      
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }   
        return (Class) params[index];
    }
	/**  
     * 通过反射,获得指定类的父类的第�?��泛型参数的实际类�? 如BuyerServiceBean extends DaoSupport<Buyer>  
     *  
     * @param clazz clazz �?��反射的类,该类必须继承泛型父类
     * @return 泛型参数的实际类�? 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回<code>Object.class</code>
     */  
    @SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class<? extends DaoSupport> clazz) {
    	//logger.debug("当前类："+clazz);
    	return getSuperClassGenricType(clazz,0);
    }
	/**  
     * 通过反射,获得方法返回值泛型参数的实际类型. �? public Map<String, Buyer> getNames(){}
     *  
     * @param Method method 方法
     * @param int index 泛型参数�?��索引,�?�?��.
     * @return 泛型参数的实际类�? 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回<code>Object.class</code>
     */ 
    @SuppressWarnings("unchecked")
	public static Class getMethodGenericReturnType(Method method, int index) {
    	Type returnType = method.getGenericReturnType();
    	if(returnType instanceof ParameterizedType){
    	    ParameterizedType type = (ParameterizedType) returnType;
    	    Type[] typeArguments = type.getActualTypeArguments();
            if (index >= typeArguments.length || index < 0) { 
            	 throw new RuntimeException("你输入的索引"+ (index<0 ? "不能小于0" : "超出了参数的总数"));
            } 
    	    return (Class)typeArguments[index];
    	}
    	return Object.class;
    }
	/**  
     * 通过反射,获得方法返回值第�?��泛型参数的实际类�? �? public Map<String, Buyer> getNames(){}
     *  
     * @param Method method 方法
     * @return 泛型参数的实际类�? 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回<code>Object.class</code>
     */ 
    @SuppressWarnings("unchecked")
	public static Class getMethodGenericReturnType(Method method) {
    	return getMethodGenericReturnType(method, 0);
    }
    
	/**  
     * 通过反射,获得方法输入参数第index个输入参数的�?��泛型参数的实际类�? �? public void add(Map<String, Buyer> maps, List<String> names){}
     *  
     * @param Method method 方法
     * @param int index 第几个输入参�?
     * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回空集�?
     */ 
    @SuppressWarnings("unchecked")
	public static List<Class> getMethodGenericParameterTypes(Method method, int index) {
    	List<Class> results = new ArrayList<Class>();
    	Type[] genericParameterTypes = method.getGenericParameterTypes();
    	if (index >= genericParameterTypes.length ||index < 0) {
             throw new RuntimeException("你输入的索引"+ (index<0 ? "不能小于0" : "超出了参数的总数"));
        } 
    	Type genericParameterType = genericParameterTypes[index];
    	if(genericParameterType instanceof ParameterizedType){
    	     ParameterizedType aType = (ParameterizedType) genericParameterType;
    	     Type[] parameterArgTypes = aType.getActualTypeArguments();
    	     for(Type parameterArgType : parameterArgTypes){
    	         Class parameterArgClass = (Class) parameterArgType;
    	         results.add(parameterArgClass);
    	     }
    	     return results;
    	}
    	return results;
    }
	/**  
     * 通过反射,获得方法输入参数第一个输入参数的�?��泛型参数的实际类�? �? public void add(Map<String, Buyer> maps, List<String> names){}
     *  
     * @param Method method 方法
     * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回空集�?
     */ 
    @SuppressWarnings("unchecked")
	public static List<Class> getMethodGenericParameterTypes(Method method) {
    	return getMethodGenericParameterTypes(method, 0);
    }
	/**  
     * 通过反射,获得Field泛型参数的实际类�? �? public Map<String, Buyer> names;
     *  
     * @param Field field 字段
     * @param int index 泛型参数�?��索引,�?�?��.
     * @return 泛型参数的实际类�? 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回<code>Object.class</code>
     */ 
    @SuppressWarnings("unchecked")
	public static Class getFieldGenericType(Field field, int index) {
    	Type genericFieldType = field.getGenericType();
    	
    	if(genericFieldType instanceof ParameterizedType){
    	    ParameterizedType aType = (ParameterizedType) genericFieldType;
    	    Type[] fieldArgTypes = aType.getActualTypeArguments();
    	    if (index >= fieldArgTypes.length || index < 0) { 
    	    	throw new RuntimeException("你输入的索引"+ (index<0 ? "不能小于0" : "超出了参数的总数"));
            } 
    	    return (Class)fieldArgTypes[index];
    	}
    	return Object.class;
    }
	/**  
     * 通过反射,获得Field泛型参数的实际类�? �? public Map<String, Buyer> names;
     *  
     * @param Field field 字段
     * @param int index 泛型参数�?��索引,�?�?��.
     * @return 泛型参数的实际类�? 如果没有实现ParameterizedType接口，即不支持泛型，�?��直接返回<code>Object.class</code>
     */ 
    @SuppressWarnings("unchecked")
	public static Class getFieldGenericType(Field field) {
    	return getFieldGenericType(field, 0);
    }
}
