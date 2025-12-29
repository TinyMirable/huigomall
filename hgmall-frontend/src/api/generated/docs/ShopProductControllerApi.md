# ShopProductControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**addSku**](#addsku) | **POST** /api/merchant/products/{productId}/skus | |
|[**createProduct**](#createproduct) | **POST** /api/merchant/shops/{shopId}/products | |
|[**getProductDetailForEdit**](#getproductdetailforedit) | **GET** /api/merchant/products/{productId} | |
|[**getShopProducts**](#getshopproducts) | **GET** /api/shops/{shopId}/products | |
|[**offShelfProduct**](#offshelfproduct) | **POST** /api/merchant/products/{productId}/off-shelf | |
|[**onShelfProduct**](#onshelfproduct) | **POST** /api/merchant/products/{productId}/on-shelf | |
|[**updateProduct**](#updateproduct) | **PUT** /api/merchant/products/{productId} | |
|[**updateSkuImage**](#updateskuimage) | **PUT** /api/merchant/products/{productId}/skus/{skuId}/image | |
|[**updateSkuPrice**](#updateskuprice) | **PUT** /api/merchant/products/{productId}/skus/{skuId}/price | |
|[**updateSkuStock**](#updateskustock) | **POST** /api/merchant/products/{productId}/skus/{skuId}/stock | |

# **addSku**
> CommonResultListSkuVO addSku(addSkuRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    AddSkuRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let addSkuRequest: AddSkuRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.addSku(
    productId,
    addSkuRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **addSkuRequest** | **AddSkuRequest**|  | |
| **productId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultListSkuVO**

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

# **createProduct**
> CommonResultShopProductVO createProduct(createProductRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    CreateProductRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let shopId: number; // (default to undefined)
let createProductRequest: CreateProductRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.createProduct(
    shopId,
    createProductRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **createProductRequest** | **CreateProductRequest**|  | |
| **shopId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopProductVO**

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

# **getProductDetailForEdit**
> CommonResultShopProductDetailVO getProductDetailForEdit()


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getProductDetailForEdit(
    productId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **productId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopProductDetailVO**

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

# **getShopProducts**
> CommonResultShopProductListVO getShopProducts()


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let shopId: number; // (default to undefined)
let status: number; // (optional) (default to undefined)
let categories: Array<number>; // (optional) (default to undefined)
let page: number; // (optional) (default to undefined)
let size: number; // (optional) (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getShopProducts(
    shopId,
    status,
    categories,
    page,
    size,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **shopId** | [**number**] |  | defaults to undefined|
| **status** | [**number**] |  | (optional) defaults to undefined|
| **categories** | **Array&lt;number&gt;** |  | (optional) defaults to undefined|
| **page** | [**number**] |  | (optional) defaults to undefined|
| **size** | [**number**] |  | (optional) defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopProductListVO**

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

# **offShelfProduct**
> CommonResultVoid offShelfProduct(updateProductStatusRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    UpdateProductStatusRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let updateProductStatusRequest: UpdateProductStatusRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.offShelfProduct(
    productId,
    updateProductStatusRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateProductStatusRequest** | **UpdateProductStatusRequest**|  | |
| **productId** | [**number**] |  | defaults to undefined|
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

# **onShelfProduct**
> CommonResultVoid onShelfProduct(updateProductStatusRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    UpdateProductStatusRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let updateProductStatusRequest: UpdateProductStatusRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.onShelfProduct(
    productId,
    updateProductStatusRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateProductStatusRequest** | **UpdateProductStatusRequest**|  | |
| **productId** | [**number**] |  | defaults to undefined|
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

# **updateProduct**
> CommonResultShopProductVO updateProduct(updateProductRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    UpdateProductRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let updateProductRequest: UpdateProductRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateProduct(
    productId,
    updateProductRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateProductRequest** | **UpdateProductRequest**|  | |
| **productId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultShopProductVO**

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

# **updateSkuImage**
> CommonResultSkuVO updateSkuImage(updateSkuImageRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    UpdateSkuImageRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let skuId: number; // (default to undefined)
let updateSkuImageRequest: UpdateSkuImageRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateSkuImage(
    productId,
    skuId,
    updateSkuImageRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateSkuImageRequest** | **UpdateSkuImageRequest**|  | |
| **productId** | [**number**] |  | defaults to undefined|
| **skuId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultSkuVO**

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

# **updateSkuPrice**
> CommonResultSkuVO updateSkuPrice(updateSkuPriceRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    UpdateSkuPriceRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let skuId: number; // (default to undefined)
let updateSkuPriceRequest: UpdateSkuPriceRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateSkuPrice(
    productId,
    skuId,
    updateSkuPriceRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateSkuPriceRequest** | **UpdateSkuPriceRequest**|  | |
| **productId** | [**number**] |  | defaults to undefined|
| **skuId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultSkuVO**

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

# **updateSkuStock**
> CommonResultSkuVO updateSkuStock(updateSkuStockRequest)


### Example

```typescript
import {
    ShopProductControllerApi,
    Configuration,
    UpdateSkuStockRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ShopProductControllerApi(configuration);

let productId: number; // (default to undefined)
let skuId: number; // (default to undefined)
let updateSkuStockRequest: UpdateSkuStockRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateSkuStock(
    productId,
    skuId,
    updateSkuStockRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateSkuStockRequest** | **UpdateSkuStockRequest**|  | |
| **productId** | [**number**] |  | defaults to undefined|
| **skuId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultSkuVO**

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

