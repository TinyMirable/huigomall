# ProductControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getHomePage**](#gethomepage) | **GET** /api/home | |
|[**getProductDetail**](#getproductdetail) | **GET** /api/products/{productId} | |
|[**getProductList**](#getproductlist) | **GET** /api/products | |

# **getHomePage**
> CommonResultHomePageVO getHomePage()


### Example

```typescript
import {
    ProductControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ProductControllerApi(configuration);

const { status, data } = await apiInstance.getHomePage();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**CommonResultHomePageVO**

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

# **getProductDetail**
> CommonResultProductDetailVO getProductDetail()


### Example

```typescript
import {
    ProductControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ProductControllerApi(configuration);

let productId: number; // (default to undefined)

const { status, data } = await apiInstance.getProductDetail(
    productId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **productId** | [**number**] |  | defaults to undefined|


### Return type

**CommonResultProductDetailVO**

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

# **getProductList**
> CommonResultProductListVO getProductList()


### Example

```typescript
import {
    ProductControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ProductControllerApi(configuration);

let categories: Array<number>; // (optional) (default to undefined)
let category: number; // (optional) (default to undefined)
let page: number; // (optional) (default to undefined)
let size: number; // (optional) (default to undefined)

const { status, data } = await apiInstance.getProductList(
    categories,
    category,
    page,
    size
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **categories** | **Array&lt;number&gt;** |  | (optional) defaults to undefined|
| **category** | [**number**] |  | (optional) defaults to undefined|
| **page** | [**number**] |  | (optional) defaults to undefined|
| **size** | [**number**] |  | (optional) defaults to undefined|


### Return type

**CommonResultProductListVO**

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

