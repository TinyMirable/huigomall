<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-6xl">
        <h1 class="text-3xl font-bold mb-6">购物车</h1>

        <!-- 加载状态 -->
        <div v-if="loading" class="text-center py-12">
          <span class="loading loading-spinner loading-lg"></span>
          <p class="mt-4 text-base-content/60">加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="alert alert-error">
          <span>{{ error }}</span>
          <button class="btn btn-sm" @click="loadCartItems">重试</button>
        </div>

        <!-- 空购物车 -->
        <div v-else-if="cartItems.length === 0" class="text-center py-12">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-base-content/30 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <p class="text-lg text-base-content/60 mb-4">购物车是空的</p>
          <button class="btn btn-primary" @click="$router.push('/')">去购物</button>
        </div>

        <!-- 购物车内容 -->
        <div v-else class="space-y-4">
          <!-- 全选和操作栏 -->
          <div class="card bg-base-100 shadow-sm">
            <div class="card-body p-4">
              <div class="flex items-center justify-between">
                <label class="label cursor-pointer gap-3">
                  <input
                    type="checkbox"
                    class="checkbox checkbox-primary"
                    :checked="allSelected"
                    @change="toggleSelectAll"
                  />
                  <span class="label-text font-semibold">全选</span>
                </label>
                <div class="flex items-center gap-4">
                  <span class="text-sm text-base-content/60">
                    已选 {{ selectedCount }} 件
                  </span>
                  <button
                    class="btn btn-sm btn-ghost text-error"
                    :class="{ 'btn-disabled': selectedCount === 0 }"
                    @click="handleBatchDelete"
                  >
                    删除选中
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 购物车商品列表 -->
          <div class="space-y-4">
            <div
              v-for="item in cartItems"
              :key="item.itemId"
              class="card bg-base-100 shadow-sm cursor-pointer hover:shadow-md transition-shadow"
              @click="goToProductDetail(item.productId!)"
            >
              <div class="card-body p-4">
                <div class="flex items-center gap-4">
                  <!-- 选择框 -->
                  <input
                    type="checkbox"
                    class="checkbox checkbox-primary"
                    :checked="selectedItems.has(item.itemId!)"
                    @change.stop="toggleSelectItem(item.itemId!)"
                    @click.stop
                  />

                  <!-- 商品图片 -->
                  <div class="w-24 h-24 rounded-lg overflow-hidden bg-base-200 flex-shrink-0" @click.stop="goToProductDetail(item.productId!)">
                    <img
                      v-if="item.skuImageUrl || item.productImageUrl"
                      :src="item.skuImageUrl || item.productImageUrl"
                      :alt="item.productName"
                      class="w-full h-full object-cover"
                    />
                    <div v-else class="w-full h-full flex items-center justify-center">
                      <svg class="w-12 h-12 text-base-content/30" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
                      </svg>
                    </div>
                  </div>

                  <!-- 商品信息 -->
                  <div class="flex-1 min-w-0" @click.stop="goToProductDetail(item.productId!)">
                    <h3 class="font-semibold text-lg mb-1 truncate">
                      {{ item.productName }}
                    </h3>
                    <p v-if="item.spec" class="text-sm text-base-content/60 mb-2">
                      规格：{{ formatSpec(item.spec) }}
                    </p>
                    <p v-if="item.shopName" class="text-sm text-base-content/60">
                      店铺：{{ item.shopName }}
                    </p>
                    <div v-if="!item.available" class="badge badge-warning badge-sm mt-2">
                      商品不可用（已下架或库存不足）
                    </div>
                  </div>

                  <!-- 价格 -->
                  <div class="text-right" @click.stop="goToProductDetail(item.productId!)">
                    <div class="text-xl font-bold mb-2" style="color: rgb(255, 202, 57);">
                      ¥{{ item.price?.toFixed(2) || '0.00' }}
                    </div>
                    <div class="text-sm text-base-content/60">
                      小计：<span class="font-semibold" style="color: rgb(255, 202, 57);">
                        ¥{{ ((item.price || 0) * (item.num || 0)).toFixed(2) }}
                      </span>
                    </div>
                  </div>

                  <!-- 数量控制 -->
                  <div class="flex flex-col items-center gap-2" @click.stop>
                    <div class="flex items-center gap-2">
                      <button
                        class="btn btn-sm btn-circle"
                        :class="{ 'btn-disabled': (item.num || 0) <= 1 || updatingItems.has(item.itemId!) }"
                        @click="decreaseQuantity(item)"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M20 12H4" />
                        </svg>
                      </button>
                      <input
                        :ref="el => setInputRef(el, item.itemId!)"
                        type="number"
                        :value="item.num"
                        min="1"
                        :max="item.stock || 999"
                        class="input input-bordered w-20 text-center"
                        @blur="handleQuantityBlur(item)"
                        @keyup.enter="(e: any) => e.target.blur()"
                      />
                      <button
                        class="btn btn-sm btn-circle"
                        :class="{ 'btn-disabled': (item.num || 0) >= (item.stock || 999) || updatingItems.has(item.itemId!) }"
                        @click="increaseQuantity(item)"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                        </svg>
                      </button>
                    </div>
                    <span class="text-xs text-base-content/60">
                      库存：{{ item.stock || 0 }}
                    </span>
                  </div>

                  <!-- 删除按钮 -->
                  <button
                    class="btn btn-ghost btn-sm text-error"
                    :class="{ 'btn-disabled': deletingItems.has(item.itemId!) }"
                    @click.stop="handleDeleteItem(item.itemId!)"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 结算栏 -->
          <div class="card bg-base-100 shadow-lg sticky bottom-0">
            <div class="card-body p-4">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-4">
                  <label class="label cursor-pointer gap-3">
                    <input
                      type="checkbox"
                      class="checkbox checkbox-primary"
                      :checked="allSelected"
                      @change="toggleSelectAll"
                    />
                    <span class="label-text">全选</span>
                  </label>
                  <div class="text-base-content/60">
                    共 {{ selectedCount }} 件商品
                  </div>
                </div>
                <div class="flex items-center gap-6">
                  <div class="text-right">
                    <div class="text-sm text-base-content/60">合计：</div>
                    <div class="text-2xl font-bold" style="color: rgb(255, 202, 57);">
                      ¥{{ totalPrice.toFixed(2) }}
                    </div>
                  </div>
                  <button
                    class="btn btn-primary btn-lg"
                    :class="{ 'btn-disabled': selectedCount === 0 || !hasAvailableItems }"
                    @click="handleCheckout"
                  >
                    结算 ({{ selectedCount }})
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import { apiGetCartItems, apiUpdateCartItem, apiDeleteCartItem } from '../api/cart'
import type { CartItemVO } from '../api/generated/models'

