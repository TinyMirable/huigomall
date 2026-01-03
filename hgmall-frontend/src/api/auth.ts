import { AuthControllerApi } from './generated/api'
import { AdminAuthControllerApi } from './generated-admin/api'
import { Configuration } from './generated/configuration'
import { Configuration as AdminConfiguration } from './generated-admin/configuration'
import type { LoginRequest, CodeLoginRequest, RegisterRequest, VerificationCodeRequest, ForgotPasswordRequest, ChangePasswordRequest, MerchantRegisterRequest } from './generated/models'
import { getApiBasePath, getAdminApiBasePath } from './config'

// 创建普通用户 API 实例
const config = new Configuration({
  basePath: getApiBasePath(),
})

const authApi = new AuthControllerApi(config)

// 创建管理员 API 实例
const adminConfig = new AdminConfiguration({
  basePath: getAdminApiBasePath(),
})

const adminAuthApi = new AdminAuthControllerApi(adminConfig)

// 普通用户登录（用户名/邮箱/手机号 + 密码）
export async function apiLogin(payload: { identifier: string; password: string }, isAdmin = false): Promise<{ access_token: string; user: any }> {
  const loginRequest: LoginRequest = {
    identifier: payload.identifier,
    password: payload.password,
  }
  
  const api = isAdmin ? adminAuthApi : authApi
  const response = await api.login(loginRequest)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '登录失败')
  }
  
  if (!result.data?.token) {
    throw new Error('登录响应中缺少 token')
  }
  
  return {
    access_token: result.data.token,
    user: result.data,
  }
}

// 验证码登录（邮箱/手机号 + 验证码）
export async function apiLoginByCode(payload: { contact: string; code: string }, isAdmin = false): Promise<{ access_token: string; user: any }> {
  const codeLoginRequest: CodeLoginRequest = {
    contact: payload.contact,
    code: payload.code,
  }
  
  const api = isAdmin ? adminAuthApi : authApi
  const response = await api.loginByCode(codeLoginRequest)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '登录失败')
  }
  
  if (!result.data?.token) {
    throw new Error('登录响应中缺少 token')
  }
  
  return {
    access_token: result.data.token,
    user: result.data,
  }
}

// 注册
export async function apiRegister(payload: { username: string; password: string; email: string; code: string }): Promise<{ access_token: string; user: any }> {
  const registerRequest: RegisterRequest = {
    username: payload.username,
    password: payload.password,
    email: payload.email,
    code: payload.code,
  }
  
  const response = await authApi.register(registerRequest)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '注册失败')
  }
  
  if (!result.data?.token) {
    throw new Error('注册响应中缺少 token')
  }
  
  return {
    access_token: result.data.token,
    user: result.data,
  }
}

// 发送通用验证码（需要用户存在，用于忘记密码、修改信息等用户操作）
export async function apiSendVerificationCode(payload: { contact: string }, isAdmin = false): Promise<void> {
  const verificationCodeRequest: VerificationCodeRequest = {
    contact: payload.contact,
  }
  
  try {
    const api = isAdmin ? adminAuthApi : authApi
    const response = await api.sendVerificationCode(verificationCodeRequest)
    const result = response.data
    
    if (result.code !== 200 && result.code !== 0) {
      throw new Error(result.message || '发送验证码失败')
    }
  } catch (error: any) {
    // 处理HTTP错误（如403）
    if (error.response) {
      const status = error.response.status
      const message = error.response.data?.message || error.response.data?.data || `请求失败 (${status})`
      throw new Error(message)
    } else if (error.request) {
      throw new Error('网络错误，请检查后端服务是否运行')
    } else {
      throw error
    }
  }
}

// 发送注册验证码（需要用户不存在，仅用于注册）
export async function apiSendRegisterCode(payload: { contact: string }): Promise<void> {
  const verificationCodeRequest: VerificationCodeRequest = {
    contact: payload.contact,
  }
  
  try {
    const response = await authApi.sendRegisterCode(verificationCodeRequest)
    const result = response.data
    
    if (result.code !== 200 && result.code !== 0) {
      throw new Error(result.message || '发送验证码失败')
    }
  } catch (error: any) {
    // 处理HTTP错误
    if (error.response) {
      const status = error.response.status
      const message = error.response.data?.message || error.response.data?.data || `请求失败 (${status})`
      throw new Error(message)
    } else if (error.request) {
      throw new Error('网络错误，请检查后端服务是否运行')
    } else {
      throw error
    }
  }
}

// 发送登录验证码（已废弃，使用 apiSendVerificationCode 替代）
// 保留此函数以保持向后兼容，但实际调用通用验证码API
export async function apiSendLoginCode(payload: { contact: string }, isAdmin = false): Promise<void> {
  // 登录验证码实际上也是需要用户存在的，所以使用通用验证码API
  return apiSendVerificationCode(payload, isAdmin)
}

// 忘记密码（重置密码）
export async function apiForgotPassword(payload: { email: string; code: string; newPassword: string }): Promise<void> {
  const forgotPasswordRequest: ForgotPasswordRequest = {
    email: payload.email,
    code: payload.code,
    newPassword: payload.newPassword,
  }
  
  const response = await authApi.forgotPassword(forgotPasswordRequest)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '重置密码失败')
  }
}

// 修改密码（需要验证码）
export async function apiChangePassword(payload: { email: string; code: string; newPassword: string }): Promise<void> {
  const changePasswordRequest: ChangePasswordRequest = {
    email: payload.email,
    code: payload.code,
    newPassword: payload.newPassword,
  }
  
  const response = await authApi.changePassword(changePasswordRequest)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '修改密码失败')
  }
}

// 注册为商家
export async function apiRegisterMerchant(payload: { merchantName: string; owner?: string }): Promise<void> {
  const token = localStorage.getItem('access_token')
  if (!token) {
    throw new Error('未登录')
  }
  
  const merchantRegisterRequest: MerchantRegisterRequest = {
    merchantName: payload.merchantName,
    owner: payload.owner,
  }
  
  const authorization = `Bearer ${token}`
  const response = await authApi.registerMerchant(merchantRegisterRequest, authorization)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '注册商家失败')
  }
}

// 获取当前用户信息（需要 token）
export async function apiMe(_token: string): Promise<{ username: string; email: string; userId: number }> {
  // 这里需要根据实际的 API 实现
  // 暂时返回一个模拟的用户信息
  // 实际应该调用获取用户信息的 API
  // const configWithToken = new Configuration({
  //   basePath: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  //   accessToken: token,
  // })
  
  // TODO: 实现实际的获取用户信息 API
  // 这里先返回一个模拟数据
  return {
    username: localStorage.getItem('user_nickname') || '用户',
    email: '',
    userId: 0,
  }
}

