import { CartControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { AddCartItemRequest, UpdateCartItemRequest, CartItemVO } from './generated/models'
import { getApiBasePath } from './config'

// 创建购物车 API 实例
const config = new Configuration({
  basePath: getApiBasePath(),
})

const cartApi = new CartControllerApi(config)

// 获取Authorization header
function getAuthorization(): string {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  return `Bearer ${token}`
}

// 添加商品到购物车
export async function apiAddCartItem(skuId: number, num: number): Promise<void> {
  const authorization = getAuthorization()
  
  const addCartItemRequest: AddCartItemRequest = {
    skuId: skuId,
    num: num,
  }
  
  const response = await cartApi.addCartItem(addCartItemRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '添加到购物车失败')
  }
}

// 获取购物车列表
export async function apiGetCartItems(): Promise<CartItemVO[]> {
  const authorization = getAuthorization()
  
  const response = await cartApi.getCartItemList(authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取购物车列表失败')
  }
  
  return result.data || []
}

// 更新购物车商品数量
export async function apiUpdateCartItem(itemId: number, num: number): Promise<CartItemVO> {
  const authorization = getAuthorization()
  
  const updateCartItemRequest: UpdateCartItemRequest = {
    num: num,
  }
  
  const response = await cartApi.updateCartItem(itemId, updateCartItemRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新购物车商品失败')
  }
  
  if (!result.data) {
    throw new Error('更新购物车商品失败：返回数据为空')
  }
  
  return result.data
}

// 删除购物车商品
export async function apiDeleteCartItem(itemId: number): Promise<void> {
  const authorization = getAuthorization()
  
  const response = await cartApi.deleteCartItem(itemId, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '删除购物车商品失败')
  }
}

