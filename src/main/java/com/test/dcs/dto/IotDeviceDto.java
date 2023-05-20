package com.test.dcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IotDeviceDto {
	private String serialNumber;
	private DeviceStatus status;
	private Integer temp;
}