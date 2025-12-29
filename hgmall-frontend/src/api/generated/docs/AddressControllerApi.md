# AddressControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**createAddress**](#createaddress) | **POST** /api/addresses | |
|[**deleteAddress**](#deleteaddress) | **DELETE** /api/addresses/{addressId} | |
|[**getAddress**](#getaddress) | **GET** /api/addresses/{addressId} | |
|[**getAddressList**](#getaddresslist) | **GET** /api/addresses | |
|[**setDefaultAddress**](#setdefaultaddress) | **PUT** /api/addresses/{addressId}/set-default | |
|[**updateAddress**](#updateaddress) | **PUT** /api/addresses/{addressId} | |

# **createAddress**
> CommonResultAddressVO createAddress(createAddressRequest)


### Example

```typescript
import {
    AddressControllerApi,
    Configuration,
    CreateAddressRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AddressControllerApi(configuration);

let createAddressRequest: CreateAddressRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.createAddress(
    createAddressRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **createAddressRequest** | **CreateAddressRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultAddressVO**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteAddress**
> CommonResultString deleteAddress()


### Example

```typescript
import {
    AddressControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AddressControllerApi(configuration);

let addressId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.deleteAddress(
    addressId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **addressId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultString**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAddress**
> CommonResultAddressVO getAddress()


### Example

```typescript
import {
    AddressControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AddressControllerApi(configuration);

let addressId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getAddress(
    addressId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **addressId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultAddressVO**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAddressList**
> CommonResultListAddressVO getAddressList()


### Example

```typescript
import {
    AddressControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AddressControllerApi(configuration);

let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getAddressList(
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultListAddressVO**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **setDefaultAddress**
> CommonResultAddressVO setDefaultAddress()


### Example

```typescript
import {
    AddressControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AddressControllerApi(configuration);

let addressId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.setDefaultAddress(
    addressId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **addressId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultAddressVO**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateAddress**
> CommonResultAddressVO updateAddress(updateAddressRequest)


### Example

```typescript
import {
    AddressControllerApi,
    Configuration,
    UpdateAddressRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AddressControllerApi(configuration);

let addressId: number; // (default to undefined)
let updateAddressRequest: UpdateAddressRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateAddress(
    addressId,
    updateAddressRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateAddressRequest** | **UpdateAddressRequest**|  | |
| **addressId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultAddressVO**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

