# BatchOrderVO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**batchOrderId** | **number** |  | [optional] [default to undefined]
**userId** | **number** |  | [optional] [default to undefined]
**totalAmount** | **number** |  | [optional] [default to undefined]
**status** | **number** |  | [optional] [default to undefined]
**addressId** | **number** |  | [optional] [default to undefined]
**address** | [**AddressVO**](AddressVO.md) |  | [optional] [default to undefined]
**createTime** | **string** |  | [optional] [default to undefined]
**payTime** | **string** |  | [optional] [default to undefined]
**expireTime** | **string** |  | [optional] [default to undefined]
**updateTime** | **string** |  | [optional] [default to undefined]
**orders** | [**Array&lt;OrderVO&gt;**](OrderVO.md) |  | [optional] [default to undefined]
**remainingSeconds** | **number** |  | [optional] [default to undefined]

## Example

```typescript
import { BatchOrderVO } from './api';

const instance: BatchOrderVO = {
    batchOrderId,
    userId,
    totalAmount,
    status,
    addressId,
    address,
    createTime,
    payTime,
    expireTime,
    updateTime,
    orders,
    remainingSeconds,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
