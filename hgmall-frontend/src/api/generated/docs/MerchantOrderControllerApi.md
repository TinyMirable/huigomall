# MerchantOrderControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getOrderDetail**](#getorderdetail) | **GET** /api/merchant/orders/{orderId} | |
|[**getOrders**](#getorders) | **GET** /api/merchant/orders | |
|[**getStatistics**](#getstatistics) | **GET** /api/merchant/orders/statistics | |
|[**shipOrder**](#shiporder) | **POST** /api/merchant/orders/{orderId}/ship | |

# **getOrderDetail**
> CommonResultMerchantOrderVO getOrderDetail()


### Example

```typescript
import {
    MerchantOrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantOrderControllerApi(configuration);

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

# **getOrders**
> CommonResultMerchantOrderListVO getOrders()


### Example

```typescript
import {
    MerchantOrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantOrderControllerApi(configuration);

let shopId: number; // (optional) (default to undefined)
let status: number; // (optional) (default to undefined)
let startTime: string; // (optional) (default to undefined)
let endTime: string; // (optional) (default to undefined)
let page: number; // (optional) (default to 1)
let size: number; // (optional) (default to 20)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getOrders(
    shopId,
    status,
    startTime,
    endTime,
    page,
    size,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shopId** | [**number**] |  | (optional) defaults to undefined|
| **status** | [**number**] |  | (optional) defaults to undefined|
| **startTime** | [**string**] |  | (optional) defaults to undefined|
| **endTime** | [**string**] |  | (optional) defaults to undefined|
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

# **getStatistics**
> CommonResultOrderStatisticsVO getStatistics()


### Example

```typescript
import {
    MerchantOrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantOrderControllerApi(configuration);

let shopId: number; // (default to undefined)
let startTime: string; // (optional) (default to undefined)
let endTime: string; // (optional) (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getStatistics(
    shopId,
    startTime,
    endTime,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shopId** | [**number**] |  | defaults to undefined|
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

# **shipOrder**
> CommonResultMerchantOrderVO shipOrder()


### Example

```typescript
import {
    MerchantOrderControllerApi,
    Configuration,
    ShipOrderRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new MerchantOrderControllerApi(configuration);

let orderId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)
let shipOrderRequest: ShipOrderRequest; // (optional)

const { status, data } = await apiInstance.shipOrder(
    orderId,
    authorization,
    shipOrderRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shipOrderRequest** | **ShipOrderRequest**|  | |
| **orderId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultMerchantOrderVO**

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

