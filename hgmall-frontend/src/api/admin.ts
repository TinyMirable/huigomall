import {
  AdminMerchantControllerApi,
  AdminUserControllerApi,
  AdminCategoryControllerApi,
  AdminOrderControllerApi,
  AdminAuditLogControllerApi,
  AdminConfigControllerApi,
  AdminShopControllerApi
} from './generated-admin/api'
import { Configuration } from './generated-admin/configuration'
import type {
  MerchantVO,
  UserListVO,
  UserVO,
  CategoryVO,
  MerchantOrderListVO,
  MerchantOrderVO,
  AuditLogListVO,
  AuditLogVO,
  SystemConfigVO,
  AuditMerchantRequest,
  UpdateMerchantStatusRequest,
  UpdateUserStatusRequest,
  CreateCategoryRequest,
  UpdateCategoryRequest,
  CancelOrderRequest,
  UpdateConfigRequest,
  ShopListVO
} from './generated-admin/models'

import { getAdminApiBasePath } from './config'
import axios from 'axios'

// 创建管理员 API 实例
const config = new Configuration({
  basePath: getAdminApiBasePath(),
})

const merchantApi = new AdminMerchantControllerApi(config)
const userApi = new AdminUserControllerApi(config)
const categoryApi = new AdminCategoryControllerApi(config)
const orderApi = new AdminOrderControllerApi(config)
const auditLogApi = new AdminAuditLogControllerApi(config)
const configApi = new AdminConfigControllerApi(config)
const shopApi = new AdminShopControllerApi(config)

// 获取Authorization header
function getAuthorization(): string {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  return `Bearer ${token}`
}

// ========== 商家管理 ==========
export async function apiGetMerchants(params: {
  status?: number
  keyword?: string
  page?: number
  size?: number
}): Promise<{ merchants: MerchantVO[]; total: number; page: number; size: number }> {
  const authorization = getAuthorization()
  // TODO: 后端API中缺少getMerchantList方法，需要添加或使用其他方法
  // 临时解决方案：使用axios直接调用
  const response = await axios.get(`${getAdminApiBasePath()}/api/admin/merchants`, {
    params: {
      status: params.status,
      keyword: params.keyword,
      page: params.page,
      size: params.size
    },
    headers: {
      Authorization: authorization
    }
  })
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取商家列表失败')
  }
  return result.data || { merchants: [], total: 0, page: 1, size: 20 }
}

export async function apiGetMerchantDetail(merchantId: number): Promise<MerchantVO> {
  const authorization = getAuthorization()
  const response = await merchantApi.getMerchantDetail(merchantId, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取商家详情失败')
  }
  if (!result.data) {
    throw new Error('商家不存在')
  }
  return result.data
}

export async function apiAuditMerchant(merchantId: number, action: 'approve' | 'reject', reason?: string): Promise<void> {
  const authorization = getAuthorization()
  const request: AuditMerchantRequest = { action, reason }
  const response = await merchantApi.auditMerchant(merchantId, request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '审核商家失败')
  }
}

export async function apiUpdateMerchantStatus(merchantId: number, status: number, reason?: string): Promise<void> {
  const authorization = getAuthorization()
  const request: UpdateMerchantStatusRequest = { status, reason }
  const response = await merchantApi.updateMerchantStatus(merchantId, request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新商家状态失败')
  }
}

// ========== 用户管理 ==========
export async function apiGetUsers(params: {
  roleId?: number
  status?: number
  keyword?: string
  page?: number
  size?: number
}): Promise<UserListVO> {
  const authorization = getAuthorization()
  const response = await userApi.getUserList(
    params.roleId,
    params.status,
    params.keyword,
    params.page,
    params.size,
    authorization
  )
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取用户列表失败')
  }
  return result.data || { users: [], total: 0, page: 1, size: 20 }
}

export async function apiGetUserDetail(userId: number): Promise<UserVO> {
  const authorization = getAuthorization()
  const response = await userApi.getUserDetail(userId, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取用户详情失败')
  }
  if (!result.data) {
    throw new Error('用户不存在')
  }
  return result.data
}

export async function apiUpdateUserStatus(userId: number, status: number, reason?: string): Promise<void> {
  const authorization = getAuthorization()
  const request: UpdateUserStatusRequest = { status, reason }
  const response = await userApi.updateUserStatus(userId, request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新用户状态失败')
  }
}

export async function apiGetUserOrders(userId: number, params: {
  page?: number
  size?: number
  status?: number
}): Promise<MerchantOrderListVO> {
  const authorization = getAuthorization()
  const response = await userApi.getUserOrders(
    userId,
    params.status,
    params.page,
    params.size,
    authorization
  )
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取用户订单失败')
  }
  return result.data || { orders: [], total: 0, page: 1, size: 20 }
}

// ========== 分类管理 ==========
export async function apiGetCategoryTree(): Promise<CategoryVO[]> {
  const authorization = getAuthorization()
  const response = await categoryApi.getCategoryTree(authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取分类树失败')
  }
  return result.data || []
}

