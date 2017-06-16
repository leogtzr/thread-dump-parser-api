package com.thread.dump.parser.bean;

public class ThreadInfo {

	private String id;
	private String name;
	private String nativeId;
	private String state;
	private String rawData;
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public String getNativeId() {
		return nativeId;
	}
	
	public void setNativeId(final String nativeId) {
		this.nativeId = nativeId;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(final String state) {
		this.state = state;
	}
	
	public String getRawData() {
		return rawData;
	}
	
	public void setRawData(final String rawData) {
		this.rawData = rawData;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ThreadInfo [name=" + name + ", state=" + state + ", id=" + id + ", nativeId=" + nativeId + ", rawData="
				+ rawData + "]";
	}
	
}
