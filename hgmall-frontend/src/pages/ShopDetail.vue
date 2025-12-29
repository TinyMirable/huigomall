<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-7xl">
        <!-- 加载状态 -->
        <div v-if="loading" class="text-center py-12">
          <span class="loading loading-spinner loading-lg"></span>
          <p class="mt-4 text-base-content/60">加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="alert alert-error">
          <span>{{ error }}</span>
          <button class="btn btn-sm" @click="loadShopData">重试</button>
        </div>

        <!-- 店铺内容 -->
        <div v-else-if="shop">
          <!-- 店铺信息卡片 -->
          <div class="card bg-base-100 shadow-lg mb-6">
            <div class="card-body">
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h1 class="text-3xl font-bold mb-2">{{ shop.name }}</h1>
                  <p v-if="shop.description" class="text-base-content/70 mb-4">{{ shop.description }}</p>
                  <p v-else class="text-base-content/50 italic mb-4">暂无描述</p>
                  <div class="flex items-center gap-4 text-sm text-base-content/60">
                    <span>店铺ID：{{ shop.shopId }}</span>
                    <ShopStatusBadge :status="shop.status" />
                  </div>
                </div>
                <!-- 管理我的店铺按钮和编辑按钮（仅商家显示） -->
                <div v-if="isShopOwner" class="flex gap-2">
                  <button
                    class="btn btn-ghost btn-sm"
                    @click="openEditShopModal"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    编辑店铺
                  </button>
                  <button
                    v-if="!isManagementMode"
                    class="btn btn-primary"
                    @click="isManagementMode = true; currentPage = 1; loadProducts()"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                      <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                    管理我的店铺
                  </button>
                  <button
                    v-else
                    class="btn btn-ghost"
                    @click="isManagementMode = false; currentPage = 1; loadProducts()"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                    返回浏览模式
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="flex gap-6">
            <!-- 左侧筛选器 -->
            <!-- 普通模式：分类筛选 -->
            <div v-if="!isManagementMode" class="w-64 flex-shrink-0">
              <div class="card bg-base-100 shadow-sm sticky top-24">
                <div class="card-body p-4">
                  <h2 class="card-title text-lg mb-4">分类筛选</h2>
                  <div class="space-y-3">
                    <label class="cursor-pointer label gap-3 p-2 rounded-lg hover:bg-base-200 transition-colors">
                      <input
                        type="checkbox"
                        class="checkbox checkbox-primary"
                        :checked="selectedCategories.length === 0 || selectedCategories.length === allCategories.length"
                        @change="toggleSelectAll"
                      />
                      <span class="label-text font-semibold">全部分类</span>
                    </label>
                    <div class="divider my-2"></div>
                    <label
                      v-for="category in allCategories"
                      :key="category.categoryId"
                      class="cursor-pointer label gap-3 p-2 rounded-lg hover:bg-base-200 transition-colors"
                    >
                      <input
                        type="checkbox"
                        class="checkbox checkbox-primary"
                        :value="category.categoryId"
                        :checked="isCategorySelected(category.categoryId)"
                        @change="toggleCategory(category.categoryId)"
                      />
                      <span class="label-text">{{ category.name }}</span>
                    </label>
                  </div>
                </div>
              </div>
            </div>

            <!-- 管理模式：状态筛选 -->
            <div v-else class="w-64 flex-shrink-0">
              <div class="card bg-base-100 shadow-sm sticky top-24">
                <div class="card-body p-4">
                  <h2 class="card-title text-lg mb-4">商品筛选</h2>
                  <div class="space-y-3">
                    <label class="cursor-pointer label gap-3 p-2 rounded-lg hover:bg-base-200 transition-colors">
                      <input
                        type="radio"
                        name="statusFilter"
                        class="radio radio-primary"
                        :value="undefined"
                        :checked="selectedStatus === undefined"
                        @change="selectedStatus = undefined; currentPage = 1; loadProducts()"
                      />
                      <span class="label-text font-semibold">全部商品</span>
                    </label>
                    <label class="cursor-pointer label gap-3 p-2 rounded-lg hover:bg-base-200 transition-colors">
                      <input
                        type="radio"
                        name="statusFilter"
                        class="radio radio-primary"
                        :value="1"
                        :checked="selectedStatus === 1"
                        @change="selectedStatus = 1; currentPage = 1; loadProducts()"
                      />
                      <span class="label-text">已上架</span>
                    </label>
                    <label class="cursor-pointer label gap-3 p-2 rounded-lg hover:bg-base-200 transition-colors">
                      <input
                        type="radio"
                        name="statusFilter"
                        class="radio radio-primary"
                        :value="0"
                        :checked="selectedStatus === 0"
                        @change="selectedStatus = 0; currentPage = 1; loadProducts()"
                      />
                      <span class="label-text">已下架</span>
                    </label>
                  </div>
                </div>
              </div>
            </div>

            <!-- 右侧内容区域 -->
            <div class="flex-1 min-w-0">
              <!-- 管理模式：管理工具栏 -->
              <div v-if="isManagementMode" class="card bg-base-100 shadow-sm mb-6">
                <div class="card-body p-4">
                  <div class="flex items-center justify-between">
                    <h2 class="text-xl font-bold">商品管理</h2>
                    <button class="btn btn-primary btn-sm" @click="goToCreateProduct">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                      </svg>
                      添加商品
                    </button>
                  </div>
                </div>
              </div>

              <!-- 商品列表 -->
              <div v-if="filteredProducts.length > 0">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                  <!-- 普通模式：使用ProductCard -->
                  <template v-if="!isManagementMode">
                    <ProductCard
                      v-for="product in filteredProducts"
                      :key="`product-${product.productId}`"
                      :name="product.name || ''"
                      :price="product.minPrice || 0"
                      :items-sold="product.sales"
                      :image-url="product.coverImageUrl"
                      :product-id="product.productId"
                      :merchant-name="shop?.name || ''"
                      :shop-id="shop?.shopId"
                    />
                  </template>

                  <!-- 管理模式：显示商品卡片和管理按钮 -->
                  <template v-else>
                    <div
                      v-for="product in filteredProducts"
                      :key="`manage-${product.productId}`"
                      class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow"
                    >
                    <div class="card-body p-4">
                      <!-- 商品图片 -->
                      <figure class="bg-base-200 h-48 rounded-lg overflow-hidden mb-4 relative">
                        <img
                          v-if="product.coverImageUrl"
                          :src="product.coverImageUrl"
                          :alt="product.name"
                          class="w-full h-full object-cover cursor-pointer"
                          @click="goToProductDetail(product.productId!)"
                        />
                        <div v-else class="w-full h-full flex items-center justify-center">
                          <svg class="w-24 h-24 text-base-content/30" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
                          </svg>
                        </div>
                        <!-- 编辑按钮（右上角，大且醒目） -->
                        <button
                          class="absolute top-2 right-2 btn btn-primary btn-sm shadow-lg z-10"
                          @click.stop="goToEditProduct(product.productId!)"
                          title="编辑商品"
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                      </figure>
                      <!-- 商品信息 -->
                      <h3 class="card-title text-base font-semibold line-clamp-2 mb-2 cursor-pointer" @click="goToProductDetail(product.productId!)">
                        {{ product.name }}
                      </h3>
                      <p class="text-lg font-bold mb-2" style="color: rgb(255, 202, 57);">¥{{ product.minPrice || 0 }}</p>
                      <p v-if="product.sales !== undefined" class="text-sm text-base-content/60 mb-2">已售 {{ product.sales }} 件</p>
                      <div class="flex items-center gap-2 mb-2">
                        <div class="badge badge-sm" :class="getProductStatusBadgeClass(product.status)">
                          {{ getProductStatusText(product.status) }}
                        </div>
                      </div>
                      <!-- 管理操作按钮 -->
                      <div class="card-actions justify-end mt-4">
                        <button
                          v-if="product.status === 0"
                          class="btn btn-success btn-xs"
                          @click="openOnShelfModal(product)"
                          :disabled="operating"
                        >
                          上架
                        </button>
                        <button
                          v-if="product.status === 1"
                          class="btn btn-warning btn-xs"
                          @click="openOffShelfModal(product)"
                          :disabled="operating"
                        >
                          下架
                        </button>
                      </div>
                    </div>
                  </div>
                  </template>
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
                <button v-if="isManagementMode" class="btn btn-primary" @click="goToCreateProduct">添加第一个商品</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />

    <!-- 上架商品弹窗 -->
    <OnShelfProductModal
      v-if="isManagementMode"
      v-model="showOnShelfModal"
      :loading="operating"
      :product="selectedProduct"
      :email="userEmail"
      :send-code-fn="sendOnShelfCode"
      @submit="handleOnShelf"
      @code-error="(e) => pushToast(e.message, 'error')"
    />

    <!-- 下架商品弹窗 -->
    <OffShelfProductModal
      v-if="isManagementMode"
      v-model="showOffShelfModal"
      :loading="operating"
      :product="selectedProduct"
      :email="userEmail"
      :send-code-fn="sendOffShelfCode"
      @submit="handleOffShelf"
      @code-error="(e) => pushToast(e.message, 'error')"
    />

    <!-- 编辑店铺弹窗 -->
    <EditShopModal
      v-model="showEditShopModal"
      :loading="updatingShop"
      :shop="shop"
      :email="userEmail"
      :send-code-fn="sendEditShopCode"
      @submit="handleUpdateShop"
      @code-error="(e) => pushToast(e.message, 'error')"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import ProductCard from '../components/ProductCard.vue'
