# AdminConfigControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getSystemConfig**](#getsystemconfig) | **GET** /api/admin/config | |
|[**updateMerchantAudit**](#updatemerchantaudit) | **PUT** /api/admin/config/merchant-audit | |
|[**updateShopAudit**](#updateshopaudit) | **PUT** /api/admin/config/shop-audit | |

# **getSystemConfig**
> CommonResultSystemConfigVO getSystemConfig()


### Example

```typescript
import {
    AdminConfigControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminConfigControllerApi(configuration);

let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getSystemConfig(
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultSystemConfigVO**

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

# **updateMerchantAudit**
> CommonResultVoid updateMerchantAudit(updateConfigRequest)


### Example

```typescript
import {
    AdminConfigControllerApi,
    Configuration,
    UpdateConfigRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminConfigControllerApi(configuration);

let updateConfigRequest: UpdateConfigRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateMerchantAudit(
    updateConfigRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateConfigRequest** | **UpdateConfigRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultVoid**

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

# **updateShopAudit**
> CommonResultVoid updateShopAudit(updateConfigRequest)


### Example

```typescript
import {
    AdminConfigControllerApi,
    Configuration,
    UpdateConfigRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminConfigControllerApi(configuration);

let updateConfigRequest: UpdateConfigRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateShopAudit(
    updateConfigRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateConfigRequest** | **UpdateConfigRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultVoid**

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

