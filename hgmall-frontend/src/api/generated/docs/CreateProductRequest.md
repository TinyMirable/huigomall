# CreateProductRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**shopId** | **number** |  | [default to undefined]
**name** | **string** |  | [default to undefined]
**description** | **string** |  | [optional] [default to undefined]
**imageUrl** | **string** |  | [optional] [default to undefined]
**code** | **string** |  | [default to undefined]
**skus** | [**Array&lt;CreateSkuRequest&gt;**](CreateSkuRequest.md) |  | [default to undefined]
**categoryId** | **number** |  | [default to undefined]

## Example

```typescript
import { CreateProductRequest } from './api';

const instance: CreateProductRequest = {
    shopId,
    name,
    description,
    imageUrl,
    code,
    skus,
    categoryId,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