import ShopStatusBadge from '../components/ShopStatusBadge.vue'
import EditShopModal from '../components/EditShopModal.vue'
import OnShelfProductModal from '../components/OnShelfProductModal.vue'
import OffShelfProductModal from '../components/OffShelfProductModal.vue'
import { apiGetShop, apiUpdateShop } from '../api/shop'
import { apiGetShopProducts, apiOnShelfProduct, apiOffShelfProduct } from '../api/shopProduct'
import { apiGetCategories } from '../api/category'
import { apiSendVerificationCode } from '../api/auth'
import { apiGetUserSummary } from '../api/user'
import type { ShopVO, ShopProductVO, ShopProductListVO, Category, UserSummaryVO } from '../api/generated/models'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const error = ref<string | null>(null)
const shop = ref<ShopVO | null>(null)
const productList = ref<ShopProductListVO>({
  products: [],
  total: 0,
  page: 1,
  size: 20
})
const isManagementMode = ref(false) // 管理模式开关
const selectedStatus = ref<number | undefined>(undefined)
const selectedCategories = ref<number[]>([])
const allCategories = ref<Category[]>([])
const currentPage = ref(1)
const pageSize = 20
const operating = ref(false)
const showEditShopModal = ref(false)
const showOnShelfModal = ref(false)
const showOffShelfModal = ref(false)
const updatingShop = ref(false)
const userEmail = ref<string>('')
const userSummary = ref<UserSummaryVO>({})
const selectedProduct = ref<ShopProductVO | null>(null)

