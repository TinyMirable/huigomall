/**
 * API 配置
 * 统一管理所有 API 的基础路径
 */

// 获取 API 基础路径
// 注意：生成的 API 代码中路径已经包含 /api，所以 basePath 不应该再包含 /api
// 生产环境：使用空字符串（路径已包含 /api，Nginx 会处理）
// 开发环境：使用完整域名（路径已包含 /api）
export function getApiBasePath(): string {
  // 如果设置了环境变量，使用环境变量
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL
  }
  
  // 生产环境（通过 Nginx）：使用空字符串，因为生成的 API 路径已经包含 /api
  // 开发环境：使用完整域名，因为生成的 API 路径已经包含 /api
  if (import.meta.env.PROD || import.meta.env.MODE === 'production') {
    return ''
  }
  
  // 开发环境默认值（不包含 /api，因为生成的路径已经包含）
  return 'http://localhost:8080'
}

// 获取 Admin API 基础路径
export function getAdminApiBasePath(): string {
  if (import.meta.env.VITE_ADMIN_API_BASE_URL) {
    return import.meta.env.VITE_ADMIN_API_BASE_URL
  }
  
  if (import.meta.env.PROD || import.meta.env.MODE === 'production') {
    return '/admin-api'
  }
  
  return 'http://localhost:8081'
}

