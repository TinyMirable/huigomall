# CartControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**addCartItem**](#addcartitem) | **POST** /api/cart | |
|[**deleteCartItem**](#deletecartitem) | **DELETE** /api/cart/{itemId} | |
|[**getCartItem**](#getcartitem) | **GET** /api/cart/{itemId} | |
|[**getCartItemList**](#getcartitemlist) | **GET** /api/cart | |
|[**updateCartItem**](#updatecartitem) | **PUT** /api/cart/{itemId} | |

# **addCartItem**
> CommonResultCartItemVO addCartItem(addCartItemRequest)


### Example

```typescript
import {
    CartControllerApi,
    Configuration,
    AddCartItemRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new CartControllerApi(configuration);

let addCartItemRequest: AddCartItemRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.addCartItem(
    addCartItemRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **addCartItemRequest** | **AddCartItemRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultCartItemVO**

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

# **deleteCartItem**
> CommonResultString deleteCartItem()


### Example

```typescript
import {
    CartControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new CartControllerApi(configuration);

let itemId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.deleteCartItem(
    itemId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **itemId** | [**number**] |  | defaults to undefined|
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

# **getCartItem**
> CommonResultCartItemVO getCartItem()


### Example

```typescript
import {
    CartControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new CartControllerApi(configuration);

let itemId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getCartItem(
    itemId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **itemId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultCartItemVO**

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

# **getCartItemList**
> CommonResultListCartItemVO getCartItemList()


### Example

```typescript
import {
    CartControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new CartControllerApi(configuration);

let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getCartItemList(
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultListCartItemVO**

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

# **updateCartItem**
> CommonResultCartItemVO updateCartItem(updateCartItemRequest)


### Example

```typescript
import {
    CartControllerApi,
    Configuration,
    UpdateCartItemRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new CartControllerApi(configuration);

let itemId: number; // (default to undefined)
let updateCartItemRequest: UpdateCartItemRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateCartItem(
    itemId,
    updateCartItemRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateCartItemRequest** | **UpdateCartItemRequest**|  | |
| **itemId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultCartItemVO**

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

