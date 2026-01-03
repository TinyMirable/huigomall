import { ProductControllerApi } from './generated/api'
import { Configuration } from './generated/configuration'
import type { HomePageVO, ProductListVO, ProductDetailVO } from './generated/models'
import { getApiBasePath } from './config'

// 创建产品 API 实例
const config = new Configuration({
  basePath: getApiBasePath(),
})

const productApi = new ProductControllerApi(config)

// 获取首页数据
export async function apiGetHomePage(): Promise<HomePageVO> {
  const response = await productApi.getHomePage()
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取首页数据失败')
  }
  
  return result.data || { categoryProducts: [], topSalesProducts: [] }
}

// 获取商品列表
export async function apiGetProductList(
  categories?: number[],
  category?: number,
  page?: number,
  size?: number
): Promise<ProductListVO> {
  const response = await productApi.getProductList(categories, category, page, size)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取商品列表失败')
  }
  
  return result.data || { products: [], total: 0, page: 1, size: 20 }
}

// 获取商品详情
export async function apiGetProductDetail(productId: number): Promise<ProductDetailVO> {
  const response = await productApi.getProductDetail(productId)
  const result = response.data
  
  if (result.code !== 200 && result.code !== 0) {
    throw new Error(result.message || '获取商品详情失败')
  }
  
  if (!result.data) {
    throw new Error('商品不存在或已下架')
  }
  
  return result.data
}