export async function apiCreateCategory(request: CreateCategoryRequest): Promise<CategoryVO> {
  const authorization = getAuthorization()
  const response = await categoryApi.createCategory(request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '创建分类失败')
  }
  if (!result.data) {
    throw new Error('创建分类失败：返回数据为空')
  }
  return result.data
}

export async function apiUpdateCategory(categoryId: number, request: UpdateCategoryRequest): Promise<CategoryVO> {
  const authorization = getAuthorization()
  const response = await categoryApi.updateCategory(categoryId, request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新分类失败')
  }
  if (!result.data) {
    throw new Error('更新分类失败：返回数据为空')
  }
  return result.data
}

export async function apiDeleteCategory(categoryId: number): Promise<void> {
  const authorization = getAuthorization()
  const response = await categoryApi.deleteCategory(categoryId, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '删除分类失败')
  }
}

// ========== 订单管理 ==========
export async function apiGetAdminOrders(params: {
  shopId?: number
  merchantId?: number
  status?: number
  startTime?: string
  endTime?: string
  keyword?: string
  page?: number
  size?: number
}): Promise<MerchantOrderListVO> {
  const authorization = getAuthorization()
  const response = await orderApi.getOrderList(
    params.shopId,
    params.merchantId,
    params.status,
    params.startTime,
    params.endTime,
    params.keyword,
    params.page,
    params.size,
    authorization
  )
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取订单列表失败')
  }
  return result.data || { orders: [], total: 0, page: 1, size: 20 }
}

export async function apiGetAdminOrderDetail(orderId: number): Promise<MerchantOrderVO> {
  const authorization = getAuthorization()
  const response = await orderApi.getOrderDetail(orderId, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取订单详情失败')
  }
  if (!result.data) {
    throw new Error('订单不存在')
  }
  return result.data
}

export async function apiCancelAdminOrder(orderId: number, reason: string): Promise<void> {
  const authorization = getAuthorization()
  const request: CancelOrderRequest = { reason }
  const response = await orderApi.cancelOrder(orderId, request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '取消订单失败')
  }
}

export async function apiGetAdminOrderStatistics(params: {
  startTime?: string
  endTime?: string
  shopId?: number
  merchantId?: number
}): Promise<any> {
  const authorization = getAuthorization()
  const response = await orderApi.getOrderStatistics(
    params.shopId,
    params.merchantId,
    params.startTime,
    params.endTime,
    authorization
  )
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取订单统计失败')
  }
  return result.data
}

// ========== 审计日志 ==========
export async function apiGetAuditLogs(params: {
  targetType?: string
  operationType?: string
  adminUserId?: number
  targetId?: number
  startTime?: string
  endTime?: string
  page?: number
  size?: number
}): Promise<AuditLogListVO> {
  const authorization = getAuthorization()
  const response = await auditLogApi.getAuditLogList(
    params.targetType,
    params.operationType,
    params.adminUserId,
    params.targetId,
    params.startTime,
    params.endTime,
    params.page,
    params.size,
    authorization
  )
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取审计日志失败')
  }
  if (!result.data) {
    return { auditLogs: [], total: 0, page: 1, size: 20 } as AuditLogListVO
  }
  return result.data
}

export async function apiGetAuditLogDetail(auditId: number): Promise<AuditLogVO> {
  const authorization = getAuthorization()
  const response = await auditLogApi.getAuditLogDetail(auditId, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取审计日志详情失败')
  }
  if (!result.data) {
    throw new Error('审计日志不存在')
  }
  return result.data
}

// ========== 系统配置 ==========
export async function apiGetSystemConfig(): Promise<SystemConfigVO> {
  const authorization = getAuthorization()
  const response = await configApi.getSystemConfig(authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取系统配置失败')
  }
  if (!result.data) {
    throw new Error('获取系统配置失败：返回数据为空')
  }
  return result.data
}

export async function apiUpdateMerchantAuditConfig(enabled: boolean): Promise<void> {
  const authorization = getAuthorization()
  const request: UpdateConfigRequest = { enabled }
  const response = await configApi.updateMerchantAudit(request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新商家审核配置失败')
  }
}

export async function apiUpdateShopAuditConfig(enabled: boolean): Promise<void> {
  const authorization = getAuthorization()
  const request: UpdateConfigRequest = { enabled }
  const response = await configApi.updateShopAudit(request, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新店铺审核配置失败')
  }
}

// ========== 店铺管理 ==========
export async function apiGetAdminShops(params: {
  status?: number
  keyword?: string
  page?: number
  size?: number
}): Promise<ShopListVO> {
  const authorization = getAuthorization()
  const response = await shopApi.getShopList(
    params.status,
    params.keyword,
    params.page,
    params.size,
    authorization
  )
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取店铺列表失败')
  }
  return result.data || { shops: [], total: 0, page: 1, size: 20 }
}

export async function apiUpdateShopStatus(shopId: number, status: number): Promise<void> {
  const authorization = getAuthorization()
  const response = await shopApi.updateShopStatus(shopId, status, authorization)
  const result = response.data
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新店铺状态失败')
  }
}

