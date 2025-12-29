# AdminCategoryControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**createCategory**](#createcategory) | **POST** /api/admin/categories | |
|[**deleteCategory**](#deletecategory) | **DELETE** /api/admin/categories/{categoryId} | |
|[**getCategoryTree**](#getcategorytree) | **GET** /api/admin/categories/tree | |
|[**updateCategory**](#updatecategory) | **PUT** /api/admin/categories/{categoryId} | |

# **createCategory**
> CommonResultCategoryVO createCategory(createCategoryRequest)


### Example

```typescript
import {
    AdminCategoryControllerApi,
    Configuration,
    CreateCategoryRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminCategoryControllerApi(configuration);

let createCategoryRequest: CreateCategoryRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.createCategory(
    createCategoryRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **createCategoryRequest** | **CreateCategoryRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultCategoryVO**

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

# **deleteCategory**
> CommonResultVoid deleteCategory()


### Example

```typescript
import {
    AdminCategoryControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminCategoryControllerApi(configuration);

let categoryId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.deleteCategory(
    categoryId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **categoryId** | [**number**] |  | defaults to undefined|
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

# **getCategoryTree**
> CommonResultListCategoryVO getCategoryTree()


### Example

```typescript
import {
    AdminCategoryControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminCategoryControllerApi(configuration);

let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getCategoryTree(
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultListCategoryVO**

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

# **updateCategory**
> CommonResultCategoryVO updateCategory(updateCategoryRequest)


### Example

```typescript
import {
    AdminCategoryControllerApi,
    Configuration,
    UpdateCategoryRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminCategoryControllerApi(configuration);

let categoryId: number; // (default to undefined)
let updateCategoryRequest: UpdateCategoryRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateCategory(
    categoryId,
    updateCategoryRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateCategoryRequest** | **UpdateCategoryRequest**|  | |
| **categoryId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultCategoryVO**

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

