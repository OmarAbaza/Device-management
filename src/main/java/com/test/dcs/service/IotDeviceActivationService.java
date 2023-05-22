package com.test.dcs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.dcs.dto.DeviceStatus;
import com.test.dcs.dto.IotDeviceDto;
import com.test.dcs.dto.ResponseDto;
import com.test.dcs.exception.DeviceIsNotValidForActivationException;
import com.test.dcs.validation.IotDeviceDataValidator;

@Service
public class IotDeviceActivationService {

	@Value("${warehouse.host.baseurl}")
	private String warehoseServiceHost;

	private String getIotDeviceDetailsUri;
	private String patchIotDeviceDetailsUri;
	private ObjectMapper objectMapper = new ObjectMapper();
	Random randomTemp = new Random();

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	public void configureUris() {
		getIotDeviceDetailsUri = warehoseServiceHost + "/iotdevices/status?serialNumber={serialNumber}";
		patchIotDeviceDetailsUri = warehoseServiceHost + "/iotdevices";
	}

	public ResponseDto activateIotDevice(String deviceSerialNumber)
			throws DeviceIsNotValidForActivationException {
		ResponseDto warehouseResponseDto = retrieveIotDeviceStatusFromWarehouse(deviceSerialNumber);
		if(warehouseResponseDto == null){
			List<String> errors = new ArrayList<>();
			errors.add("Device data retrival from warehouse failed");
			throw new DeviceIsNotValidForActivationException(errors);
		}
		IotDeviceDto iotDeviceDto = objectMapper.convertValue(warehouseResponseDto.getData(), IotDeviceDto.class);
		validateIotDeviceDto(iotDeviceDto);
		iotDeviceDto.setStatus(DeviceStatus.ACTIVE);
		iotDeviceDto.setTemp(deviceTempValueGenerator());
		return patchIotDeviceInWarehouse(iotDeviceDto);
	}

	private ResponseDto retrieveIotDeviceStatusFromWarehouse(String serialNumber) {
		Map<String, String> params = new HashMap<>();
		params.put("serialNumber", serialNumber);
		return restTemplate.getForObject(getIotDeviceDetailsUri, ResponseDto.class, params);
	}

	private ResponseDto patchIotDeviceInWarehouse(IotDeviceDto iotDeviceDto) {
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		return restTemplate.patchForObject(patchIotDeviceDetailsUri, iotDeviceDto, ResponseDto.class);
	}

	private void validateIotDeviceDto(IotDeviceDto iotDeviceDto) throws DeviceIsNotValidForActivationException {
		List<String> validationErrors = IotDeviceDataValidator.getInstance().validateDeviceDto(iotDeviceDto);
		if (!validationErrors.isEmpty()) {
			throw new DeviceIsNotValidForActivationException(validationErrors);
		}
	}

	private int deviceTempValueGenerator() {
		return randomTemp.ints(0, 10).findAny().getAsInt();
	}
}
