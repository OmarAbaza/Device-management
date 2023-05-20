package com.test.dcs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.dcs.dto.DeviceStatus;
import com.test.dcs.dto.IotDeviceDto;
import com.test.dcs.dto.ResponseDto;
import com.test.dcs.exception.DeviceDataMissingException;
import com.test.dcs.exception.DeviceAlreadyActiveException;
import com.test.dcs.validation.IotDeviceDataValidator;

@Service
public class IotDeviceActivationService {

	@Value("${warehouse.host.baseurl}")
	private String warehoseServiceHost;

	private String getIotDeviceDetailsUri;
	private String patchIotDeviceDetailsUri;
	private ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	private void configureUris() {
		getIotDeviceDetailsUri = warehoseServiceHost + "/iotdevices/status?serialNumber={serialNumber}";
		patchIotDeviceDetailsUri = warehoseServiceHost + "/iotdevices";
	}

	public ResponseDto activateIotDevice(String deviceSerialNumber)
			throws DeviceAlreadyActiveException, DeviceDataMissingException {
		ResponseDto warehouseResponseDto = retrieveIotDeviceStatusFromWarehouse(deviceSerialNumber);
		IotDeviceDto iotDeviceDto = objectMapper.convertValue(warehouseResponseDto.getData(), IotDeviceDto.class);
		validateIotDeviceDto(iotDeviceDto);
		iotDeviceDto.setStatus(DeviceStatus.ACTIVE);
		iotDeviceDto.setTemp(deviceTempValueGenerator());
		ResponseDto patchUpdatesResponse = patchIotDeviceInWarehouse(iotDeviceDto);
		return patchUpdatesResponse;
	}

	private ResponseDto retrieveIotDeviceStatusFromWarehouse(String serialNumber) {
		Map<String, String> params = new HashMap<>();
		params.put("serialNumber", serialNumber);
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(getIotDeviceDetailsUri, ResponseDto.class, params);
	}

	private ResponseDto patchIotDeviceInWarehouse(IotDeviceDto iotDeviceDto) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseDto responseDto = restTemplate.patchForObject(patchIotDeviceDetailsUri, iotDeviceDto, ResponseDto.class);
		return responseDto;
	}

	private void validateIotDeviceDto(IotDeviceDto iotDeviceDto) throws DeviceDataMissingException {
		List<String> validationErrors = IotDeviceDataValidator.getInstance().validateDeviceDto(iotDeviceDto);
		if (validationErrors.size() > 0) {
			throw new DeviceDataMissingException(validationErrors);
		}
	}

	private int deviceTempValueGenerator() {
		Random randomTemp = new Random();
		return randomTemp.ints(0, 10).findAny().getAsInt();
	}
}
