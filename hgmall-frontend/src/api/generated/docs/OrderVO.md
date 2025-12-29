# OrderVO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**orderId** | **number** |  | [optional] [default to undefined]
**batchOrderId** | **number** |  | [optional] [default to undefined]
**userId** | **number** |  | [optional] [default to undefined]
**shopId** | **number** |  | [optional] [default to undefined]
**shopName** | **string** |  | [optional] [default to undefined]
**status** | **number** |  | [optional] [default to undefined]
**total** | **number** |  | [optional] [default to undefined]
**addressId** | **number** |  | [optional] [default to undefined]
**address** | [**AddressVO**](AddressVO.md) |  | [optional] [default to undefined]
**createTime** | **string** |  | [optional] [default to undefined]
**updateTime** | **string** |  | [optional] [default to undefined]
**orderItems** | [**Array&lt;OrderItemVO&gt;**](OrderItemVO.md) |  | [optional] [default to undefined]

## Example

```typescript
import { OrderVO } from './api';

const instance: OrderVO = {
    orderId,
    batchOrderId,
    userId,
    shopId,
    shopName,
    status,
    total,
    addressId,
    address,
    createTime,
    updateTime,
    orderItems,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
