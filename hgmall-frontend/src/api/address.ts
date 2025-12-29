import { Configuration } from './generated/configuration'
import axios from 'axios'
import type { AddressVO } from './generated/models/address-vo'

// 类型定义
export interface CreateAddressRequest {
  detail: string
  receiverName: string
  receiverPhone: string
  isDefault?: boolean
}

export interface UpdateAddressRequest {
  detail: string
  receiverName: string
  receiverPhone: string
  isDefault?: boolean
}

// 创建API配置
const config = new Configuration({
  basePath: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
})

// 获取Authorization header
function getAuthorization(): string {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  return `Bearer ${token}`
}

// 获取地址列表
export async function apiGetAddressList(): Promise<AddressVO[]> {
  const authorization = getAuthorization()
  const response = await axios.get(`${config.basePath}/api/addresses`, {
    headers: {
      'Authorization': authorization
    }
  })
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取地址列表失败')
  }
  
  return result.data || []
}

// 获取单个地址详情
export async function apiGetAddress(addressId: number): Promise<AddressVO> {
  const authorization = getAuthorization()
  const response = await axios.get(`${config.basePath}/api/addresses/${addressId}`, {
    headers: {
      'Authorization': authorization
    }
  })
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取地址详情失败')
  }
  
  if (!result.data) {
    throw new Error('地址不存在')
  }
  
  return result.data
}

// 创建地址
export async function apiCreateAddress(request: CreateAddressRequest): Promise<AddressVO> {
  const authorization = getAuthorization()
  const response = await axios.post(`${config.basePath}/api/addresses`, request, {
    headers: {
      'Authorization': authorization,
      'Content-Type': 'application/json'
    }
  })
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '创建地址失败')
  }
  
  if (!result.data) {
    throw new Error('创建地址响应中缺少数据')
  }
  
  return result.data
}

// 更新地址
export async function apiUpdateAddress(addressId: number, request: UpdateAddressRequest): Promise<AddressVO> {
  const authorization = getAuthorization()
  const response = await axios.put(`${config.basePath}/api/addresses/${addressId}`, request, {
    headers: {
      'Authorization': authorization,
      'Content-Type': 'application/json'
    }
  })
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '更新地址失败')
  }
  
  if (!result.data) {
    throw new Error('更新地址响应中缺少数据')
  }
  
  return result.data
}

// 删除地址
export async function apiDeleteAddress(addressId: number): Promise<void> {
  const authorization = getAuthorization()
  const response = await axios.delete(`${config.basePath}/api/addresses/${addressId}`, {
    headers: {
      'Authorization': authorization
    }
  })
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '删除地址失败')
  }
}

// 设置默认地址
export async function apiSetDefaultAddress(addressId: number): Promise<AddressVO> {
  const authorization = getAuthorization()
  const response = await axios.put(`${config.basePath}/api/addresses/${addressId}/set-default`, {}, {
    headers: {
      'Authorization': authorization,
      'Content-Type': 'application/json'
    }
  })
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '设置默认地址失败')
  }
  
  if (!result.data) {
    throw new Error('设置默认地址响应中缺少数据')
  }
  
  return result.data
}

