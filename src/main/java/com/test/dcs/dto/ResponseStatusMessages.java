package com.test.dcs.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseStatusMessages {

	DEVICE_RETRIEVED_SUCCESSFULLY("Device data is retrieved successfully"),
	DEVICE_PATCHED_SUCCESSFULLY("Device data is patched successfully"),
	DEVICE_ACTIVATED_SUCCESSFULLY("Device is activated successfully"),
	DEVICE_NOT_FOUND("Device not found"),
	DATA_VALIDATION_FAILED("Data validation failed"),
	INVALID_DATA("Request contains invalid data"),
	TECHNICAL_FAILURE("Technical failure occurred, Please contact support team"),
	ACCESS_DENIED("User doesn't have the required permission level to perform this action");

	private String message;

	private ResponseStatusMessages(String message) {
		this.message = message;
	}

	@JsonValue
	public String getMessage() {
		return this.message;
	}

}
