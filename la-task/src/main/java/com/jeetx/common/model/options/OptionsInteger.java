package com.jeetx.common.model.options;

public class OptionsInteger {

	private Integer key;
	private String value;

	public OptionsInteger() {

	}

	public OptionsInteger(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
