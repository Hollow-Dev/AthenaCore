package net.athenamc.core.exceptions;

import lombok.Getter;

public class PermissionRequiredException extends Exception {
	private static final long serialVersionUID = -3958122966745241855L;
	
	@Getter
	private String permission;
	
	public PermissionRequiredException(String permission) {
		this.permission = permission;
	}
}
