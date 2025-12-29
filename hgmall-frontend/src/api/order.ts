import { OrderControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { CreateOrderRequest, PayOrderRequest, BatchOrderVO } from './generated/models'

// 创建订单 API 实例
const config = new Configuration({
  basePath: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
})

const orderApi = new OrderControllerApi(config)

// 获取Authorization header
function getAuthorization(): string {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  return `Bearer ${token}`
}

// 创建订单
export async function apiCreateOrder(request: CreateOrderRequest): Promise<BatchOrderVO> {
  const authorization = getAuthorization()
  
  const response = await orderApi.createOrder(request, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '创建订单失败')
  }
  
  if (!result.data) {
    throw new Error('创建订单失败：返回数据为空')
  }
  
  return result.data
}

// 获取批量订单详情
export async function apiGetBatchOrder(batchOrderId: number): Promise<BatchOrderVO> {
  const authorization = getAuthorization()
  
  const response = await orderApi.getBatchOrderDetail(batchOrderId, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取订单详情失败')
  }
  
  if (!result.data) {
    throw new Error('订单不存在')
  }
  
  return result.data
}

// 支付订单
export async function apiPayOrder(batchOrderId: number, paymentMethod?: string): Promise<BatchOrderVO> {
  const authorization = getAuthorization()
  
  const payOrderRequest: PayOrderRequest = {
    batchOrderId: batchOrderId,
    paymentMethod: paymentMethod || 'ONLINE'
  }
  
  const response = await orderApi.payOrder(batchOrderId, payOrderRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '支付失败')
  }
  
  if (!result.data) {
    throw new Error('支付失败：返回数据为空')
  }
  
  return result.data
}

// 取消订单
export async function apiCancelOrder(batchOrderId: number): Promise<void> {
  const authorization = getAuthorization()
  
  const response = await orderApi.cancelOrder(batchOrderId, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '取消订单失败')
  }
}

// 获取订单列表
export async function apiGetBatchOrderList(): Promise<BatchOrderVO[]> {
  const authorization = getAuthorization()
  
  const response = await orderApi.getBatchOrderList(authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取订单列表失败')
  }
  
  return result.data || []
}

