# OrderControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**cancelOrder**](#cancelorder) | **POST** /api/orders/{batchOrderId}/cancel | |
|[**createOrder**](#createorder) | **POST** /api/orders | |
|[**getBatchOrderDetail**](#getbatchorderdetail) | **GET** /api/orders/{batchOrderId} | |
|[**getBatchOrderList**](#getbatchorderlist) | **GET** /api/orders | |
|[**payOrder**](#payorder) | **POST** /api/orders/{batchOrderId}/pay | |

# **cancelOrder**
> CommonResultString cancelOrder()


### Example

```typescript
import {
    OrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new OrderControllerApi(configuration);

let batchOrderId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.cancelOrder(
    batchOrderId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **batchOrderId** | [**number**] |  | defaults to undefined|
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

# **createOrder**
> CommonResultBatchOrderVO createOrder(createOrderRequest)


### Example

```typescript
import {
    OrderControllerApi,
    Configuration,
    CreateOrderRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new OrderControllerApi(configuration);

let createOrderRequest: CreateOrderRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.createOrder(
    createOrderRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **createOrderRequest** | **CreateOrderRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultBatchOrderVO**

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

# **getBatchOrderDetail**
> CommonResultBatchOrderVO getBatchOrderDetail()


### Example

```typescript
import {
    OrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new OrderControllerApi(configuration);

let batchOrderId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getBatchOrderDetail(
    batchOrderId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **batchOrderId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultBatchOrderVO**

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

# **getBatchOrderList**
> CommonResultListBatchOrderVO getBatchOrderList()


### Example

```typescript
import {
    OrderControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new OrderControllerApi(configuration);

let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getBatchOrderList(
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultListBatchOrderVO**

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

# **payOrder**
> CommonResultBatchOrderVO payOrder(payOrderRequest)


### Example

```typescript
import {
    OrderControllerApi,
    Configuration,
    PayOrderRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new OrderControllerApi(configuration);

let batchOrderId: number; // (default to undefined)
let payOrderRequest: PayOrderRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.payOrder(
    batchOrderId,
    payOrderRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **payOrderRequest** | **PayOrderRequest**|  | |
| **batchOrderId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultBatchOrderVO**

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