// 判断是否为店铺所有者（商家且店铺属于该商家，但不包括管理员）
// 管理员有单独的管理面板，不与商家共用界面
const isShopOwner = computed(() => {
  // 管理员不显示管理按钮
  if (userSummary.value.role === 'ADMIN') {
    return false
  }
  // 只有商家（且已审核通过）才能管理店铺
  if (!userSummary.value.merchantStatus || userSummary.value.merchantStatus !== 'APPROVED') {
    return false
  }
  // 验证店铺是否属于当前商家
  if (!shop.value?.merchantId || !userSummary.value.merchantId) {
    return false
  }
  // 比较店铺的商家ID和当前用户的商家ID
  return shop.value.merchantId === userSummary.value.merchantId
})

// 过滤后的商品列表（普通模式按分类筛选）
const filteredProducts = computed(() => {
  if (isManagementMode.value) {
    // 管理模式：直接返回所有商品（已按状态筛选）
    return productList.value.products || []
  } else {
    // 普通模式：直接返回已加载的商品（已按分类筛选）
    return productList.value.products || []
  }
})

const totalPages = computed(() => {
  const total = productList.value.total || 0
  return Math.ceil(total / pageSize) || 1
})

const visiblePages = computed(() => {
  const pages: number[] = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push(-1)
      pages.push(total)
    } else if (current >= total - 3) {
      pages.push(1)
      pages.push(-1)
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push(-1)
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push(-1)
      pages.push(total)
    }
  }
  
  return pages
})


