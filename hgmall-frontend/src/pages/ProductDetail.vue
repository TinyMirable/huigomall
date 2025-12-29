<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-6xl">
        <!-- 加载状态 -->
        <div v-if="loading" class="text-center py-12">
          <span class="loading loading-spinner loading-lg"></span>
          <p class="mt-4 text-base-content/60">加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="text-center py-12">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-error/50 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p class="text-lg text-error mb-4">{{ error }}</p>
          <button class="btn btn-primary" @click="loadProductDetail">重试</button>
        </div>

        <!-- 商品详情 -->
        <div v-else-if="productDetail" class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- 左侧：商品图片 -->
          <div class="space-y-4">
            <div class="aspect-square bg-base-200 rounded-xl overflow-hidden">
              <img
                v-if="selectedSku?.imageUrl || productDetail.coverImageUrl"
                :src="selectedSku?.imageUrl || productDetail.coverImageUrl"
                :alt="productDetail.name"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center">
                <svg class="w-32 h-32 text-base-content/30" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
                </svg>
              </div>
            </div>
          </div>

          <!-- 右侧：商品信息 -->
          <div class="space-y-6">
            <!-- 商品名称 -->
            <div>
              <h1 class="text-3xl font-bold mb-2">{{ productDetail.name }}</h1>
              <div v-if="productDetail.merchantName" class="flex items-center gap-2">
                <span class="text-base-content/60">商家：</span>
                <button
                  v-if="shopId"
                  class="btn btn-link btn-sm p-0 h-auto min-h-0 text-primary"
                  @click="goToShop(shopId)"
                >
                  {{ productDetail.merchantName }}
                </button>
                <span v-else class="text-base-content/60">{{ productDetail.merchantName }}</span>
              </div>
            </div>

            <!-- 价格 -->
            <div class="flex items-baseline gap-4">
              <span class="text-4xl font-bold" style="color: rgb(255, 202, 57);">
                ¥{{ selectedSku?.price || (productDetail.skus && productDetail.skus.length > 0 ? productDetail.skus[0].price : 0) }}
              </span>
              <span v-if="productDetail.sales !== undefined" class="text-base-content/60">
                已售 {{ productDetail.sales }} 件
              </span>
            </div>

            <!-- SKU选择 -->
            <div v-if="productDetail.skus && productDetail.skus.length > 0" class="space-y-4">
              <div v-for="(sku, index) in productDetail.skus" :key="sku.skuId">
                <div class="flex items-center justify-between p-4 border rounded-lg cursor-pointer transition-all"
                     :class="selectedSku?.skuId === sku.skuId ? 'border-primary bg-primary/5' : 'border-base-300 hover:border-primary/50'"
                     @click="selectSku(sku)">
                  <div class="flex-1">
                    <div class="flex items-center gap-3">
                      <div v-if="sku.imageUrl" class="w-16 h-16 rounded overflow-hidden bg-base-200 flex-shrink-0">
                        <img :src="sku.imageUrl" :alt="sku.spec" class="w-full h-full object-cover" />
                      </div>
                      <div class="flex-1">
                        <div class="font-semibold">{{ formatSpec(sku.spec) || '默认规格' }}</div>
                        <div class="text-sm text-base-content/60">库存：{{ sku.stock || 0 }}</div>
                      </div>
                      <div class="text-xl font-bold" style="color: rgb(255, 202, 57);">
                        ¥{{ sku.price }}
                      </div>
                    </div>
                  </div>
                  <div v-if="selectedSku?.skuId === sku.skuId" class="ml-4">
                    <svg class="w-6 h-6 text-primary" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>

            <!-- 数量选择 -->
            <div class="flex items-center gap-4">
              <span class="font-semibold">数量：</span>
              <div class="flex items-center gap-2">
                <button
                  class="btn btn-sm btn-circle"
                  :class="{ 'btn-disabled': quantity <= 1 }"
                  @click="decreaseQuantity"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M20 12H4" />
                  </svg>
                </button>
                <input
                  v-model.number="quantity"
                  type="number"
                  min="1"
                  :max="selectedSku?.stock || 999"
                  class="input input-bordered w-20 text-center"
                  @change="validateQuantity"
                />
                <button
                  class="btn btn-sm btn-circle"
                  :class="{ 'btn-disabled': quantity >= (selectedSku?.stock || 999) }"
                  @click="increaseQuantity"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                  </svg>
                </button>
              </div>
              <span class="text-sm text-base-content/60">
                库存：{{ selectedSku?.stock || 0 }}
              </span>
            </div>

            <!-- 操作按钮 -->
            <div class="flex gap-4">
              <button
                class="btn flex-1 bg-base-content text-base-100 hover:bg-base-content/90 border-none"
                :class="{ 'btn-disabled': !canAddToCart || addingToCart }"
                @click="handleAddToCart"
              >
                <span v-if="addingToCart" class="loading loading-spinner loading-sm"></span>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                加入购物车
              </button>
              <button
                class="btn flex-1 bg-indigo-600 text-white hover:bg-indigo-700 border-none"
                :class="{ 'btn-disabled': !canAddToCart || buying }"
                @click="handleBuyNow"
              >
                <span v-if="buying" class="loading loading-spinner loading-sm"></span>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
                立即购买
              </button>
              <button class="btn btn-outline">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                收藏
              </button>
            </div>

            <!-- 商品描述 -->
            <div v-if="productDetail.description" class="card bg-base-200">
              <div class="card-body">
                <h3 class="card-title text-lg">商品描述</h3>
                <p class="whitespace-pre-wrap">{{ productDetail.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />

    <!-- 购买确认弹窗 -->
    <dialog ref="buyConfirmModal" class="modal">
      <div class="modal-box max-w-md shadow-2xl">
        <h3 class="font-bold text-xl mb-4 flex items-center gap-2">
          <div class="avatar placeholder">
            <div class="bg-primary text-primary-content rounded-full w-10">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </div>
          </div>
          确认购买
        </h3>
        <p class="text-base-content/70 mb-5 text-center">请确认以下商品信息</p>
        
        <!-- 商品信息卡片 -->
        <div class="card bg-gradient-to-br from-base-200 to-base-300 mb-5 shadow-lg border border-base-300">
          <div class="card-body p-5">
            <div class="flex gap-4">
              <!-- 商品图片 -->
              <div class="w-24 h-24 rounded-xl overflow-hidden bg-base-300 flex-shrink-0 shadow-md ring-2 ring-base-content/10">
                <img
                  v-if="selectedSku?.imageUrl || productDetail?.coverImageUrl"
                  :src="selectedSku?.imageUrl || productDetail?.coverImageUrl"
                  :alt="productDetail?.name"
                  class="w-full h-full object-cover"
                />
                <div v-else class="w-full h-full flex items-center justify-center">
                  <svg class="w-12 h-12 text-base-content/30" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
                  </svg>
                </div>
              </div>
              
              <!-- 商品详情 -->
              <div class="flex-1 min-w-0">
                <h4 class="font-bold text-base mb-2 line-clamp-2 text-base-content">{{ productDetail?.name }}</h4>
                <p class="text-sm text-base-content/70 mb-3 bg-base-100 px-2 py-1 rounded">
                  规格：{{ formatSpec(selectedSku?.spec) || '默认规格' }}
                </p>
                <div class="flex items-center justify-between">
                  <span class="text-sm text-base-content/60 font-medium">数量：<span class="text-base-content font-bold">{{ quantity }}</span></span>
                  <span class="text-xl font-bold" style="color: rgb(255, 202, 57);">
                    ¥{{ selectedSku?.price || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 价格明细 -->
        <div class="bg-base-200 rounded-xl p-4 mb-5 space-y-3">
          <div class="flex justify-between items-center text-sm">
            <span class="text-base-content/70">单价：</span>
            <span class="font-semibold text-base-content">¥{{ selectedSku?.price || 0 }}</span>
          </div>
          <div class="flex justify-between items-center text-sm">
            <span class="text-base-content/70">数量：</span>
            <span class="font-semibold text-base-content">{{ quantity }}</span>
          </div>
          <div class="divider my-1"></div>
          <div class="flex justify-between items-center pt-2">
            <span class="text-lg font-semibold text-base-content">小计：</span>
            <span class="text-2xl font-bold" style="color: rgb(255, 202, 57);">
              ¥{{ ((selectedSku?.price || 0) * quantity).toFixed(2) }}
            </span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="modal-action gap-3">
          <button class="btn btn-ghost flex-1" @click="closeBuyConfirmModal">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
            取消
          </button>
          <button class="btn btn-primary flex-1 shadow-lg" @click="confirmBuyNow">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
            </svg>
            确定购买
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeBuyConfirmModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import { apiGetProductDetail } from '../api/product'
import { apiAddCartItem, apiGetCartItems } from '../api/cart'
import type { ProductDetailVO, SkuVO } from '../api/generated/models'

const route = useRoute()
const router = useRouter()

const productDetail = ref<ProductDetailVO | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const selectedSku = ref<SkuVO | null>(null)
const quantity = ref(1)
const addingToCart = ref(false)
const buying = ref(false)
const buyConfirmModal = ref<HTMLDialogElement | null>(null)
const shopId = ref<number | null>(null)

const canAddToCart = computed(() => {
  if (!selectedSku.value) {
    return productDetail.value?.skus && productDetail.value.skus.length > 0
  }
  return selectedSku.value.stock && selectedSku.value.stock > 0 && quantity.value > 0
})

function pushToast(message: string, type: 'success' | 'error' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : 'alert-error'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

async function loadProductDetail() {
  const productId = route.params.id
  if (!productId || isNaN(Number(productId))) {
    error.value = '商品ID无效'
    return
  }

  loading.value = true
  error.value = null
  try {
    productDetail.value = await apiGetProductDetail(Number(productId))
    
    // 默认选择第一个SKU
    if (productDetail.value.skus && productDetail.value.skus.length > 0) {
      selectedSku.value = productDetail.value.skus[0]
      quantity.value = 1
    }
    
    // 从商品详情中获取shopId
    shopId.value = productDetail.value.shopId || null
  } catch (e: any) {
    error.value = e.message || '加载商品详情失败'
    console.error('Failed to load product detail:', e)
  } finally {
    loading.value = false
  }
}

function goToShop(shopId: number) {
  router.push(`/shop/${shopId}`)
}

function selectSku(sku: SkuVO) {
  selectedSku.value = sku
  // 重置数量，确保不超过库存
  if (sku.stock && quantity.value > sku.stock) {
    quantity.value = sku.stock
  }
}

function increaseQuantity() {
  const maxStock = selectedSku.value?.stock || 999
  if (quantity.value < maxStock) {
    quantity.value++
  }
}

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--
  }
}

function validateQuantity() {
  if (quantity.value < 1) {
    quantity.value = 1
  }
  const maxStock = selectedSku.value?.stock || 999
  if (quantity.value > maxStock) {
    quantity.value = maxStock
    pushToast(`数量不能超过库存（${maxStock}）`, 'error')
  }
}

function formatSpec(spec?: string): string {
  if (!spec) return ''
  try {
    const parsed = JSON.parse(spec)
    if (typeof parsed === 'object') {
      return Object.entries(parsed)
        .map(([key, value]) => `${key}: ${value}`)
        .join(', ')
    }
    return spec
  } catch {
    return spec
  }
}

async function handleAddToCart() {
  if (!canAddToCart.value) {
    pushToast('请选择规格或检查库存', 'error')
    return
  }

  if (!selectedSku.value || !selectedSku.value.skuId) {
    pushToast('请选择商品规格', 'error')
    return
  }

  // 检查是否登录
  const token = localStorage.getItem('access_token')
  if (!token) {
    pushToast('请先登录', 'error')
    router.push('/login')
    return
  }

  addingToCart.value = true
  try {
    await apiAddCartItem(selectedSku.value.skuId, quantity.value)
    pushToast('已添加到购物车', 'success')
  } catch (e: any) {
    if (e.message && e.message.includes('未登录')) {
      pushToast('请先登录', 'error')
      router.push('/login')
    } else {
      pushToast(`添加到购物车失败：${e.message || e}`, 'error')
    }
  } finally {
    addingToCart.value = false
  }
}

function openBuyConfirmModal() {
  buyConfirmModal.value?.showModal()
}

function closeBuyConfirmModal() {
  buyConfirmModal.value?.close()
}

async function confirmBuyNow() {
  closeBuyConfirmModal()
  
  if (!canAddToCart.value) {
    pushToast('请选择规格或检查库存', 'error')
    return
  }

  if (!selectedSku.value || !selectedSku.value.skuId) {
    pushToast('请选择商品规格', 'error')
    return
  }

  // 检查是否登录
  const token = localStorage.getItem('access_token')
  if (!token) {
    pushToast('请先登录', 'error')
    router.push('/login')
    return
  }

  buying.value = true
  try {
    // 先添加到购物车
    await apiAddCartItem(selectedSku.value.skuId, quantity.value)
    
    // 获取购物车项ID（需要重新获取购物车列表）
    const cartItems = await apiGetCartItems()
    const cartItem = cartItems.find(item => item.skuId === selectedSku.value?.skuId)
    
    if (cartItem && cartItem.itemId) {
      // 跳转到订单确认页面
      router.push({
        path: '/checkout',
        query: {
          cartItemIds: [cartItem.itemId]
        }
      })
    } else {
      // 如果找不到购物车项，使用立即购买数据
      router.push({
        path: '/checkout',
        query: {
          buyNow: encodeURIComponent(JSON.stringify({
            skuId: selectedSku.value.skuId,
            productId: productDetail.value?.productId,
            name: productDetail.value?.name,
            price: selectedSku.value.price,
            spec: selectedSku.value.spec,
            quantity: quantity.value,
            coverImageUrl: productDetail.value?.coverImageUrl,
            skuImageUrl: selectedSku.value.imageUrl
          }))
        }
      })
    }
  } catch (e: any) {
    if (e.message && e.message.includes('未登录')) {
      pushToast('请先登录', 'error')
      router.push('/login')
    } else {
      pushToast(`操作失败：${e.message || e}`, 'error')
    }
  } finally {
    buying.value = false
  }
}

async function handleBuyNow() {
  if (!canAddToCart.value) {
    pushToast('请选择规格或检查库存', 'error')
    return
  }

  if (!selectedSku.value || !selectedSku.value.skuId) {
    pushToast('请选择商品规格', 'error')
    return
  }

  // 检查是否登录
  const token = localStorage.getItem('access_token')
  if (!token) {
    pushToast('请先登录', 'error')
    router.push('/login')
    return
  }

  // 显示确认弹窗
  openBuyConfirmModal()
}

onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
</style>

