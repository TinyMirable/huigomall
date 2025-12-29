<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-6xl">
        <h1 class="text-3xl font-bold mb-6">我的订单</h1>

        <!-- 加载状态 -->
        <div v-if="loading" class="text-center py-12">
          <span class="loading loading-spinner loading-lg"></span>
          <p class="mt-4 text-base-content/60">加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="alert alert-error">
          <span>{{ error }}</span>
          <button class="btn btn-sm" @click="loadOrders">重试</button>
        </div>

        <!-- 订单列表 -->
        <div v-else-if="orders.length > 0" class="space-y-6">
          <div
            v-for="batchOrder in orders"
            :key="batchOrder.batchOrderId"
            class="card bg-base-100 shadow-md"
          >
            <div class="card-body">
              <!-- 订单头部 -->
              <div class="flex items-center justify-between mb-4 pb-4 border-b border-base-300">
                <div class="flex items-center gap-4">
                  <div>
                    <p class="text-sm text-base-content/60">订单号</p>
                    <p class="font-semibold">{{ batchOrder.batchOrderId }}</p>
                  </div>
                  <div>
                    <p class="text-sm text-base-content/60">下单时间</p>
                    <p class="text-sm">{{ formatDate(batchOrder.createTime) }}</p>
                  </div>
                </div>
                <div class="text-right">
                  <div class="badge badge-lg" :class="getStatusBadgeClass(batchOrder.status)">
                    {{ getStatusText(batchOrder.status) }}
                  </div>
                  <p class="text-lg font-bold mt-2" style="color: rgb(255, 202, 57);">
                    总计：¥{{ batchOrder.totalAmount?.toFixed(2) || '0.00' }}
                  </p>
                </div>
              </div>

              <!-- 收货地址 -->
              <div v-if="batchOrder.address" class="mb-4 p-3 bg-base-200 rounded-lg">
                <div class="flex items-center gap-2 mb-1">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-base-content/60" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <span class="font-semibold">{{ batchOrder.address.receiverName }}</span>
                  <span class="text-base-content/60">{{ batchOrder.address.receiverPhone }}</span>
                </div>
                <p class="text-sm text-base-content/60 ml-7">{{ batchOrder.address.detail }}</p>
              </div>

              <!-- 小订单列表 -->
              <div class="space-y-4">
                <div
                  v-for="order in batchOrder.orders"
                  :key="order.orderId"
                  class="border border-base-300 rounded-lg p-4"
                >
                  <!-- 店铺信息 -->
                  <div class="flex items-center gap-2 mb-3 pb-3 border-b border-base-300">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                    </svg>
                    <span class="font-semibold">{{ order.shopName || '店铺' }}</span>
                    <div class="badge badge-sm" :class="getStatusBadgeClass(order.status)">
                      {{ getStatusText(order.status) }}
                    </div>
                  </div>

                  <!-- 订单项列表 -->
                  <div class="space-y-3">
                    <div
                      v-for="item in order.orderItems"
                      :key="item.itemId"
                      class="flex items-center gap-4"
                    >
                      <!-- 商品图片 -->
                      <div class="w-20 h-20 rounded-lg overflow-hidden bg-base-200 flex-shrink-0">
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
                      <div class="flex-1 min-w-0">
                        <h3 class="font-semibold mb-1 line-clamp-2">{{ item.productName }}</h3>
                        <p v-if="item.spec" class="text-sm text-base-content/60 mb-1">
                          规格：{{ formatSpec(item.spec) }}
                        </p>
                        <div class="flex items-center gap-4 text-sm">
                          <span class="text-base-content/60">数量：{{ item.num }}</span>
                          <span class="font-semibold" style="color: rgb(255, 202, 57);">
                            单价：¥{{ item.priceSnapshot?.toFixed(2) || '0.00' }}
                          </span>
                        </div>
                      </div>

                      <!-- 小计 -->
                      <div class="text-right">
                        <p class="text-lg font-bold" style="color: rgb(255, 202, 57);">
                          ¥{{ item.subtotal?.toFixed(2) || '0.00' }}
                        </p>
                      </div>
                    </div>
                  </div>

                  <!-- 小订单总金额 -->
                  <div class="flex justify-end mt-3 pt-3 border-t border-base-300">
                    <div class="text-right">
                      <p class="text-sm text-base-content/60">小订单合计</p>
                      <p class="text-xl font-bold" style="color: rgb(255, 202, 57);">
                        ¥{{ order.total?.toFixed(2) || '0.00' }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="flex justify-end gap-3 mt-4 pt-4 border-t border-base-300">
                <button
                  v-if="batchOrder.status === 0"
                  class="btn btn-error btn-sm"
                  @click="handleCancelOrder(batchOrder.batchOrderId!)"
                  :disabled="cancelling"
                >
                  <span v-if="cancelling" class="loading loading-spinner loading-sm"></span>
                  <span v-else>取消订单</span>
                </button>
                <button
                  v-if="batchOrder.status === 0"
                  class="btn btn-primary btn-sm"
                  @click="handlePayOrder(batchOrder.batchOrderId!)"
                >
                  立即支付
                </button>
                <button
                  v-if="batchOrder.status === 2"
                  class="btn btn-success btn-sm"
                  @click="handleConfirmReceive(batchOrder.batchOrderId!)"
                >
                  确认收货
                </button>
                <button
                  class="btn btn-outline btn-sm"
                  @click="viewOrderDetail(batchOrder.batchOrderId!)"
                >
                  查看详情
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="text-center py-12">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-base-content/30 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p class="text-lg text-base-content/60 mb-4">暂无订单</p>
          <button class="btn btn-primary" @click="$router.push('/')">去购物</button>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import { apiGetBatchOrderList, apiCancelOrder } from '../api/order'
import type { BatchOrderVO } from '../api/generated/models'

const router = useRouter()

const loading = ref(false)
const error = ref<string | null>(null)
const orders = ref<BatchOrderVO[]>([])
const cancelling = ref(false)

function formatDate(dateStr?: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
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

function getStatusText(status?: number): string {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已发货',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return statusMap[status ?? -1] || '未知状态'
}

function getStatusBadgeClass(status?: number): string {
  const classMap: Record<number, string> = {
    0: 'badge-warning',
    1: 'badge-info',
    2: 'badge-primary',
    3: 'badge-success',
    4: 'badge-error',
    5: 'badge-warning',
    6: 'badge-error'
  }
  return classMap[status ?? -1] || 'badge-ghost'
}

async function loadOrders() {
  loading.value = true
  error.value = null
  try {
    orders.value = await apiGetBatchOrderList()
    // 排序：未支付的订单在前面，已支付的放后面；同组内按创建时间倒序排列
    orders.value.sort((a, b) => {
      const statusA = a.status ?? -1
      const statusB = b.status ?? -1
      
      // 未支付订单（status === 0）优先显示
      if (statusA === 0 && statusB !== 0) {
        return -1 // a 排在前面
      }
      if (statusA !== 0 && statusB === 0) {
        return 1 // b 排在前面
      }
      
      // 同组内按创建时间倒序排列
      const timeA = a.createTime ? new Date(a.createTime).getTime() : 0
      const timeB = b.createTime ? new Date(b.createTime).getTime() : 0
      return timeB - timeA
    })
  } catch (e: any) {
    if (e.message && e.message.includes('未登录')) {
      error.value = '请先登录'
      router.push('/login')
    } else {
      error.value = e.message || '加载订单列表失败'
    }
    console.error('Failed to load orders:', e)
  } finally {
    loading.value = false
  }
}

async function handleCancelOrder(batchOrderId: number) {
  if (!confirm('确定要取消这个订单吗？')) {
    return
  }

  cancelling.value = true
  try {
    await apiCancelOrder(batchOrderId)
    pushToast('订单已取消', 'success')
    await loadOrders()
  } catch (e: any) {
    pushToast(e.message || '取消订单失败', 'error')
  } finally {
    cancelling.value = false
  }
}

function handlePayOrder(batchOrderId: number) {
  router.push({
    path: '/checkout',
    query: {
      batchOrderId: batchOrderId.toString()
    }
  })
}

function handleConfirmReceive(batchOrderId: number) {
  // TODO: 实现确认收货功能
  pushToast('确认收货功能开发中', 'info')
}

function viewOrderDetail(batchOrderId: number) {
  // TODO: 跳转到订单详情页面
  pushToast('订单详情功能开发中', 'info')
  // router.push(`/order/${batchOrderId}`)
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

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
</style>