function getProductStatusText(status?: number): string {
  const statusMap: Record<number, string> = {
    0: '已下架',
    1: '已上架'
  }
  return statusMap[status ?? -1] || '未知状态'
}

function getProductStatusBadgeClass(status?: number): string {
  const classMap: Record<number, string> = {
    0: 'badge-ghost',
    1: 'badge-success'
  }
  return classMap[status ?? -1] || 'badge-ghost'
}

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
  
  // 重置到第一页并重新加载商品
  currentPage.value = 1
  loadProducts()
}

function toggleSelectAll() {
  if (selectedCategories.value.length === 0 || selectedCategories.value.length === allCategories.value.length) {
    // 全选
    selectedCategories.value = allCategories.value
      .map(cat => cat.categoryId)
      .filter((id): id is number => id !== undefined) as number[]
  } else {
    // 取消全选
    selectedCategories.value = []
  }
  
  // 重置到第一页并重新加载商品
  currentPage.value = 1
  loadProducts()
}

async function loadCategories() {
  try {
    const categories = await apiGetCategories()
    allCategories.value = categories
    
    // 默认全选所有分类
    selectedCategories.value = categories
      .map(cat => cat.categoryId)
      .filter((id): id is number => id !== undefined) as number[]
  } catch (e: any) {
    console.error('Failed to load categories:', e)
  }
}

async function loadUserSummary() {
  try {
    const summary = await apiGetUserSummary()
    userSummary.value = summary
    userEmail.value = summary.email || localStorage.getItem('user_email') || ''
    if (summary.email) {
      localStorage.setItem('user_email', summary.email)
    }
  } catch (e: any) {
    console.error('Failed to load user summary:', e)
    userEmail.value = localStorage.getItem('user_email') || ''
  }
}

async function loadShopData() {
  const shopId = parseInt(route.params.shopId as string, 10)
  if (isNaN(shopId)) {
    error.value = '无效的店铺ID'
    return
  }

  loading.value = true
  error.value = null
  
  try {
    shop.value = await apiGetShop(shopId)
    await loadProducts()
  } catch (e: any) {
    error.value = e.message || '加载店铺信息失败'
    console.error('Failed to load shop:', e)
  } finally {
    loading.value = false
  }
}

async function loadProducts() {
  if (!shop.value?.shopId) return

  try {
    // 普通模式：只显示已上架商品（status=1），不需要认证，按分类筛选
    // 管理模式：显示所有商品（selectedStatus），需要认证，不按分类筛选
    const status = isManagementMode.value ? selectedStatus.value : 1
    const requireAuth = isManagementMode.value && isShopOwner.value
    
    // 普通模式：根据选中的分类筛选；管理模式：不筛选分类
    const categories = isManagementMode.value ? undefined : 
      (selectedCategories.value.length === 0 || selectedCategories.value.length === allCategories.value.length 
        ? undefined 
        : selectedCategories.value)
    
    productList.value = await apiGetShopProducts(
      shop.value.shopId,
      status,
      categories,
      currentPage.value,
      pageSize,
      requireAuth
    )
  } catch (e: any) {
    pushToast(`加载商品列表失败：${e.message || e}`, 'error')
    console.error('Failed to load products:', e)
  }
}

