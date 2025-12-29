# AdminMerchantControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**auditMerchant**](#auditmerchant) | **POST** /api/admin/merchants/{merchantId}/audit | |
|[**getMerchantDetail**](#getmerchantdetail) | **GET** /api/admin/merchants/{merchantId} | |
|[**updateMerchantStatus**](#updatemerchantstatus) | **POST** /api/admin/merchants/{merchantId}/status | |

# **auditMerchant**
> CommonResultVoid auditMerchant(auditMerchantRequest)


### Example

```typescript
import {
    AdminMerchantControllerApi,
    Configuration,
    AuditMerchantRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminMerchantControllerApi(configuration);

let merchantId: number; // (default to undefined)
let auditMerchantRequest: AuditMerchantRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.auditMerchant(
    merchantId,
    auditMerchantRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **auditMerchantRequest** | **AuditMerchantRequest**|  | |
| **merchantId** | [**number**] |  | defaults to undefined|
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

# **getMerchantDetail**
> CommonResultMerchantVO getMerchantDetail()


### Example

```typescript
import {
    AdminMerchantControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminMerchantControllerApi(configuration);

let merchantId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getMerchantDetail(
    merchantId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **merchantId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultMerchantVO**

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

# **updateMerchantStatus**
> CommonResultVoid updateMerchantStatus(updateMerchantStatusRequest)


### Example

```typescript
import {
    AdminMerchantControllerApi,
    Configuration,
    UpdateMerchantStatusRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminMerchantControllerApi(configuration);

let merchantId: number; // (default to undefined)
let updateMerchantStatusRequest: UpdateMerchantStatusRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateMerchantStatus(
    merchantId,
    updateMerchantStatusRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateMerchantStatusRequest** | **UpdateMerchantStatusRequest**|  | |
| **merchantId** | [**number**] |  | defaults to undefined|
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

