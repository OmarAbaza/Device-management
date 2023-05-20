package com.test.dcs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import com.test.dcs.dto.ActivateDeviceDto;
import com.test.dcs.dto.ResponseDto;
import com.test.dcs.dto.ResponseStatusMessages;
import com.test.dcs.exception.DeviceAlreadyActiveException;
import com.test.dcs.exception.DeviceDataMissingException;
import com.test.dcs.service.IotDeviceActivationService;

@RestController
@RequestMapping(path = "dcs")
public class IotDeviceController {

	@Autowired
	private IotDeviceActivationService deviceActivationService;

	@PostMapping("/activate")
	public ResponseEntity<ResponseDto> activateDevice(@RequestBody ActivateDeviceDto activateDeviceDto)
			throws DeviceDataMissingException, DeviceAlreadyActiveException {
		ResponseDto responseDto = deviceActivationService.activateIotDevice(activateDeviceDto.getSerialNumber());
		return new ResponseEntity<ResponseDto>(
				new ResponseDto(true, ResponseStatusMessages.DEVICE_ACTIVATED_SUCCESSFULLY, responseDto), HttpStatus.OK);
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFound.class)
	public ResponseEntity<ResponseDto> handleDeviceNotFoundExceptions(NotFound ex) {
		return new ResponseEntity<ResponseDto>(
				new ResponseDto(false, ResponseStatusMessages.DEVICE_NOT_FOUND, ex.getResponseBodyAsString()),
				HttpStatus.NOT_FOUND);
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DeviceDataMissingException.class)
	public ResponseEntity<ResponseDto> handleDataValidationExceptions(DeviceDataMissingException ex) {
		return new ResponseEntity<ResponseDto>(
				new ResponseDto(false, ResponseStatusMessages.DATA_VALIDATION_FAILED, ex.getErrors()),
				HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseDto> handleInvalidFormatExceptions(HttpMessageNotReadableException ex) {
		return new ResponseEntity<ResponseDto>(new ResponseDto(false, ResponseStatusMessages.INVALID_DATA, ex.getRootCause()),
				HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDto> handleTechnicalExceptions(Exception ex) {
		ex.printStackTrace();
		return new ResponseEntity<ResponseDto>(new ResponseDto(false, ResponseStatusMessages.TECHNICAL_FAILURE, null),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
