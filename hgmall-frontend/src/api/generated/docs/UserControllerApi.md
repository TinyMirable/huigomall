# UserControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getUserSummary**](#getusersummary) | **GET** /api/users/me/summary | |
|[**sendEmailCodeForUpdate**](#sendemailcodeforupdate) | **POST** /api/users/me/email/send-code | |
|[**updateEmail**](#updateemail) | **PUT** /api/users/me/email | |
|[**updatePhone**](#updatephone) | **PUT** /api/users/me/phone | |
|[**updateUsername**](#updateusername) | **POST** /api/users/me/username | |

# **getUserSummary**
> CommonResultUserSummaryVO getUserSummary()


### Example

```typescript
import {
    UserControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new UserControllerApi(configuration);

let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getUserSummary(
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultUserSummaryVO**

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

# **sendEmailCodeForUpdate**
> CommonResultString sendEmailCodeForUpdate(sendEmailCodeRequest)


### Example

```typescript
import {
    UserControllerApi,
    Configuration,
    SendEmailCodeRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new UserControllerApi(configuration);

let sendEmailCodeRequest: SendEmailCodeRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.sendEmailCodeForUpdate(
    sendEmailCodeRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **sendEmailCodeRequest** | **SendEmailCodeRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultString**

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

# **updateEmail**
> CommonResultString updateEmail(updateEmailRequest)


### Example

```typescript
import {
    UserControllerApi,
    Configuration,
    UpdateEmailRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new UserControllerApi(configuration);

let updateEmailRequest: UpdateEmailRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateEmail(
    updateEmailRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateEmailRequest** | **UpdateEmailRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultString**

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

# **updatePhone**
> CommonResultString updatePhone(updatePhoneRequest)


### Example

```typescript
import {
    UserControllerApi,
    Configuration,
    UpdatePhoneRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new UserControllerApi(configuration);

let updatePhoneRequest: UpdatePhoneRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updatePhone(
    updatePhoneRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updatePhoneRequest** | **UpdatePhoneRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultString**

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

# **updateUsername**
> CommonResultUpdateUsernameResponse updateUsername(updateUsernameRequest)


### Example

```typescript
import {
    UserControllerApi,
    Configuration,
    UpdateUsernameRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new UserControllerApi(configuration);

let updateUsernameRequest: UpdateUsernameRequest; //
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.updateUsername(
    updateUsernameRequest,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateUsernameRequest** | **UpdateUsernameRequest**|  | |
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultUpdateUsernameResponse**

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

