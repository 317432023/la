package com.jeetx.common.model.dto;

public class GroupPrivilegeDTO {
	private Integer groupid;
	private String privilegeid;
	public GroupPrivilegeDTO(Integer groupid, String privilegeid) {
		super();
		this.groupid = groupid;
		this.privilegeid = privilegeid;
	}
	public Integer getGroupid() {
		return groupid;
	}
	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}
	public String getPrivilegeid() {
		return privilegeid;
	}
	public void setPrivilegeid(String privilegeid) {
		this.privilegeid = privilegeid;
	}
}

