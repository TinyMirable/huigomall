<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-7xl">
        <h1 class="text-3xl font-bold mb-6">商品列表</h1>

        <div class="flex gap-6">
          <!-- 左侧筛选器（固定） -->
          <div class="w-64 flex-shrink-0">
            <div class="card bg-base-100 shadow-sm sticky top-24">
              <div class="card-body p-4">
                <h2 class="card-title text-lg mb-4">分类筛选</h2>
                <div class="space-y-2">
                  <label class="cursor-pointer label gap-3 p-2 rounded-lg hover:bg-base-200 transition-colors">
                    <input
                      type="checkbox"
                      class="checkbox checkbox-primary"
                      :checked="selectedCategories.length === 0"
                      @change="toggleSelectAll"
                    />
                    <span class="label-text font-semibold">全部分类</span>
                  </label>
                  <div class="divider my-2"></div>
                  <!-- 多级分类树 -->
                  <div class="space-y-1">
                    <div
                      v-for="category in categoryTree"
                      :key="category.categoryId"
                      class="space-y-1"
                    >
                      <!-- 一级分类 -->
                      <label class="cursor-pointer label gap-2 p-2 rounded-lg hover:bg-base-200 transition-colors">
                        <input
                          type="checkbox"
                          class="checkbox checkbox-primary"
                          :checked="isCategorySelected(category.categoryId!)"
                          @change="toggleCategory(category.categoryId!)"
                        />
                        <span class="label-text font-medium">{{ category.name }}</span>
                      </label>
                      
                      <!-- 二级分类 -->
                      <div
                        v-if="category.children && category.children.length > 0"
                        class="ml-6 space-y-1"
                      >
                        <div
                          v-for="child in category.children"
                          :key="child.categoryId"
                          class="space-y-1"
                        >
                          <label class="cursor-pointer label gap-2 p-1.5 rounded-lg hover:bg-base-200 transition-colors">
                            <input
                              type="checkbox"
                              class="checkbox checkbox-primary checkbox-sm"
                              :checked="isCategorySelected(child.categoryId!)"
                              @change="toggleCategory(child.categoryId!)"
                            />
                            <span class="label-text text-sm">{{ child.name }}</span>
                          </label>
                          
                          <!-- 三级分类 -->
                          <div
                            v-if="child.children && child.children.length > 0"
                            class="ml-6 space-y-1"
                          >
                            <label
                              v-for="grandChild in child.children"
                              :key="grandChild.categoryId"
                              class="cursor-pointer label gap-2 p-1 rounded-lg hover:bg-base-200 transition-colors"
                            >
                              <input
                                type="checkbox"
                                class="checkbox checkbox-primary checkbox-xs"
                                :checked="isCategorySelected(grandChild.categoryId!)"
                                @change="toggleCategory(grandChild.categoryId!)"
                              />
                              <span class="label-text text-xs">{{ grandChild.name }}</span>
                            </label>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧商品列表 -->
          <div class="flex-1 min-w-0">

            <!-- 加载状态 -->
            <div v-if="loading" class="text-center py-12">
              <span class="loading loading-spinner loading-lg"></span>
              <p class="mt-4 text-base-content/60">加载中...</p>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="alert alert-error">
              <span>{{ error }}</span>
              <button class="btn btn-sm" @click="loadProducts">重试</button>
            </div>

            <!-- 商品列表 -->
            <div v-else-if="productList.products && productList.products.length > 0">
              <!-- 商品网格 -->
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                <ProductCard
                  v-for="product in productList.products"
                  :key="product.productId"
                  :name="product.name || ''"
                  :price="product.minPrice || 0"
                  :items-sold="product.sales"
                  :image-url="product.coverImageUrl"
                  :product-id="product.productId"
                  :merchant-name="product.merchantName"
                  :shop-id="product.shopId"
                />
              </div>

              <!-- 分页 -->
              <div class="flex justify-center">
                <div class="join">
                  <button
                    class="join-item btn"
                    :class="{ 'btn-disabled': currentPage <= 1 }"
                    @click="goToPage(currentPage - 1)"
                  >
                    «
                  </button>
                  <template v-for="page in visiblePages" :key="page">
                    <button
                      v-if="page !== -1"
                      class="join-item btn"
                      :class="{ 'btn-active': page === currentPage }"
                      @click="goToPage(page)"
                    >
                      {{ page }}
                    </button>
                    <span v-else class="join-item btn btn-disabled">...</span>
                  </template>
                  <button
                    class="join-item btn"
                    :class="{ 'btn-disabled': currentPage >= totalPages }"
                    @click="goToPage(currentPage + 1)"
                  >
                    »
                  </button>
                </div>
              </div>

              <!-- 分页信息 -->
              <div class="text-center mt-4 text-sm text-base-content/60">
                共 {{ productList.total || 0 }} 件商品，第 {{ currentPage }} / {{ totalPages }} 页
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="text-center py-12">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-base-content/30 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
                <path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
              <p class="text-lg text-base-content/60 mb-4">暂无商品</p>
              <button class="btn btn-primary" @click="$router.push('/')">返回首页</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import ProductCard from '../components/ProductCard.vue'
