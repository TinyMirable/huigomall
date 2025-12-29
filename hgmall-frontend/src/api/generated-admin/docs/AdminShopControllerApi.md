# AdminShopControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getShopList**](#getshoplist) | **GET** /api/admin/shops | |
|[**updateShopStatus**](#updateshopstatus) | **PUT** /api/admin/shops/{shopId}/status | |

# **getShopList**
> CommonResultShopListVO getShopList()


### Example

```typescript
import {
    AdminShopControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminShopControllerApi(configuration);

let status: number; // (optional) (default to undefined)
let keyword: string; // (optional) (default to undefined)
let page: number; // (optional) (default to 1)
let size: number; // (optional) (default to 20)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getShopList(
    status,
    keyword,
    page,
    size,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **status** | [**number**] |  | (optional) defaults to undefined|
| **keyword** | [**string**] |  | (optional) defaults to undefined|
| **page** | [**number**] |  | (optional) defaults to 1|
| **size** | [**number**] |  | (optional) defaults to 20|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopListVO**

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

# **updateShopStatus**
> CommonResultVoid updateShopStatus()


### Example

```typescript
import {
    AdminShopControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminShopControllerApi(configuration);

let shopId: number; // (default to undefined)
let status: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateShopStatus(
    shopId,
    status,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shopId** | [**number**] |  | defaults to undefined|
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

