package com.thread.dump.parser.bean;

import java.util.Objects;
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
	private boolean daemon;
	
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

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(final boolean daemon) {
		this.daemon = daemon;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ThreadInfo that = (ThreadInfo) o;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(nativeId, that.nativeId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, nativeId);
	}

	@Override
	public String toString() {
		if (this.isDaemon()) {
			return String.format("Thread Id: '%s' (daemon), Name: '%s', State: '%s'", this.id, this.name, this.state);
		}
		return String.format("Thread Id: '%s', Name: '%s', State: '%s'", this.id, this.name, this.state);
	}
}
