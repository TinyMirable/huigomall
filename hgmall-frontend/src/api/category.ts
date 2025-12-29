import { CategoryControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { Category, CategoryVO } from './generated/models'

// 创建分类 API 实例
const config = new Configuration({
  basePath: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
})

const categoryApi = new CategoryControllerApi(config)

// 扩展 CategoryVO 类型，支持 children 字段（分类树结构）
export interface CategoryTreeVO extends CategoryVO {
  children?: CategoryTreeVO[]
}

// 获取所有一级分类列表
export async function apiGetCategories(): Promise<Category[]> {
  const response = await categoryApi.getCategories()
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取分类列表失败')
  }
  
  return result.data || []
}

// 获取分类树（支持多级分类，最大深度为3）
export async function apiGetCategoryTree(): Promise<CategoryTreeVO[]> {
  const response = await categoryApi.getCategoryTree()
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取分类树失败')
  }
  
  return (result.data || []) as CategoryTreeVO[]
}