import { apiGetProductList } from '../api/product'
import { apiGetCategoryTree } from '../api/category'
import type { ProductListVO } from '../api/generated/models'
import type { CategoryTreeVO } from '../api/category'

const route = useRoute()

const loading = ref(false)
const error = ref<string | null>(null)
const productList = ref<ProductListVO>({
  products: [],
  total: 0,
  page: 1,
  size: 20
})
const categoryTree = ref<CategoryTreeVO[]>([])
const selectedCategories = ref<number[]>([])
const currentPage = ref(1)
const pageSize = 20

const totalPages = computed(() => {
  const total = productList.value.total || 0
  return Math.ceil(total / pageSize) || 1
})

const visiblePages = computed(() => {
  const pages: number[] = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    // 如果总页数少于等于7，显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 显示当前页附近的页码
    if (current <= 4) {
      // 前几页
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push(-1) // 省略号
      pages.push(total)
    } else if (current >= total - 3) {
      // 后几页
      pages.push(1)
      pages.push(-1) // 省略号
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      // 中间页
      pages.push(1)
      pages.push(-1) // 省略号
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push(-1) // 省略号
      pages.push(total)
    }
  }
  
  return pages
})

function isCategorySelected(categoryId?: number): boolean {
  if (!categoryId) return false
  return selectedCategories.value.includes(categoryId)
}

function toggleCategory(categoryId?: number) {
  if (!categoryId) return
  
  const index = selectedCategories.value.indexOf(categoryId)
  if (index > -1) {
    selectedCategories.value.splice(index, 1)
  } else {
    selectedCategories.value.push(categoryId)
  }
  
  // 重置到第一页
  currentPage.value = 1
  loadProducts()
}

// 递归获取所有分类ID
function getAllCategoryIds(categories: CategoryTreeVO[]): number[] {
  const ids: number[] = []
  for (const category of categories) {
    if (category.categoryId) {
      ids.push(category.categoryId)
    }
    if (category.children) {
      ids.push(...getAllCategoryIds(category.children))
    }
  }
  return ids
}

function toggleSelectAll() {
  if (selectedCategories.value.length === 0) {
    // 全选所有分类
    selectedCategories.value = getAllCategoryIds(categoryTree.value)
  } else {
    // 取消全选
    selectedCategories.value = []
  }
  
  // 重置到第一页
  currentPage.value = 1
  loadProducts()
}

async function loadCategories() {
  try {
    categoryTree.value = await apiGetCategoryTree()
    // 默认不选择任何分类（显示全部商品）
    selectedCategories.value = []
  } catch (e: any) {
    console.error('Failed to load categories:', e)
  }
}

async function loadProducts() {
  loading.value = true
  error.value = null
  
  try {
    // 如果未选择任何分类，不传分类参数（显示全部商品）
    // 否则传入选中的分类ID数组，后端会自动递归查询子分类的商品
    const categories = selectedCategories.value.length === 0
                      ? undefined
                      : selectedCategories.value
    
    const data = await apiGetProductList(categories, undefined, currentPage.value, pageSize)
    productList.value = data
  } catch (e: any) {
    error.value = e.message || '加载商品列表失败'
    console.error('Failed to load products:', e)
  } finally {
    loading.value = false
  }
}

function goToPage(page: number) {
  if (page < 1 || page > totalPages.value || page === currentPage.value) {
    return
  }
  currentPage.value = page
  loadProducts()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 监听路由参数变化
watch(() => route.query, (newQuery) => {
  if (newQuery.page) {
    const page = parseInt(newQuery.page as string, 10)
    if (!isNaN(page) && page > 0) {
      currentPage.value = page
    }
  }
}, { immediate: true })

onMounted(async () => {
  await loadCategories()
  await loadProducts()
})
</script>

<style scoped>
</style>

