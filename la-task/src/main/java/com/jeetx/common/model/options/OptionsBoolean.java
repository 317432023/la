package com.jeetx.common.model.options;

public class OptionsBoolean {

	private Boolean key;
	private String value;
	
	public OptionsBoolean() {
	}

	public OptionsBoolean(Boolean key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public Boolean getKey() {
		return key;
	}

	public void setKey(Boolean key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
