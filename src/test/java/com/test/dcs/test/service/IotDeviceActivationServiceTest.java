package com.test.dcs.test.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.test.dcs.dto.DeviceStatus;
import com.test.dcs.dto.IotDeviceDto;
import com.test.dcs.dto.ResponseDto;
import com.test.dcs.dto.ResponseStatusMessages;
import com.test.dcs.exception.DeviceIsNotValidForActivationException;
import com.test.dcs.service.IotDeviceActivationService;

@ExtendWith(MockitoExtension.class)
public class IotDeviceActivationServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private IotDeviceActivationService deviceActivationService;

	@Captor
	ArgumentCaptor<IotDeviceDto> iotDeviceDtoCaptor;

	private final String DEVICE_SERIAL = "123123";

	@BeforeEach
	public void testSetup() {
		deviceActivationService.configureUris();
	}

	@Test
	public void whenAValidReadyDeviceIsSent_thenDeviceActivatedSuccessfully() throws DeviceIsNotValidForActivationException {
		ResponseDto retrivalResponseDto= generateSuccessResponseDtoWithReadyDeviceMock();
		ResponseDto patchResponseDto = generateDevicePatchedResponseDtoMock(retrivalResponseDto);
		when(restTemplate.getForObject(any(), eq(ResponseDto.class),anyMap())).thenReturn(retrivalResponseDto);
		when(restTemplate.patchForObject(any(String.class), any(),  eq(ResponseDto.class))).thenReturn(patchResponseDto);
		ResponseDto actual = deviceActivationService.activateIotDevice(DEVICE_SERIAL);
		verify(restTemplate,times(1)).getForObject(any(String.class),eq(ResponseDto.class),anyMap());
		verify(restTemplate,times(1)).patchForObject(any(String.class), iotDeviceDtoCaptor.capture(),  eq(ResponseDto.class));
		assertEquals(DeviceStatus.ACTIVE, iotDeviceDtoCaptor.getValue().getStatus());
		assertEquals(patchResponseDto, actual);
		assertTrue(iotDeviceDtoCaptor.getValue().getTemp() >= 0 && iotDeviceDtoCaptor.getValue().getTemp() <= 10);
	}

	@Test
	public void whenAValidActiveDeviceIsSent_thenDeviceIsNotValidForActivationExceptionIsThrown() {
		ResponseDto retrivalResponseDto= generateSuccessResponseDtoWithActiveDeviceMock();
		when(restTemplate.getForObject(any(), eq(ResponseDto.class),anyMap())).thenReturn(retrivalResponseDto);
		DeviceIsNotValidForActivationException thrown = assertThrows(DeviceIsNotValidForActivationException.class, () -> {deviceActivationService.activateIotDevice(DEVICE_SERIAL);});
		verify(restTemplate,times(1)).getForObject(any(String.class),eq(ResponseDto.class),anyMap());
		verify(restTemplate,times(0)).patchForObject(any(), any(),  any());
		assertEquals(1,thrown.getErrors().size());
		assertTrue(thrown.getErrors().contains("Device is already activated"));
	}

	@Test
	public void whenAnyOfTheDeviceDataIsMissing_thenDeviceIsNotValidForActivationExceptionIsThrown() {
		ResponseDto retrivalResponseDto= generateSuccessResponseDtoWithMissingDataMock();
		when(restTemplate.getForObject(any(), eq(ResponseDto.class),anyMap())).thenReturn(retrivalResponseDto);
		DeviceIsNotValidForActivationException thrown = assertThrows(DeviceIsNotValidForActivationException.class, () -> {deviceActivationService.activateIotDevice(DEVICE_SERIAL);});
		verify(restTemplate,times(1)).getForObject(any(String.class),eq(ResponseDto.class),anyMap());
		verify(restTemplate,times(0)).patchForObject(any(), any(),  any());
		assertEquals(2,thrown.getErrors().size());
		assertTrue(thrown.getErrors().contains("Device status is required"));
		assertTrue(thrown.getErrors().contains("Device serial number is missing"));
	}

	@Test
	public void whenTheDeviceDataIsMissing_thenDeviceIsNotValidForActivationExceptionIsThrown() {
		ResponseDto retrivalResponseDto= generateSuccessResponseDtoWithMissingDataMock();
		when(restTemplate.getForObject(any(), eq(ResponseDto.class),anyMap())).thenReturn(retrivalResponseDto);
		DeviceIsNotValidForActivationException thrown = assertThrows(DeviceIsNotValidForActivationException.class, () -> {deviceActivationService.activateIotDevice(DEVICE_SERIAL);});
		verify(restTemplate,times(1)).getForObject(any(String.class),eq(ResponseDto.class),anyMap());
		verify(restTemplate,times(0)).patchForObject(any(), any(),  any());
		assertEquals(2,thrown.getErrors().size());
		assertTrue(thrown.getErrors().contains("Device status is required"));
		assertTrue(thrown.getErrors().contains("Device serial number is missing"));
	}

	@Test
	public void whenAnyOfTheDeviceDataIsNotValid_thenDeviceIsNotValidForActivationExceptionIsThrown() {
		ResponseDto retrivalResponseDto= generateSuccessResponseDtoWithBadDataMock();
		when(restTemplate.getForObject(any(), eq(ResponseDto.class),anyMap())).thenReturn(retrivalResponseDto);
		DeviceIsNotValidForActivationException thrown = assertThrows(DeviceIsNotValidForActivationException.class, () -> {deviceActivationService.activateIotDevice(DEVICE_SERIAL);});
		verify(restTemplate,times(1)).getForObject(any(String.class),eq(ResponseDto.class),anyMap());
		verify(restTemplate,times(0)).patchForObject(any(), any(),  any());
		assertEquals(1,thrown.getErrors().size());
		assertTrue(thrown.getErrors().contains("Device serial number should contain digits only"));
	}

	@Test
	public void whenTheDeviceDataIsNotFound_thenDeviceIsNotValidForActivationExceptionIsThrown() {
		ResponseDto retrivalResponseDto= generateDeviceNotFoundResponseDtoMock();
		when(restTemplate.getForObject(any(), eq(ResponseDto.class),anyMap())).thenReturn(retrivalResponseDto);
		DeviceIsNotValidForActivationException thrown = assertThrows(DeviceIsNotValidForActivationException.class, () -> {deviceActivationService.activateIotDevice(DEVICE_SERIAL);});
		verify(restTemplate,times(1)).getForObject(any(String.class),eq(ResponseDto.class),anyMap());
		verify(restTemplate,times(0)).patchForObject(any(), any(),  any());
		assertEquals(1,thrown.getErrors().size());
		assertTrue(thrown.getErrors().contains("Device data is missing"));
	}

	@Test
	public void whenTheDeviceDataRetrievalFails_thenDeviceIsNotValidForActivationExceptionIsThrown() {
		when(restTemplate.getForObject(any(), eq(ResponseDto.class),anyMap())).thenReturn(null);
		DeviceIsNotValidForActivationException thrown = assertThrows(DeviceIsNotValidForActivationException.class, () -> {deviceActivationService.activateIotDevice(DEVICE_SERIAL);});
		verify(restTemplate,times(1)).getForObject(any(String.class),eq(ResponseDto.class),anyMap());
		verify(restTemplate,times(0)).patchForObject(any(), any(),  any());
		assertEquals(1,thrown.getErrors().size());
		assertTrue(thrown.getErrors().contains("Device data retrival from warehouse failed"));
	}

	private ResponseDto generateSuccessResponseDtoWithActiveDeviceMock() {
		IotDeviceDto iotDeviceDto = new IotDeviceDto();
		iotDeviceDto.setSerialNumber(DEVICE_SERIAL);
		iotDeviceDto.setStatus(DeviceStatus.ACTIVE);
		iotDeviceDto.setTemp(8);
		return new ResponseDto(true, ResponseStatusMessages.DEVICE_RETRIEVED_SUCCESSFULLY, iotDeviceDto);
	}

	private ResponseDto generateSuccessResponseDtoWithReadyDeviceMock() {
		IotDeviceDto iotDeviceDto = new IotDeviceDto();
		iotDeviceDto.setSerialNumber(DEVICE_SERIAL);
		iotDeviceDto.setStatus(DeviceStatus.READY);
		iotDeviceDto.setTemp(-1);
		return new ResponseDto(true, ResponseStatusMessages.DEVICE_RETRIEVED_SUCCESSFULLY, iotDeviceDto);
	}

	private ResponseDto generateSuccessResponseDtoWithMissingDataMock() {
		IotDeviceDto iotDeviceDto = new IotDeviceDto();
		return new ResponseDto(true, ResponseStatusMessages.DEVICE_RETRIEVED_SUCCESSFULLY, iotDeviceDto);
	}

	private ResponseDto generateSuccessResponseDtoWithBadDataMock() {
		IotDeviceDto iotDeviceDto = new IotDeviceDto();
		iotDeviceDto.setStatus(DeviceStatus.READY);
		iotDeviceDto.setSerialNumber("test");
		return new ResponseDto(true, ResponseStatusMessages.DEVICE_RETRIEVED_SUCCESSFULLY, iotDeviceDto);
	}

	private ResponseDto generateDevicePatchedResponseDtoMock(Object data) {
		return new ResponseDto(true, ResponseStatusMessages.DEVICE_PATCHED_SUCCESSFULLY, data);
	}
	private ResponseDto generateDeviceNotFoundResponseDtoMock() {
		return new ResponseDto(true, ResponseStatusMessages.DEVICE_NOT_FOUND, null);
	}
}
