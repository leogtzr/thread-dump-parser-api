package com.thread.dump.parser.bean;

import java.util.Optional;

/**
 * @author Leo Gutiérrez
 */
public class ThreadInfo {

	private String id;
	private String name;
	private String nativeId;
	private String state;
	private String stackTrace;
	
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
	
	public Optional<String> getStackTrace() {
		return Optional.<String>ofNullable(stackTrace);
	}
	
	public void setStackTrace(final String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ThreadInfo [name=" + name + ", state=" + state + ", id=" + id + ", nativeId=" + nativeId + ", raw: " + ((stackTrace != null) ? " not null " : " null ");
	}
	
}
