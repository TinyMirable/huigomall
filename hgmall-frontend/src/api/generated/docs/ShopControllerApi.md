# ShopControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getShop**](#getshop) | **GET** /api/shops/{shopId} | |

# **getShop**
> CommonResultShopVO getShop()


### Example

```typescript
import {
    ShopControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopControllerApi(configuration);

let shopId: number; // (default to undefined)

const { status, data } = await apiInstance.getShop(
    shopId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shopId** | [**number**] |  | defaults to undefined|


### Return type

**CommonResultShopVO**

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

