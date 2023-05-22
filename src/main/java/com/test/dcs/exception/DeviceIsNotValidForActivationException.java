package com.test.dcs.exception;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeviceIsNotValidForActivationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6299748856644201967L;
	private final List<String> errors;

	public DeviceIsNotValidForActivationException(List<String> errors) {
		this.errors = errors;
	}
}
