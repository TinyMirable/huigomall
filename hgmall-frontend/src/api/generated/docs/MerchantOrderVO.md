# MerchantOrderVO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**orderId** | **number** |  | [optional] [default to undefined]
**batchOrderId** | **number** |  | [optional] [default to undefined]
**userId** | **number** |  | [optional] [default to undefined]
**userName** | **string** |  | [optional] [default to undefined]
**userEmail** | **string** |  | [optional] [default to undefined]
**shopId** | **number** |  | [optional] [default to undefined]
**shopName** | **string** |  | [optional] [default to undefined]
**status** | **number** |  | [optional] [default to undefined]
**statusText** | **string** |  | [optional] [default to undefined]
**total** | **number** |  | [optional] [default to undefined]
**addressId** | **number** |  | [optional] [default to undefined]
**address** | [**AddressVO**](AddressVO.md) |  | [optional] [default to undefined]
**orderItems** | [**Array&lt;MerchantOrderItemVO&gt;**](MerchantOrderItemVO.md) |  | [optional] [default to undefined]
**createTime** | **string** |  | [optional] [default to undefined]
**updateTime** | **string** |  | [optional] [default to undefined]

## Example

```typescript
import { MerchantOrderVO } from './api';

const instance: MerchantOrderVO = {
    orderId,
    batchOrderId,
    userId,
    userName,
    userEmail,
    shopId,
    shopName,
    status,
    statusText,
    total,
    addressId,
    address,
    orderItems,
    createTime,
    updateTime,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
