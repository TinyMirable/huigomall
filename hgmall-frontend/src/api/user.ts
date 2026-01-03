import { UserControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { UserSummaryVO, UpdateUsernameRequest, UpdateEmailRequest, UpdatePhoneRequest, SendEmailCodeRequest } from './generated/models'
import { getApiBasePath } from './config'

// 创建用户 API 实例
const config = new Configuration({
  basePath: getApiBasePath(),
})

const userApi = new UserControllerApi(config)

// 获取当前用户概要信息
export async function apiGetUserSummary(): Promise<UserSummaryVO> {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  
  // 构建Authorization header
  const authorization = `Bearer ${token}`
  
  const response = await userApi.getUserSummary(authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取用户信息失败')
  }
  
  if (!result.data) {
    throw new Error('用户信息不存在')
  }
  
  return result.data
}

// 修改用户名
export async function apiUpdateUsername(username: string): Promise<{ username: string }> {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  
  const updateUsernameRequest: UpdateUsernameRequest = {
    username: username,
  }
  
  const authorization = `Bearer ${token}`
  const response = await userApi.updateUsername(updateUsernameRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '修改用户名失败')
  }
  
  if (!result.data || !result.data.username) {
    throw new Error('修改用户名响应中缺少数据')
  }
  
  return {
    username: result.data.username
  }
}

// 发送邮箱验证码到新邮箱（用于修改邮箱）
export async function apiSendEmailCodeForUpdate(email: string): Promise<void> {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  
  const sendEmailCodeRequest: SendEmailCodeRequest = {
    email: email,
  }
  
  const authorization = `Bearer ${token}`
  const response = await userApi.sendEmailCodeForUpdate(sendEmailCodeRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '发送验证码失败')
  }
}

// 修改邮箱
export async function apiUpdateEmail(email: string, code?: string): Promise<void> {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  
  const updateEmailRequest: UpdateEmailRequest = {
    email: email,
    code: code,
  }
  
  const authorization = `Bearer ${token}`
  const response = await userApi.updateEmail(updateEmailRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '修改邮箱失败')
  }
}

// 修改手机号
export async function apiUpdatePhone(phoneNumber: string, code?: string): Promise<void> {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  
  const updatePhoneRequest: UpdatePhoneRequest = {
    phoneNumber: phoneNumber,
    code: code,
  }
  
  const authorization = `Bearer ${token}`
  const response = await userApi.updatePhone(updatePhoneRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '修改手机号失败')
  }
}

