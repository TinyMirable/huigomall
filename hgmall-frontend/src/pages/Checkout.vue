<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-6xl">
        <h1 class="text-3xl font-bold mb-6">确认订单</h1>

        <!-- 加载状态 -->
        <div v-if="loading" class="text-center py-12">
          <span class="loading loading-spinner loading-lg"></span>
          <p class="mt-4 text-base-content/60">加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="alert alert-error">
          <span>{{ error }}</span>
          <button class="btn btn-sm" @click="loadData">重试</button>
        </div>

        <!-- 订单确认内容 -->
        <div v-else-if="!batchOrder" class="space-y-6">
          <!-- 收货地址选择 -->
          <div class="card bg-base-100 shadow-sm">
            <div class="card-body">
              <h2 class="card-title mb-4">收货地址</h2>
              <div v-if="addresses.length === 0" class="text-center py-8">
                <p class="text-base-content/60 mb-4">暂无收货地址</p>
                <button class="btn btn-primary" @click="$router.push('/addresses')">添加收货地址</button>
              </div>
              <div v-else class="space-y-3">
                <label
                  v-for="address in addresses"
                  :key="address.addressId"
                  class="flex items-start gap-4 p-4 border rounded-lg cursor-pointer transition-all"
                  :class="selectedAddressId === address.addressId ? 'border-primary bg-primary/5' : 'border-base-300 hover:border-primary/50'"
                >
                  <input
                    type="radio"
                    name="address"
                    :value="address.addressId"
                    v-model="selectedAddressId"
                    class="radio radio-primary mt-1"
                  />
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="font-semibold">{{ address.receiverName }}</span>
                      <span class="text-base-content/60">{{ address.receiverPhone }}</span>
                      <span v-if="address.isDefault" class="badge badge-primary badge-sm">默认</span>
                    </div>
                    <p class="text-sm text-base-content/60">{{ address.detail }}</p>
                  </div>
                </label>
                <button class="btn btn-outline btn-sm w-full" @click="$router.push('/addresses')">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                  </svg>
                  管理收货地址
                </button>
              </div>
            </div>
          </div>

          <!-- 商品列表 -->
          <div class="card bg-base-100 shadow-sm">
            <div class="card-body">
              <h2 class="card-title mb-4">商品信息</h2>
              <div v-if="checkoutItems.length === 0" class="text-center py-8">
                <p class="text-base-content/60">没有选中商品</p>
              </div>
              <div v-else class="space-y-4">
                <div
                  v-for="item in checkoutItems"
                  :key="item.itemId || item.skuId"
                  class="flex items-center gap-4 p-4 border border-base-300 rounded-lg"
                >
                  <!-- 商品图片 -->
                  <div class="w-20 h-20 rounded-lg overflow-hidden bg-base-200 flex-shrink-0">
                    <img
                      v-if="item.skuImageUrl || item.productImageUrl || item.coverImageUrl"
                      :src="item.skuImageUrl || item.productImageUrl || item.coverImageUrl"
                      :alt="item.productName || item.name"
                      class="w-full h-full object-cover"
                    />
                    <div v-else class="w-full h-full flex items-center justify-center">
                      <svg class="w-12 h-12 text-base-content/30" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
                      </svg>
                    </div>
                  </div>

                  <!-- 商品信息 -->
                  <div class="flex-1 min-w-0">
                    <h3 class="font-semibold text-lg mb-1 truncate">
                      {{ item.productName || item.name }}
                    </h3>
                    <p v-if="item.spec" class="text-sm text-base-content/60 mb-2">
                      规格：{{ formatSpec(item.spec) }}
                    </p>
                    <p v-if="item.shopName" class="text-sm text-base-content/60">
                      店铺：{{ item.shopName }}
                    </p>
                  </div>

                  <!-- 价格和数量 -->
                  <div class="text-right">
                    <div class="text-xl font-bold mb-2" style="color: rgb(255, 202, 57);">
                      ¥{{ (item.price || 0).toFixed(2) }}
                    </div>
                    <div class="text-sm text-base-content/60">
                      x{{ item.num || item.quantity || 1 }}
                    </div>
                    <div class="text-sm font-semibold mt-2" style="color: rgb(255, 202, 57);">
                      小计：¥{{ ((item.price || 0) * (item.num || item.quantity || 1)).toFixed(2) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 订单汇总 -->
          <div class="card bg-base-100 shadow-sm">
            <div class="card-body">
              <div class="flex justify-between items-center text-lg">
                <span>订单总额：</span>
                <span class="text-2xl font-bold" style="color: rgb(255, 202, 57);">
                  ¥{{ totalPrice.toFixed(2) }}
                </span>
              </div>
            </div>
          </div>

          <!-- 提交订单按钮 -->
          <div class="flex justify-end gap-4">
            <button class="btn btn-ghost" @click="$router.back()">返回</button>
            <button
              class="btn btn-primary btn-lg"
              :class="{ 'btn-disabled': !canSubmit }"
              @click="handleSubmitOrder"
            >
              提交订单
            </button>
          </div>
        </div>

        <!-- 支付倒计时页面 -->
        <div v-else class="space-y-6">
          <div class="card bg-base-100 shadow-lg">
            <div class="card-body">
              <h2 class="card-title mb-4">订单已创建，请尽快支付</h2>
              
              <!-- 倒计时 -->
              <div class="text-center py-8">
                <div class="text-6xl font-bold text-primary mb-4">
                  {{ formatCountdown(countdown) }}
                </div>
                <p class="text-base-content/60">剩余支付时间</p>
              </div>

              <!-- 订单信息 -->
              <div class="space-y-4 mt-6">
                <div class="flex justify-between">
                  <span class="text-base-content/60">订单号：</span>
                  <span class="font-semibold">{{ batchOrder.batchOrderId }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-base-content/60">订单总额：</span>
                  <span class="text-xl font-bold" style="color: rgb(255, 202, 57);">
                    ¥{{ (batchOrder.totalAmount || 0).toFixed(2) }}
                  </span>
                </div>
              </div>

              <!-- 收货地址 -->
              <div v-if="batchOrder.address" class="mt-6 p-4 bg-base-200 rounded-lg">
                <h3 class="font-semibold mb-2">收货地址</h3>
                <p class="text-sm">
                  {{ batchOrder.address.receiverName }} {{ batchOrder.address.receiverPhone }}
                </p>
                <p class="text-sm text-base-content/60">{{ batchOrder.address.detail }}</p>
              </div>

              <!-- 商品列表 -->
              <div v-if="batchOrder.orders" class="mt-6 space-y-4">
                <div
                  v-for="order in batchOrder.orders"
                  :key="order.orderId"
                  class="p-4 bg-base-200 rounded-lg"
                >
                  <h4 class="font-semibold mb-2">{{ order.shopName }}</h4>
                  <div
                    v-for="orderItem in order.orderItems"
                    :key="orderItem.itemId"
                    class="flex items-center gap-3 mb-2"
                  >
                    <div class="w-16 h-16 rounded overflow-hidden bg-base-300 flex-shrink-0">
                      <img
                        v-if="orderItem.skuImageUrl || orderItem.productImageUrl"
                        :src="orderItem.skuImageUrl || orderItem.productImageUrl"
                        :alt="orderItem.productName"
                        class="w-full h-full object-cover"
                      />
                    </div>
                    <div class="flex-1">
                      <p class="font-semibold text-sm">{{ orderItem.productName }}</p>
                      <p v-if="orderItem.spec" class="text-xs text-base-content/60">
                        {{ formatSpec(orderItem.spec) }}
                      </p>
                      <p class="text-xs text-base-content/60">
                        ¥{{ (orderItem.priceSnapshot || 0).toFixed(2) }} x {{ orderItem.num }}
                      </p>
                    </div>
                  </div>
                  <div class="text-right mt-2">
                    <span class="text-sm text-base-content/60">小计：</span>
                    <span class="font-semibold" style="color: rgb(255, 202, 57);">
                      ¥{{ (order.total || 0).toFixed(2) }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 支付按钮 -->
              <div class="flex justify-end gap-4 mt-6">
                <button
                  class="btn btn-ghost"
                  :class="{ 'btn-disabled': paying }"
                  @click="handleCancelOrder"
                >
                  取消订单
                </button>
                <button
                  class="btn btn-primary btn-lg"
                  :class="{ 'btn-disabled': countdown <= 0 || paying }"
                  @click="handlePayOrder"
                >
                  <span v-if="paying" class="loading loading-spinner loading-sm"></span>
                  <span v-else>确认支付</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />

    <!-- 确认提交订单弹窗 -->
    <div v-if="showConfirmModal" class="modal modal-open">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">确认订单信息</h3>
        <div class="space-y-4">
          <div>
            <h4 class="font-semibold mb-2">商品信息：</h4>
            <div class="space-y-2 max-h-60 overflow-y-auto">
              <div
                v-for="item in checkoutItems"
                :key="item.itemId || item.skuId"
                class="p-2 bg-base-200 rounded text-sm"
              >
                <p class="font-semibold">{{ item.productName || item.name }}</p>
                <p v-if="item.spec" class="text-base-content/60">
                  规格：{{ formatSpec(item.spec) }}
                </p>
                <p class="text-base-content/60">
                  数量：{{ item.num || item.quantity || 1 }} | 
                  单价：¥{{ (item.price || 0).toFixed(2) }}
                </p>
              </div>
            </div>
          </div>
          <div>
            <h4 class="font-semibold mb-2">收货地址：</h4>
            <div v-if="selectedAddress" class="p-2 bg-base-200 rounded text-sm">
              <p>{{ selectedAddress.receiverName }} {{ selectedAddress.receiverPhone }}</p>
              <p class="text-base-content/60">{{ selectedAddress.detail }}</p>
            </div>
          </div>
          <div class="flex justify-between items-center pt-4 border-t">
            <span class="font-semibold">订单总额：</span>
            <span class="text-xl font-bold" style="color: rgb(255, 202, 57);">
              ¥{{ totalPrice.toFixed(2) }}
            </span>
          </div>
        </div>
        <div class="modal-action">
          <button class="btn btn-ghost" @click="showConfirmModal = false">取消</button>
          <button
            class="btn btn-primary"
            :class="{ 'btn-disabled': submitting }"
            @click="confirmSubmitOrder"
          >
            <span v-if="submitting" class="loading loading-spinner loading-sm"></span>
            <span v-else>确认提交</span>
          </button>
        </div>
      </div>
      <div class="modal-backdrop" @click="showConfirmModal = false"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import { apiGetAddressList } from '../api/address'
import { apiGetCartItems } from '../api/cart'
import { apiCreateOrder, apiPayOrder, apiCancelOrder, apiGetBatchOrder } from '../api/order'
import type { AddressVO } from '../api/generated/models/address-vo'
import type { CartItemVO } from '../api/generated/models/cart-item-vo'
import type { BatchOrderVO } from '../api/generated/models/batch-order-vo'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const error = ref<string | null>(null)
const addresses = ref<AddressVO[]>([])
const selectedAddressId = ref<number | null>(null)
const checkoutItems = ref<(CartItemVO | any)[]>([])
const batchOrder = ref<BatchOrderVO | null>(null)
const countdown = ref(0)
const showConfirmModal = ref(false)
const submitting = ref(false)
const paying = ref(false)
let countdownTimer: number | null = null

// 从路由参数获取数据
const cartItemIds = computed(() => {
  const ids = route.query.cartItemIds
  if (Array.isArray(ids)) {
    return ids.map(id => Number(id))
  } else if (ids) {
    return [Number(ids)]
  }
  return null
})

// 立即购买数据（从路由参数）
const buyNowData = computed(() => {
  const data = route.query.buyNow
  if (data && typeof data === 'string') {
    try {
      return JSON.parse(decodeURIComponent(data))
    } catch {
      return null
    }
  }
  return null
})

const selectedAddress = computed(() => {
  return addresses.value.find(addr => addr.addressId === selectedAddressId.value)
})

const totalPrice = computed(() => {
  return checkoutItems.value.reduce((sum, item) => {
    return sum + ((item.price || 0) * (item.num || item.quantity || 1))
  }, 0)
})

const canSubmit = computed(() => {
  return selectedAddressId.value !== null && checkoutItems.value.length > 0
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

function formatCountdown(seconds: number): string {
  if (seconds <= 0) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

async function loadData() {
  loading.value = true
  error.value = null

  try {
    // 加载地址列表
    addresses.value = await apiGetAddressList()
    if (addresses.value.length > 0) {
      const defaultAddress = addresses.value.find(addr => addr.isDefault)
      selectedAddressId.value = defaultAddress?.addressId || addresses.value[0].addressId || null
    }

    // 加载商品数据
    if (buyNowData.value) {
      // 立即购买模式
      checkoutItems.value = [buyNowData.value]
    } else if (cartItemIds.value && cartItemIds.value.length > 0) {
      // 从购物车选择
      const allCartItems = await apiGetCartItems()
      checkoutItems.value = allCartItems.filter(item => 
        item.itemId && cartItemIds.value!.includes(item.itemId)
      )
    } else {
      // 如果没有参数，尝试从购物车获取所有选中的商品
      const allCartItems = await apiGetCartItems()
      checkoutItems.value = allCartItems.filter(item => item.available)
    }

    if (checkoutItems.value.length === 0) {
      error.value = '没有可结算的商品'
    }
  } catch (e: any) {
    if (e.message && e.message.includes('未登录')) {
      router.push('/login')
    } else {
      error.value = e.message || '加载数据失败'
      console.error('Failed to load checkout data:', e)
    }
  } finally {
    loading.value = false
  }
}

function handleSubmitOrder() {
  if (!canSubmit.value) {
    pushToast('请选择收货地址', 'error')
    return
  }
  showConfirmModal.value = true
}

async function confirmSubmitOrder() {
  if (!selectedAddressId.value) {
    pushToast('请选择收货地址', 'error')
    return
  }

  submitting.value = true
  try {
    // 获取有itemId的商品（来自购物车）
    const cartItemIdsToSubmit = checkoutItems.value
      .map(item => item.itemId)
      .filter((id): id is number => id !== undefined && id !== null)

    // 如果立即购买的商品不在购物车中，需要先添加到购物车
    const buyNowItems = checkoutItems.value.filter(item => !item.itemId)
    if (buyNowItems.length > 0) {
      // 立即购买的商品需要先添加到购物车
      const { apiAddCartItem } = await import('../api/cart')
      for (const item of buyNowItems) {
        if (item.skuId) {
          await apiAddCartItem(item.skuId, item.quantity || 1)
        }
      }
      // 重新获取购物车项ID
      const { apiGetCartItems } = await import('../api/cart')
      const allCartItems = await apiGetCartItems()
      // 找到刚添加的商品
      for (const item of buyNowItems) {
        if (item.skuId) {
          const cartItem = allCartItems.find(ci => ci.skuId === item.skuId)
          if (cartItem && cartItem.itemId) {
            cartItemIdsToSubmit.push(cartItem.itemId)
          }
        }
      }
    }

    const batchOrderResult = await apiCreateOrder({
      cartItemIds: cartItemIdsToSubmit.length > 0 ? cartItemIdsToSubmit : undefined,
      addressId: selectedAddressId.value
    })

    batchOrder.value = batchOrderResult
    showConfirmModal.value = false

    // 开始倒计时
    if (batchOrderResult.remainingSeconds) {
      countdown.value = batchOrderResult.remainingSeconds
      startCountdown()
    } else {
      // 如果没有剩余时间，计算过期时间
      if (batchOrderResult.expireTime) {
        const expireTime = new Date(batchOrderResult.expireTime).getTime()
        const now = Date.now()
        countdown.value = Math.max(0, Math.floor((expireTime - now) / 1000))
        startCountdown()
      }
    }

    pushToast('订单创建成功', 'success')
  } catch (e: any) {
    pushToast(`创建订单失败：${e.message || e}`, 'error')
  } finally {
    submitting.value = false
  }
}

function startCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  countdownTimer = window.setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
      pushToast('订单已过期', 'error')
    }
  }, 1000)
}

async function handlePayOrder() {
  if (!batchOrder.value || !batchOrder.value.batchOrderId) {
    pushToast('订单信息错误', 'error')
    return
  }

  if (countdown.value <= 0) {
    pushToast('订单已过期，无法支付', 'error')
    return
  }

  paying.value = true
  try {
    const result = await apiPayOrder(batchOrder.value.batchOrderId)
    // 跳转到支付成功页面
    router.push({
      path: '/payment-success',
      query: {
        batchOrderId: batchOrder.value.batchOrderId.toString()
      }
    })
  } catch (e: any) {
    pushToast(`支付失败：${e.message || e}`, 'error')
  } finally {
    paying.value = false
  }
}

async function handleCancelOrder() {
  if (!batchOrder.value || !batchOrder.value.batchOrderId) {
    return
  }

  if (!confirm('确定要取消订单吗？')) {
    return
  }

  try {
    await apiCancelOrder(batchOrder.value.batchOrderId)
    pushToast('订单已取消', 'success')
    router.push('/cart')
  } catch (e: any) {
    pushToast(`取消订单失败：${e.message || e}`, 'error')
  }
}

onMounted(() => {
  loadData()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
</style>

