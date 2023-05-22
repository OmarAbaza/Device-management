package com.test.dcs.test.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.dcs.dto.ActivateDeviceDto;
import com.test.dcs.dto.DeviceStatus;
import com.test.dcs.dto.IotDeviceDto;
import com.test.dcs.dto.ResponseDto;
import com.test.dcs.dto.ResponseStatusMessages;
import com.test.dcs.exception.DeviceIsNotValidForActivationException;
import com.test.dcs.rest.IotDeviceController;
import com.test.dcs.service.IotDeviceActivationService;

@WebMvcTest(IotDeviceController.class)
public class IotDeviceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
	private IotDeviceActivationService iotDeviceActivationService;

    private final String DEVICE_SERIAL = "123123";
    @Test
    public void whenActivateValidDevice_ReturnsSuccess() throws Exception {
        when(iotDeviceActivationService.activateIotDevice(anyString())).thenReturn(generateSuccessResponseDtoWithActiveDeviceMock());
        mvc.perform(MockMvcRequestBuilders
                .post("/dcs/activate")
                .content(asJsonString(new ActivateDeviceDto(DEVICE_SERIAL)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseStatusMessages.DEVICE_ACTIVATED_SUCCESSFULLY.getMessage()));
    }

    @Test
    public void whenAttemptingToactivateInvalidDevice_ReturnsBadRequest() throws Exception {
        when(iotDeviceActivationService.activateIotDevice(anyString())).thenThrow(DeviceIsNotValidForActivationException.class);
        mvc.perform(MockMvcRequestBuilders
                .post("/dcs/activate")
                .content(asJsonString(new ActivateDeviceDto(DEVICE_SERIAL)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenTechnicalErrorOccuresDuringActivateDevice_ReturnsBadRequest() throws Exception {
        when(iotDeviceActivationService.activateIotDevice(anyString())).thenThrow(RuntimeException.class);
        mvc.perform(MockMvcRequestBuilders
                .post("/dcs/activate")
                .content(asJsonString(new ActivateDeviceDto(DEVICE_SERIAL)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    private ResponseDto generateSuccessResponseDtoWithActiveDeviceMock() {
		IotDeviceDto iotDeviceDto = new IotDeviceDto();
		iotDeviceDto.setSerialNumber(DEVICE_SERIAL);
		iotDeviceDto.setStatus(DeviceStatus.ACTIVE);
		iotDeviceDto.setTemp(5);
		return new ResponseDto(true, ResponseStatusMessages.DEVICE_ACTIVATED_SUCCESSFULLY, iotDeviceDto);
	}
    
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
