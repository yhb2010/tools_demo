package com.demo.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.esotericsoftware.reflectasm.MethodAccess;

//属性拷贝
public class ReflectASMBeanUtil {

	private static Map<String, MethodAccess> methodAccessMap = new HashMap<String, MethodAccess>();

	private static Map<String, List<FieldInfo>> fieldInfoMap = new HashMap<String, List<FieldInfo>>();

	private static Map<String, Map<String, Integer>> fieldIndexMap = new HashMap<String, Map<String, Integer>>();

	public static void copyProperties(Object desc, Object src) {

		Class<? extends Object> descClass = desc.getClass();
		Class<? extends Object> srcClass = src.getClass();

		MethodAccess descMethodAccess = getMethodAccessByCache(descClass);
		MethodAccess srcMethodAccess = getMethodAccessByCache(srcClass);

		List<FieldInfo> fieldInfos = fieldInfoMap.get(srcClass.getName());
		for (FieldInfo fieldInfo : fieldInfos) {
			// 调用源对象get方法获取值
			Object srcFieldValue = srcMethodAccess.invoke(src, fieldInfo.getGetterIndex());
			try {
				// 根据setter方法名称查询fieldIndex，调用目标对象set方法存入值
				descMethodAccess.invoke(desc, (Integer) fieldIndexMap.get(descClass.getName()).get(fieldInfo.getSetterMethodName()), srcFieldValue);
			} catch (Exception e) {}
		}
    }

    private static MethodAccess getMethodAccessByCache(Class<? extends Object> clazz) {
    	MethodAccess orgiMethodAccess = methodAccessMap.get(clazz.getName());
        if (orgiMethodAccess == null) {
        	synchronized (clazz) {
        		if (orgiMethodAccess == null) {
        			orgiMethodAccess = cache(clazz);
        		}
        	}
        }
        return orgiMethodAccess;
    }

	// 单例模式
	private static MethodAccess cache(Class<? extends Object> clazz) {
		MethodAccess methodAccess = MethodAccess.get(clazz);
		methodAccessMap.put(clazz.getName(), methodAccess);

		List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
		Map<String, Integer> indexMap = new HashMap<String, Integer>();

		List<String> fieldNames = getFieldNames(clazz);
		Iterator<String> iterator = fieldNames.iterator();
		while (iterator.hasNext()) {
			String fieldName = iterator.next();

			// 获取getter/setter方法名
			String getterMethodName = "get" + StringUtils.capitalize(fieldName);
			String setterMethodName = "set" + StringUtils.capitalize(fieldName);

			// 获取getter/setter方法对应的index
			Integer getterIndex;
			Integer setterIndex;
			try {
				getterIndex = methodAccess.getIndex(getterMethodName);
				setterIndex = methodAccess.getIndex(setterMethodName);

				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.setSetterMethodName(setterMethodName);
				fieldInfo.setGetterIndex(getterIndex);
				fieldInfos.add(fieldInfo);

				// setter方法名称 -> setterIndex
				indexMap.put(setterMethodName, setterIndex);
			} catch (Exception e) {
				// 无匹配的getter/setter方法时，视为非标准属性或无法复制的属性，直接从属性列表中移出
				iterator.remove();
			}
		}
		fieldInfoMap.put(clazz.getName(), fieldInfos); // 将类名，属性名称注册到map中
		fieldIndexMap.put(clazz.getName(), indexMap);
		return methodAccess;
	}

	private static List<String> getFieldNames(Class<? extends Object> clazz) {
		List<String> fieldList = new ArrayList<String>();
		for( ; clazz != Object.class; clazz = clazz.getSuperclass()) {
			for (Field field : clazz.getDeclaredFields()) {
				fieldList.add(field.getName());
			}
		}
		return fieldList;
	}


	private static class FieldInfo {

		private String setterMethodName;
		private Integer getterIndex;

		public String getSetterMethodName() {
			return setterMethodName;
		}
		public void setSetterMethodName(String setterMethodName) {
			this.setterMethodName = setterMethodName;
		}
		public Integer getGetterIndex() {
			return getterIndex;
		}
		public void setGetterIndex(Integer getterIndex) {
			this.getterIndex = getterIndex;
		}

	}

}
