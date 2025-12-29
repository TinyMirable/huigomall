# AdminOrderControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**cancelOrder**](#cancelorder) | **POST** /api/admin/orders/{orderId}/cancel | |
|[**getOrderDetail**](#getorderdetail) | **GET** /api/admin/orders/{orderId} | |
|[**getOrderList**](#getorderlist) | **GET** /api/admin/orders | |
|[**getOrderStatistics**](#getorderstatistics) | **GET** /api/admin/orders/statistics | |

# **cancelOrder**
> CommonResultVoid cancelOrder(cancelOrderRequest)


### Example

```typescript
import {
    AdminOrderControllerApi,
    Configuration,
    CancelOrderRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminOrderControllerApi(configuration);

let orderId: number; // (default to undefined)
let cancelOrderRequest: CancelOrderRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.cancelOrder(
    orderId,
    cancelOrderRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **cancelOrderRequest** | **CancelOrderRequest**|  | |
| **orderId** | [**number**] |  | defaults to undefined|
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

# **getOrderDetail**
> CommonResultMerchantOrderVO getOrderDetail()


### Example

```typescript
import {
    AdminOrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminOrderControllerApi(configuration);

let orderId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getOrderDetail(
    orderId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **orderId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultMerchantOrderVO**

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

# **getOrderList**
> CommonResultMerchantOrderListVO getOrderList()


### Example

```typescript
import {
    AdminOrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminOrderControllerApi(configuration);

let shopId: number; // (optional) (default to undefined)
let merchantId: number; // (optional) (default to undefined)
let status: number; // (optional) (default to undefined)
let startTime: string; // (optional) (default to undefined)
let endTime: string; // (optional) (default to undefined)
let keyword: string; // (optional) (default to undefined)
let page: number; // (optional) (default to 1)
let size: number; // (optional) (default to 20)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getOrderList(
    shopId,
    merchantId,
    status,
    startTime,
    endTime,
    keyword,
    page,
    size,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shopId** | [**number**] |  | (optional) defaults to undefined|
| **merchantId** | [**number**] |  | (optional) defaults to undefined|
| **status** | [**number**] |  | (optional) defaults to undefined|
| **startTime** | [**string**] |  | (optional) defaults to undefined|
| **endTime** | [**string**] |  | (optional) defaults to undefined|
| **keyword** | [**string**] |  | (optional) defaults to undefined|
| **page** | [**number**] |  | (optional) defaults to 1|
| **size** | [**number**] |  | (optional) defaults to 20|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultMerchantOrderListVO**

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

# **getOrderStatistics**
> CommonResultOrderStatisticsVO getOrderStatistics()


### Example

```typescript
import {
    AdminOrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminOrderControllerApi(configuration);

let shopId: number; // (optional) (default to undefined)
let merchantId: number; // (optional) (default to undefined)
let startTime: string; // (optional) (default to undefined)
let endTime: string; // (optional) (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getOrderStatistics(
    shopId,
    merchantId,
    startTime,
    endTime,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shopId** | [**number**] |  | (optional) defaults to undefined|
| **merchantId** | [**number**] |  | (optional) defaults to undefined|
| **startTime** | [**string**] |  | (optional) defaults to undefined|
| **endTime** | [**string**] |  | (optional) defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultOrderStatisticsVO**

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

