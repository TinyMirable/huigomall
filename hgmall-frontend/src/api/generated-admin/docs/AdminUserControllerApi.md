# AdminUserControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getUserDetail**](#getuserdetail) | **GET** /api/admin/users/{userId} | |
|[**getUserList**](#getuserlist) | **GET** /api/admin/users | |
|[**getUserOrders**](#getuserorders) | **GET** /api/admin/users/{userId}/orders | |
|[**updateUserStatus**](#updateuserstatus) | **POST** /api/admin/users/{userId}/status | |

# **getUserDetail**
> CommonResultUserVO getUserDetail()


### Example

```typescript
import {
    AdminUserControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminUserControllerApi(configuration);

let userId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getUserDetail(
    userId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **userId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultUserVO**

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

# **getUserList**
> CommonResultUserListVO getUserList()


### Example

```typescript
import {
    AdminUserControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminUserControllerApi(configuration);

let roleId: number; // (optional) (default to undefined)
let status: number; // (optional) (default to undefined)
let keyword: string; // (optional) (default to undefined)
let page: number; // (optional) (default to 1)
let size: number; // (optional) (default to 20)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getUserList(
    roleId,
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
| **roleId** | [**number**] |  | (optional) defaults to undefined|
| **status** | [**number**] |  | (optional) defaults to undefined|
| **keyword** | [**string**] |  | (optional) defaults to undefined|
| **page** | [**number**] |  | (optional) defaults to 1|
| **size** | [**number**] |  | (optional) defaults to 20|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultUserListVO**

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

# **getUserOrders**
> CommonResultMerchantOrderListVO getUserOrders()


### Example

```typescript
import {
    AdminUserControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminUserControllerApi(configuration);

let userId: number; // (default to undefined)
let status: number; // (optional) (default to undefined)
let page: number; // (optional) (default to 1)
let size: number; // (optional) (default to 20)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getUserOrders(
    userId,
    status,
    page,
    size,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **userId** | [**number**] |  | defaults to undefined|
| **status** | [**number**] |  | (optional) defaults to undefined|
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

# **updateUserStatus**
> CommonResultVoid updateUserStatus(updateUserStatusRequest)


### Example

```typescript
import {
    AdminUserControllerApi,
    Configuration,
    UpdateUserStatusRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminUserControllerApi(configuration);

let userId: number; // (default to undefined)
let updateUserStatusRequest: UpdateUserStatusRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateUserStatus(
    userId,
    updateUserStatusRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateUserStatusRequest** | **UpdateUserStatusRequest**|  | |
| **userId** | [**number**] |  | defaults to undefined|
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

