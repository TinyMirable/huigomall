import { MerchantShopControllerApi, ShopControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { CreateShopRequest, CloseShopRequest, UpdateShopRequest, ResumeShopRequest, ShopVO } from './generated/models'

// 创建店铺 API 实例
const config = new Configuration({
  basePath: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
})

const merchantShopApi = new MerchantShopControllerApi(config)
const shopApi = new ShopControllerApi(config)

// 获取Authorization header
function getAuthorization(): string {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  return `Bearer ${token}`
}

// 获取商家店铺列表
export async function apiGetShops(): Promise<ShopVO[]> {
  const authorization = getAuthorization()
  
  const response = await merchantShopApi.getShops(authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取店铺列表失败')
  }
  
  return result.data || []
}

// 创建店铺
export async function apiCreateShop(payload: { name: string; description?: string; code: string }): Promise<ShopVO> {
  const authorization = getAuthorization()
  
  const createShopRequest: CreateShopRequest = {
    name: payload.name,
    description: payload.description,
    code: payload.code
  }
  
  const response = await merchantShopApi.createShop(createShopRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '创建店铺失败')
  }
  
  if (!result.data) {
    throw new Error('创建店铺失败：返回数据为空')
  }
  
  return result.data
}

// 关闭店铺
export async function apiCloseShop(shopId: number, code: string): Promise<void> {
  const authorization = getAuthorization()
  
  const closeShopRequest: CloseShopRequest = {
    code: code
  }
  
  const response = await merchantShopApi.closeShop(shopId, closeShopRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '关闭店铺失败')
  }
}

// 更新店铺信息
export async function apiUpdateShop(
  shopId: number,
  payload: { name: string; description?: string; code: string }
): Promise<ShopVO> {
  const authorization = getAuthorization()
  
  const updateShopRequest: UpdateShopRequest = {
    name: payload.name,
    description: payload.description,
    code: payload.code
  }
  
  const response = await merchantShopApi.updateShop(shopId, updateShopRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新店铺失败')
  }
  
  if (!result.data) {
    throw new Error('更新店铺失败：返回数据为空')
  }
  
  return result.data
}

// 重新开启店铺（从CLOSED恢复到ACTIVE）
export async function apiResumeShop(shopId: number, code: string): Promise<ShopVO> {
  const authorization = getAuthorization()
  
  const resumeShopRequest: ResumeShopRequest = {
    code: code
  }
  
  const response = await merchantShopApi.resumeShop(shopId, resumeShopRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '重新开启店铺失败')
  }
  
  if (!result.data) {
    throw new Error('重新开启店铺失败：返回数据为空')
  }
  
  return result.data
}

// 获取店铺信息（仅ACTIVE状态，所有人可查看）
export async function apiGetShop(shopId: number): Promise<ShopVO> {
  const response = await shopApi.getShop(shopId)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取店铺信息失败')
  }
  
  if (!result.data) {
    throw new Error('店铺不存在或已关闭')
  }
  
  return result.data
}

