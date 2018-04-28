package com.chainway.fileservice.dto;

import java.io.Serializable;

public interface Render<T> extends Serializable {

	public T genObject();
	
	public void fill(T obj,String key,Object value);
}
