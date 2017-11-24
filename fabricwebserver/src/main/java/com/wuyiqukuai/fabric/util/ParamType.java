package com.wuyiqukuai.fabric.util;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * json handle generate paramType
 * @author root
 *
 */
public class ParamType implements ParameterizedType {

	@SuppressWarnings("rawtypes")
	private final Class raw;
	private final Type[] args;

	@SuppressWarnings("rawtypes")
	public ParamType(Class raw, Type[] args) {
		this.raw = raw;
		this.args = args != null ? args : new Type[0];
	}

	@Override
	public Type[] getActualTypeArguments() {
		return args;
	}

	@Override
	public Type getRawType() {
		return raw;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}

}
