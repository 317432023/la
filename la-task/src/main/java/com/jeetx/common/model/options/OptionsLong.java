package com.jeetx.common.model.options;

public class OptionsLong {

	private Long key;
	private String value;

	public OptionsLong() {

	}

	public OptionsLong(Long key, String value) {
		this.key = key;
		this.value = value;
	}

	public long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
