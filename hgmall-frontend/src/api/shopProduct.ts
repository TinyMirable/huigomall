import { ShopProductControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { 
  ShopProductListVO, 
  ShopProductVO, 
  ShopProductDetailVO,
  CreateProductRequest,
  UpdateProductRequest,
  UpdateSkuImageRequest,
  UpdateSkuStockRequest,
  UpdateSkuPriceRequest,
  AddSkuRequest,
  SkuVO
} from './generated/models'

// 创建店铺商品 API 实例
const config = new Configuration({
  basePath: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
})

const shopProductApi = new ShopProductControllerApi(config)

// 获取Authorization header
function getAuthorization(): string {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  return `Bearer ${token}`
}

// 获取店铺商品列表
export async function apiGetShopProducts(
  shopId: number,
  status?: number,
  categories?: number[],
  page?: number,
  size?: number,
  requireAuth?: boolean
): Promise<ShopProductListVO> {
  const authorization = requireAuth ? getAuthorization() : undefined
  
  const response = await shopProductApi.getShopProducts(
    shopId,
    status,
    categories,
    page,
    size,
    authorization
  )
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取店铺商品列表失败')
  }
  
  return result.data || { products: [], total: 0, page: 1, size: 20 }
}

// 创建商品
export async function apiCreateProduct(
  shopId: number,
  payload: {
    name: string
    categoryId: number
    description?: string
    imageUrl?: string
    code: string
    skus: Array<{
      spec: string
      price: number
      stock: number
      imageUrl?: string
    }>
  }
): Promise<ShopProductVO> {
  const authorization = getAuthorization()
  
  const createProductRequest: CreateProductRequest = {
    shopId: shopId,
    name: payload.name,
    categoryId: payload.categoryId,
    description: payload.description,
    imageUrl: payload.imageUrl,
    code: payload.code,
    skus: payload.skus
  }
  
  const response = await shopProductApi.createProduct(shopId, createProductRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '创建商品失败')
  }
  
  if (!result.data) {
    throw new Error('创建商品失败：返回数据为空')
  }
  
  return result.data
}

// 更新商品
export async function apiUpdateProduct(
  productId: number,
  payload: {
    name: string
    categoryId: number
    description?: string
    imageUrl?: string
  }
): Promise<ShopProductVO> {
  const authorization = getAuthorization()
  
  const updateProductRequest: UpdateProductRequest = {
    name: payload.name,
    categoryId: payload.categoryId,
    description: payload.description,
    imageUrl: payload.imageUrl,
    code: '' // 后端已不再需要验证码，传空字符串
  } as UpdateProductRequest
  
  const response = await shopProductApi.updateProduct(productId, updateProductRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新商品失败')
  }
  
  if (!result.data) {
    throw new Error('更新商品失败：返回数据为空')
  }
  
  return result.data
}

// 获取商品详情（用于编辑）
export async function apiGetProductDetailForEdit(productId: number): Promise<ShopProductDetailVO> {
  const authorization = getAuthorization()
  
  const response = await shopProductApi.getProductDetailForEdit(productId, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取商品详情失败')
  }
  
  if (!result.data) {
    throw new Error('商品不存在')
  }
  
  return result.data
}

// 上架商品
export async function apiOnShelfProduct(productId: number, code: string): Promise<void> {
  const authorization = getAuthorization()
  
  const updateProductStatusRequest: { status: number; code: string } = {
    status: 1,
    code: code
  }
  
  const response = await shopProductApi.onShelfProduct(productId, updateProductStatusRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '商品上架失败')
  }
}

// 下架商品
export async function apiOffShelfProduct(productId: number, code: string): Promise<void> {
  const authorization = getAuthorization()
  
  const updateProductStatusRequest: { status: number; code: string } = {
    status: 0,
    code: code
  }
  
  const response = await shopProductApi.offShelfProduct(productId, updateProductStatusRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '商品下架失败')
  }
}

// 更新SKU图片
export async function apiUpdateSkuImage(
  productId: number,
  skuId: number,
  payload: {
    imageUrl: string
    code: string
  }
): Promise<SkuVO> {
  const authorization = getAuthorization()
  
  const updateSkuImageRequest: UpdateSkuImageRequest = {
    imageUrl: payload.imageUrl,
    code: payload.code
  }
  
  const response = await shopProductApi.updateSkuImage(
    productId,
    skuId,
    updateSkuImageRequest,
    authorization
  )
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新SKU图片失败')
  }
  
  if (!result.data) {
    throw new Error('更新SKU图片失败：返回数据为空')
  }
  
  return result.data
}

// 更新SKU库存
export async function apiUpdateSkuStock(
  productId: number,
  skuId: number,
  payload: {
    delta?: number
    absoluteStock?: number
    code: string
  }
): Promise<SkuVO> {
  const authorization = getAuthorization()
  
  const updateSkuStockRequest: UpdateSkuStockRequest = {
    delta: payload.delta,
    absoluteStock: payload.absoluteStock,
    code: payload.code
  }
  
  const response = await shopProductApi.updateSkuStock(
    productId,
    skuId,
    updateSkuStockRequest,
    authorization
  )
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新SKU库存失败')
  }
  
  if (!result.data) {
    throw new Error('更新SKU库存失败：返回数据为空')
  }
  
  return result.data
}

// 更新SKU价格
export async function apiUpdateSkuPrice(
  productId: number,
  skuId: number,
  payload: {
    price: number
    code: string
  }
): Promise<SkuVO> {
  const authorization = getAuthorization()
  
  const updateSkuPriceRequest: UpdateSkuPriceRequest = {
    price: payload.price,
    code: payload.code
  }
  
  const response = await shopProductApi.updateSkuPrice(
    productId,
    skuId,
    updateSkuPriceRequest,
    authorization
  )
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新SKU价格失败')
  }
  
  if (!result.data) {
    throw new Error('更新SKU价格失败：返回数据为空')
  }
  
  return result.data
}

// 添加SKU
export async function apiAddSku(
  productId: number,
  payload: {
    skus: Array<{
      spec: string
      price: number
      stock: number
      imageUrl?: string
    }>
    code: string
  }
): Promise<SkuVO[]> {
  const authorization = getAuthorization()
  
  const addSkuRequest: AddSkuRequest = {
    skus: payload.skus,
    code: payload.code
  }
  
  const response = await shopProductApi.addSku(productId, addSkuRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '添加SKU失败')
  }
  
  return result.data || []
}
