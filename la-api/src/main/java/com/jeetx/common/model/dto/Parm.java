package com.jeetx.common.model.dto;

public class Parm implements Comparable<Parm> {
		private Integer id;
		private String text;
		private String url;
		private Integer fragmentsNum;
		private Integer RedNum;
		public int compareTo(Parm parm){
			return this.id.compareTo(parm.id); // 
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public Integer getFragmentsNum() {
			return fragmentsNum;
		}
		public void setFragmentsNum(Integer fragmentsNum) {
			this.fragmentsNum = fragmentsNum;
		}
		public Integer getRedNum() {
			return RedNum;
		}
		public void setRedNum(Integer redNum) {
			RedNum = redNum;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer x) {
			this.id = x;
		}


}
