package com.chainway.dispatchercore.common.pingxx;

import com.google.gson.Gson;

public class GsonUtil {

	private static Gson gson = new Gson();

	public static <T> T jsonToBean(String json, Class<T> cls) {
		return gson.fromJson(json, cls);
	}

	public static <T> String beanToJson(T t) {
		return gson.toJson(t);
	}
}