const router = useRouter()

const cartItems = ref<CartItemVO[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const selectedItems = ref<Set<number>>(new Set())
const updatingItems = ref<Set<number>>(new Set())
const deletingItems = ref<Set<number>>(new Set())
const inputRefs = ref<Map<number, HTMLInputElement>>(new Map())

// 存储每个商品的数量（用于失焦更新）
const quantityCache = ref<Map<number, number>>(new Map())

const allSelected = computed(() => {
  if (cartItems.value.length === 0) return false
  return cartItems.value.every(item => selectedItems.value.has(item.itemId!))
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => selectedItems.value.has(item.itemId!)).length
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => selectedItems.value.has(item.itemId!))
    .reduce((sum, item) => {
      return sum + ((item.price || 0) * (item.num || 0))
    }, 0)
})

const hasAvailableItems = computed(() => {
  return cartItems.value.some(item => selectedItems.value.has(item.itemId!) && item.available)
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

function setInputRef(el: any, itemId: number) {
  if (el) {
    inputRefs.value.set(itemId, el)
  }
}

async function loadCartItems() {
  const token = localStorage.getItem('access_token')
  if (!token) {
    router.push('/login')
    return
  }

  loading.value = true
  error.value = null
  try {
    cartItems.value = await apiGetCartItems()
    // 初始化数量缓存
    cartItems.value.forEach(item => {
      if (item.itemId && item.num) {
        quantityCache.value.set(item.itemId, item.num)
      }
    })
  } catch (e: any) {
    if (e.message && e.message.includes('未登录')) {
      router.push('/login')
    } else {
      error.value = e.message || '加载购物车失败'
      console.error('Failed to load cart items:', e)
    }
  } finally {
    loading.value = false
  }
}

function toggleSelectAll() {
  if (allSelected.value) {
    selectedItems.value.clear()
  } else {
    cartItems.value.forEach(item => {
      if (item.itemId) {
        selectedItems.value.add(item.itemId)
      }
    })
  }
}

function toggleSelectItem(itemId: number) {
  if (selectedItems.value.has(itemId)) {
    selectedItems.value.delete(itemId)
  } else {
    selectedItems.value.add(itemId)
  }
}

function increaseQuantity(item: CartItemVO) {
  if (!item.itemId || !item.num) return
  const maxStock = item.stock || 999
  if (item.num >= maxStock) {
    pushToast(`数量不能超过库存（${maxStock}）`, 'error')
    return
  }
  item.num++
  // 更新缓存，但不立即更新后端
  quantityCache.value.set(item.itemId, item.num)
}

function decreaseQuantity(item: CartItemVO) {
  if (!item.itemId || !item.num) return
  if (item.num <= 1) {
    return
  }
  item.num--
  // 更新缓存，但不立即更新后端
  quantityCache.value.set(item.itemId, item.num)
}

// 失焦时更新后端
async function handleQuantityBlur(item: CartItemVO) {
  if (!item.itemId || !item.num) return

  const cachedQuantity = quantityCache.value.get(item.itemId)
  // 如果数量没有变化，不需要更新
  if (cachedQuantity === item.num) {
    return
  }

  // 验证数量
  if (item.num < 1) {
    item.num = 1
    pushToast('数量不能小于1', 'error')
    return
  }

  const maxStock = item.stock || 999
  if (item.num > maxStock) {
    item.num = maxStock
    pushToast(`数量不能超过库存（${maxStock}）`, 'error')
    return
  }

  updatingItems.value.add(item.itemId)
  try {
    const updated = await apiUpdateCartItem(item.itemId, item.num)
    // 更新本地数据
    const index = cartItems.value.findIndex(i => i.itemId === item.itemId)
    if (index !== -1) {
      cartItems.value[index] = updated
      quantityCache.value.set(item.itemId, updated.num || item.num)
    }
    pushToast('数量已更新', 'success')
  } catch (e: any) {
    // 恢复原值
    item.num = cachedQuantity || 1
    pushToast(`更新失败：${e.message || e}`, 'error')
  } finally {
    updatingItems.value.delete(item.itemId)
  }
}

async function handleDeleteItem(itemId: number) {
  if (!confirm('确定要删除这个商品吗？')) {
    return
  }

  deletingItems.value.add(itemId)
  try {
    await apiDeleteCartItem(itemId)
    cartItems.value = cartItems.value.filter(item => item.itemId !== itemId)
    selectedItems.value.delete(itemId)
    quantityCache.value.delete(itemId)
    pushToast('已删除', 'success')
  } catch (e: any) {
    pushToast(`删除失败：${e.message || e}`, 'error')
  } finally {
    deletingItems.value.delete(itemId)
  }
}

async function handleBatchDelete() {
  const itemsToDelete = Array.from(selectedItems.value)
  if (itemsToDelete.length === 0) {
    pushToast('请选择要删除的商品', 'error')
    return
  }

  if (!confirm(`确定要删除选中的 ${itemsToDelete.length} 件商品吗？`)) {
    return
  }

  try {
    await Promise.all(itemsToDelete.map(itemId => apiDeleteCartItem(itemId)))
    cartItems.value = cartItems.value.filter(item => !selectedItems.value.has(item.itemId!))
    selectedItems.value.clear()
    itemsToDelete.forEach(itemId => quantityCache.value.delete(itemId))
    pushToast(`已删除 ${itemsToDelete.length} 件商品`, 'success')
  } catch (e: any) {
    pushToast(`删除失败：${e.message || e}`, 'error')
    // 重新加载购物车以同步状态
    await loadCartItems()
  }
}

function handleCheckout() {
  if (selectedCount.value === 0) {
    pushToast('请选择要结算的商品', 'error')
    return
  }

  if (!hasAvailableItems.value) {
    pushToast('所选商品中包含不可用商品，请先移除', 'error')
    return
  }

  // 获取选中的商品ID列表
  const selectedItemIds = cartItems.value
    .filter(item => selectedItems.value.has(item.itemId!))
    .map(item => item.itemId!)
    .filter((id): id is number => id !== undefined && id !== null)

  // 跳转到订单确认页面
  router.push({
    path: '/checkout',
    query: {
      cartItemIds: selectedItemIds
    }
  })
}

function goToProductDetail(productId: number) {
  if (productId) {
    router.push(`/product/${productId}`)
  }
}

onMounted(() => {
  loadCartItems()
})
</script>

<style scoped>
</style>

