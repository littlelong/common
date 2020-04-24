package common.xiao.domain;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;

public class EasySet {

	private static Map<String, Method> methodMap = Maps.newHashMap();
	
	public EasySet() {
		super();
		initFiles();
	}

	public void setValueByText(String excelName, String value) {
		try {
			methodMap.get(excelName).invoke(this, value);
		} catch (Exception e) {
		}
	}

	private void initFiles() {
		Method[] methods = this.getClass().getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(ExcelName.class)) {
				method.setAccessible(true);
				methodMap.put(method.getAnnotation(ExcelName.class).name(), method);
			}
		}
	}

}