function goToPage(page: number) {
  if (page < 1 || page > totalPages.value || page === currentPage.value) {
    return
  }
  currentPage.value = page
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToProductDetail(productId?: number) {
  if (productId) {
    router.push(`/product/${productId}`)
  }
}

function goToCreateProduct() {
  if (shop.value?.shopId) {
    router.push(`/shop/${shop.value.shopId}/create-product`)
  }
}

function goToEditProduct(productId?: number) {
  if (shop.value?.shopId && productId) {
    router.push(`/shop/${shop.value.shopId}/edit-product/${productId}`)
  }
}

// 上架商品相关
function openOnShelfModal(product: ShopProductVO) {
  selectedProduct.value = product
  showOnShelfModal.value = true
}

async function sendOnShelfCode(emailAddr: string): Promise<void> {
  if (!emailAddr) {
    throw new Error('无法获取邮箱地址，请先完善个人信息')
  }
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleOnShelf(code: string) {
  if (!selectedProduct.value?.productId) {
    pushToast('商品信息错误', 'error')
    return
  }

  operating.value = true
  try {
    await apiOnShelfProduct(selectedProduct.value.productId, code)
    pushToast('商品已上架', 'success')
    showOnShelfModal.value = false
    selectedProduct.value = null
    await loadProducts()
  } catch (e: any) {
    pushToast(`上架失败：${e.message || e}`, 'error')
  } finally {
    operating.value = false
  }
}

// 下架商品相关
function openOffShelfModal(product: ShopProductVO) {
  selectedProduct.value = product
  showOffShelfModal.value = true
}

async function sendOffShelfCode(emailAddr: string): Promise<void> {
  if (!emailAddr) {
    throw new Error('无法获取邮箱地址，请先完善个人信息')
  }
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleOffShelf(code: string) {
  if (!selectedProduct.value?.productId) {
    pushToast('商品信息错误', 'error')
    return
  }

  operating.value = true
  try {
    await apiOffShelfProduct(selectedProduct.value.productId, code)
    pushToast('商品已下架', 'success')
    showOffShelfModal.value = false
    selectedProduct.value = null
    await loadProducts()
  } catch (e: any) {
    pushToast(`下架失败：${e.message || e}`, 'error')
  } finally {
    operating.value = false
  }
}

function pushToast(message: string, type: 'success' | 'error' | 'info' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : type === 'error' ? 'alert-error' : 'alert-info'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

// 编辑店铺相关
function openEditShopModal() {
  if (!shop.value) return
  showEditShopModal.value = true
}

async function sendEditShopCode(emailAddr: string): Promise<void> {
  if (!emailAddr) {
    throw new Error('无法获取邮箱地址，请先完善个人信息')
  }
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleUpdateShop(data: { name: string; description?: string; code: string }) {
  if (!shop.value?.shopId) {
    pushToast('店铺信息错误', 'error')
    return
  }

  const trimmedName = data.name.trim()
  if (!trimmedName) {
    pushToast('请输入店铺名称', 'error')
    return
  }
  if (trimmedName.length < 2) {
    pushToast('店铺名称长度至少为2位', 'error')
    return
  }
  if (trimmedName.length > 100) {
    pushToast('店铺名称长度不能超过100位', 'error')
    return
  }

  updatingShop.value = true
  try {
    const updatedShop = await apiUpdateShop(shop.value.shopId, {
      name: trimmedName,
      description: data.description,
      code: data.code
    })
    shop.value = updatedShop
    pushToast('店铺信息更新成功', 'success')
    showEditShopModal.value = false
  } catch (e: any) {
    pushToast(`更新失败：${e.message || e}`, 'error')
  } finally {
    updatingShop.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadUserSummary(), loadCategories()])
  await loadShopData()
  
  // 检查URL查询参数，如果是管理模式且用户是商家，则自动进入管理模式
  if (route.query.mode === 'management' && isShopOwner.value) {
    isManagementMode.value = true
    currentPage.value = 1
    loadProducts()
  }
})
</script>

<style scoped>
</style>
