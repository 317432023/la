package com.jeetx.common.model.page;

import java.util.List;

public class QueryResult<T> {
	private List<T> resultData;
	private long resultCount;
	public List<T> getResultData() {
		return resultData;
	}
	public void setResultData(List<T> resultData) {
		this.resultData = resultData;
	}
	public long getResultCount() {
		return resultCount;
	}
	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}
}
