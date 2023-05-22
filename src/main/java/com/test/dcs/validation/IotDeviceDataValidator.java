package com.test.dcs.validation;

import java.util.ArrayList;
import java.util.List;
import com.test.dcs.dto.DeviceStatus;
import com.test.dcs.dto.IotDeviceDto;

public class IotDeviceDataValidator {

	private static IotDeviceDataValidator instance = null;

	private IotDeviceDataValidator() {
	}

	public static synchronized IotDeviceDataValidator getInstance() {
		if (instance == null)
			instance = new IotDeviceDataValidator();
		return instance;
	}

	public List<String> validateDeviceDto(IotDeviceDto subject) {
		List<String> validationErrors = new ArrayList<>();
		if (subject != null) {

			if (subject.getSerialNumber() == null) {
				validationErrors.add("Device serial number is missing");
			} else if (!subject.getSerialNumber().matches("(\\d)*")) {
				validationErrors.add("Device serial number should contain digits only");
			}

			if (subject.getStatus() != null) {
				if (subject.getStatus() == DeviceStatus.ACTIVE) {
					validationErrors.add("Device is already activated");
				}
			} else {
				validationErrors.add("Device status is required");
			}
		} else {
			validationErrors.add("Device data is missing");

		}
		return validationErrors;
	}
}
