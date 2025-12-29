# AdminAuditLogControllerApi

All URIs are relative to *http://localhost:8081*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getAuditLogDetail**](#getauditlogdetail) | **GET** /api/admin/audit-logs/{auditId} | |
|[**getAuditLogList**](#getauditloglist) | **GET** /api/admin/audit-logs | |

# **getAuditLogDetail**
> CommonResultAuditLogVO getAuditLogDetail()


### Example

```typescript
import {
    AdminAuditLogControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminAuditLogControllerApi(configuration);

let auditId: number; // (default to undefined)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getAuditLogDetail(
    auditId,
    authorization
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **auditId** | [**number**] |  | defaults to undefined|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultAuditLogVO**

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

# **getAuditLogList**
> CommonResultAuditLogListVO getAuditLogList()


### Example

```typescript
import {
    AdminAuditLogControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AdminAuditLogControllerApi(configuration);

let targetType: string; // (optional) (default to undefined)
let operationType: string; // (optional) (default to undefined)
let adminUserId: number; // (optional) (default to undefined)
let targetId: number; // (optional) (default to undefined)
let startTime: string; // (optional) (default to undefined)
let endTime: string; // (optional) (default to undefined)
let page: number; // (optional) (default to 1)
let size: number; // (optional) (default to 20)
let authorization: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getAuditLogList(
    targetType,
    operationType,
    adminUserId,
    targetId,
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
| **targetType** | [**string**] |  | (optional) defaults to undefined|
| **operationType** | [**string**] |  | (optional) defaults to undefined|
| **adminUserId** | [**number**] |  | (optional) defaults to undefined|
| **targetId** | [**number**] |  | (optional) defaults to undefined|
| **startTime** | [**string**] |  | (optional) defaults to undefined|
| **endTime** | [**string**] |  | (optional) defaults to undefined|
| **page** | [**number**] |  | (optional) defaults to 1|
| **size** | [**number**] |  | (optional) defaults to 20|
| **authorization** | [**string**] |  | (optional) defaults to undefined|


### Return type

**CommonResultAuditLogListVO**

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

