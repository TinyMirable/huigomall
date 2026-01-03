import { MerchantOrderControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { MerchantOrderListVO, MerchantOrderVO, OrderStatisticsVO } from './generated/models'
import { getApiBasePath } from './config'

// 创建商家订单 API 实例
const config = new Configuration({
  basePath: getApiBasePath(),
})

const merchantOrderApi = new MerchantOrderControllerApi(config)

// 获取Authorization header
function getAuthorization(): string {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  return `Bearer ${token}`
}

// 获取商家订单列表
export async function apiGetMerchantOrders(params: {
  shopId?: number
  status?: number
  startTime?: string
  endTime?: string
  page?: number
  size?: number
}): Promise<MerchantOrderListVO> {
  const authorization = getAuthorization()
  
  const response = await merchantOrderApi.getOrders(
    params.shopId,
    params.status,
    params.startTime,
    params.endTime,
    params.page,
    params.size,
    authorization
  )
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取订单列表失败')
  }
  
  if (!result.data) {
    return { orders: [], total: 0, page: 1, size: 20 }
  }
  
  return result.data
}

// 获取订单详情
export async function apiGetMerchantOrderDetail(orderId: number): Promise<MerchantOrderVO> {
  const authorization = getAuthorization()
  
  const response = await merchantOrderApi.getOrderDetail(orderId, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取订单详情失败')
  }
  
  if (!result.data) {
    throw new Error('订单不存在')
  }
  
  return result.data
}

// 订单发货
export async function apiShipOrder(orderId: number, remark?: string): Promise<MerchantOrderVO> {
  const authorization = getAuthorization()
  
  const shipOrderRequest = remark ? { remark } : undefined
  
  const response = await merchantOrderApi.shipOrder(orderId, authorization, shipOrderRequest)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '发货失败')
  }
  
  if (!result.data) {
    throw new Error('发货失败：返回数据为空')
  }
  
  return result.data
}

// 获取订单统计报表
// 注意：shopId在生成的API中是必填参数，如果不传则传0表示查询所有店铺
export async function apiGetOrderStatistics(params: {
  shopId?: number
  startTime?: string
  endTime?: string
}): Promise<OrderStatisticsVO> {
  const authorization = getAuthorization()
  
  // shopId是必填参数，如果不传则传0表示查询所有店铺
  const shopId = params.shopId ?? 0
  
  const response = await merchantOrderApi.getStatistics(
    shopId,
    params.startTime,
    params.endTime,
    authorization
  )
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取统计报表失败')
  }
  
  if (!result.data) {
    throw new Error('获取统计报表失败：返回数据为空')
  }
  
  return result.data
}

