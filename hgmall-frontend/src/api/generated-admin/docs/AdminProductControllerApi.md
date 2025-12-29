# AdminProductControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**updateProductStatus**](#updateproductstatus) | **PUT** /api/admin/products/{productId}/status | |

# **updateProductStatus**
> CommonResultVoid updateProductStatus()


### Example

```typescript
import {
    AdminProductControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminProductControllerApi(configuration);

let productId: number; // (default to undefined)
let status: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateProductStatus(
    productId,
    status,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **productId** | [**number**] |  | defaults to undefined|
| **status** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultVoid**

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

