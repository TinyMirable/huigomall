# MerchantShopControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**closeShop**](#closeshop) | **POST** /api/merchant/shops/{shopId}/close | |
|[**createShop**](#createshop) | **POST** /api/merchant/shops | |
|[**getShops**](#getshops) | **GET** /api/merchant/shops | |
|[**resumeShop**](#resumeshop) | **POST** /api/merchant/shops/{shopId}/resume | |
|[**updateShop**](#updateshop) | **PUT** /api/merchant/shops/{shopId} | |

# **closeShop**
> CommonResultVoid closeShop(closeShopRequest)


### Example

```typescript
import {
    MerchantShopControllerApi,
    Configuration,
    CloseShopRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantShopControllerApi(configuration);

let shopId: number; // (default to undefined)
let closeShopRequest: CloseShopRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.closeShop(
    shopId,
    closeShopRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **closeShopRequest** | **CloseShopRequest**|  | |
| **shopId** | [**number**] |  | defaults to undefined|
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

# **createShop**
> CommonResultShopVO createShop(createShopRequest)


### Example

```typescript
import {
    MerchantShopControllerApi,
    Configuration,
    CreateShopRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantShopControllerApi(configuration);

let createShopRequest: CreateShopRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.createShop(
    createShopRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **createShopRequest** | **CreateShopRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopVO**

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

# **getShops**
> CommonResultListShopVO getShops()


### Example

```typescript
import {
    MerchantShopControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantShopControllerApi(configuration);

let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getShops(
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultListShopVO**

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

# **resumeShop**
> CommonResultShopVO resumeShop(resumeShopRequest)


### Example

```typescript
import {
    MerchantShopControllerApi,
    Configuration,
    ResumeShopRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantShopControllerApi(configuration);

let shopId: number; // (default to undefined)
let resumeShopRequest: ResumeShopRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.resumeShop(
    shopId,
    resumeShopRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **resumeShopRequest** | **ResumeShopRequest**|  | |
| **shopId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopVO**

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

# **updateShop**
> CommonResultShopVO updateShop(updateShopRequest)


### Example

```typescript
import {
    MerchantShopControllerApi,
    Configuration,
    UpdateShopRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantShopControllerApi(configuration);

let shopId: number; // (default to undefined)
let updateShopRequest: UpdateShopRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateShop(
    shopId,
    updateShopRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateShopRequest** | **UpdateShopRequest**|  | |
| **shopId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopVO**

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

