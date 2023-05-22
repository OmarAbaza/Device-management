# Device configuration service
	This is a springboot serivce to activate the IoT devices to be available for sale.
	The serice is using the getStaus and patch endpoints from the warehouse service to validate the device data before configuring the device.

# Build and deploy the service
## Prerequisites
- Java8 and mvn commands installed in the system
- Docker engine (*optional*)
## Build and deploy
The service is developed as a Maven project. To build and start the service run the following command in the application main directory 
`mvn clean install spring-boot:run` 
There is a dockerFile supplied in the repository to run the service in a container `docker build -t .`	

# REST Interfaces
Base URLs:

* <a href="http://localhost:8080">http://localhost:8080</a>

<h1 id="openapi-definition-iot-device-controller">iot-device-controller</h1>

## activateDevice

<a id="opIdactivateDevice"></a>

`POST /dcs/activate`

> Body parameter

```json
{
  "serialNumber": "string"
}
```

<h3 id="activatedevice-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[ActivateDeviceDto](#schemaactivatedevicedto)|true|none|

> Example responses

> 200 Response

<h3 id="activatedevice-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResponseDto](#schemaresponsedto)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ResponseDto](#schemaresponsedto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|[ResponseDto](#schemaresponsedto)|
|406|[Not Acceptable](https://tools.ietf.org/html/rfc7231#section-6.5.6)|Not Acceptable|[ResponseDto](#schemaresponsedto)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ResponseDto](#schemaresponsedto)|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_ResponseDto">ResponseDto</h2>
<!-- backwards compatibility -->
<a id="schemaresponsedto"></a>
<a id="schema_ResponseDto"></a>
<a id="tocSresponsedto"></a>
<a id="tocsresponsedto"></a>

```json
{
  "success": true,
  "message": "Device data is retrieved successfully",
  "data": {}
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|success|boolean|false|none|none|
|message|string|false|none|none|
|data|object|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|message|Device data is retrieved successfully|
|message|Device data is patched successfully|
|message|Device is activated successfully|
|message|Device not found|
|message|Data validation failed|
|message|Request contains invalid data|
|message|Technical failure occurred, Please contact support team|
|message|User doesn't have the required permission level to perform this action|

<h2 id="tocS_ActivateDeviceDto">ActivateDeviceDto</h2>
<!-- backwards compatibility -->
<a id="schemaactivatedevicedto"></a>
<a id="schema_ActivateDeviceDto"></a>
<a id="tocSactivatedevicedto"></a>
<a id="tocsactivatedevicedto"></a>

```json
{
  "serialNumber": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|serialNumber|string|false|none|none|

