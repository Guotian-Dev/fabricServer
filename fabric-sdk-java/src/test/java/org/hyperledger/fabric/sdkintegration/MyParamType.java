package org.hyperledger.fabric.sdkintegration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * json handle generate paramType
 * @author root
 *
 */
public class MyParamType implements ParameterizedType {

	private final Class raw;
	private final Type[] args;

	public MyParamType(Class raw, Type[] args) {
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
