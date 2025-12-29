# AdminAuthControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**login**](#login) | **POST** /api/admin/auth/login | |
|[**loginByCode**](#loginbycode) | **POST** /api/admin/auth/login-by-code | |
|[**sendLoginCode**](#sendlogincode) | **POST** /api/admin/auth/send-login-code | |
|[**sendVerificationCode**](#sendverificationcode) | **POST** /api/admin/auth/send-code | |

# **login**
> CommonResultLoginResult login(loginRequest)


### Example

```typescript
import {
    AdminAuthControllerApi,
    Configuration,
    LoginRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminAuthControllerApi(configuration);

let loginRequest: LoginRequest; //

const { status, data } = await apiInstance.login(
    loginRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **loginRequest** | **LoginRequest**|  | |


### Return type

**CommonResultLoginResult**

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

# **loginByCode**
> CommonResultLoginResult loginByCode(codeLoginRequest)


### Example

```typescript
import {
    AdminAuthControllerApi,
    Configuration,
    CodeLoginRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminAuthControllerApi(configuration);

let codeLoginRequest: CodeLoginRequest; //

const { status, data } = await apiInstance.loginByCode(
    codeLoginRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **codeLoginRequest** | **CodeLoginRequest**|  | |


### Return type

**CommonResultLoginResult**

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

# **sendLoginCode**
> CommonResultString sendLoginCode(verificationCodeRequest)


### Example

```typescript
import {
    AdminAuthControllerApi,
    Configuration,
    VerificationCodeRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminAuthControllerApi(configuration);

let verificationCodeRequest: VerificationCodeRequest; //

const { status, data } = await apiInstance.sendLoginCode(
    verificationCodeRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **verificationCodeRequest** | **VerificationCodeRequest**|  | |


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

# **sendVerificationCode**
> CommonResultString sendVerificationCode(verificationCodeRequest)


### Example

```typescript
import {
    AdminAuthControllerApi,
    Configuration,
    VerificationCodeRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminAuthControllerApi(configuration);

let verificationCodeRequest: VerificationCodeRequest; //

const { status, data } = await apiInstance.sendVerificationCode(
    verificationCodeRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **verificationCodeRequest** | **VerificationCodeRequest**|  | |


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

