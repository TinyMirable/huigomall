<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-4xl">
        <!-- 加载状态 -->
        <div v-if="loading" class="text-center py-12">
          <span class="loading loading-spinner loading-lg"></span>
          <p class="mt-4 text-base-content/60">加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="alert alert-error">
          <span>{{ error }}</span>
          <button class="btn btn-sm" @click="loadOrder">重试</button>
        </div>

        <!-- 支付成功内容 -->
        <div v-else-if="batchOrder" class="space-y-6">
          <!-- 成功提示 -->
          <div class="card bg-base-100 shadow-lg">
            <div class="card-body text-center py-12">
              <div class="mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h1 class="text-3xl font-bold text-success mb-2">支付成功！</h1>
              <p class="text-base-content/60">您的订单已成功支付，商品将尽快送达</p>
            </div>
          </div>

          <!-- 订单信息 -->
          <div class="card bg-base-100 shadow-sm">
            <div class="card-body">
              <h2 class="card-title mb-4">订单信息</h2>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-base-content/60">订单号：</span>
                  <span class="font-semibold">{{ batchOrder.batchOrderId }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-base-content/60">支付时间：</span>
                  <span class="font-semibold">{{ formatTime(batchOrder.payTime) }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-base-content/60">订单总额：</span>
                  <span class="text-xl font-bold" style="color: rgb(255, 202, 57);">
                    ¥{{ (batchOrder.totalAmount || 0).toFixed(2) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 收货地址 -->
          <div v-if="batchOrder.address" class="card bg-base-100 shadow-sm">
            <div class="card-body">
              <h2 class="card-title mb-4">收货地址</h2>
              <div class="p-4 bg-base-200 rounded-lg">
                <div class="flex items-center gap-2 mb-2">
                  <span class="font-semibold">{{ batchOrder.address.receiverName }}</span>
                  <span class="text-base-content/60">{{ batchOrder.address.receiverPhone }}</span>
                </div>
                <p class="text-base-content/60">{{ batchOrder.address.detail }}</p>
              </div>
            </div>
          </div>

          <!-- 商品列表 -->
          <div v-if="batchOrder.orders && batchOrder.orders.length > 0" class="card bg-base-100 shadow-sm">
            <div class="card-body">
              <h2 class="card-title mb-4">商品清单</h2>
              <div class="space-y-6">
                <div
                  v-for="order in batchOrder.orders"
                  :key="order.orderId"
                  class="p-4 bg-base-200 rounded-lg"
                >
                  <div class="flex items-center justify-between mb-4">
                    <h3 class="font-semibold text-lg">{{ order.shopName }}</h3>
                    <span class="text-sm text-base-content/60">
                      订单号：{{ order.orderId }}
                    </span>
                  </div>
                  <div class="space-y-3">
                    <div
                      v-for="orderItem in order.orderItems"
                      :key="orderItem.itemId"
                      class="flex items-center gap-4 p-3 bg-base-100 rounded"
                    >
                      <!-- 商品图片 -->
                      <div class="w-20 h-20 rounded-lg overflow-hidden bg-base-200 flex-shrink-0">
                        <img
                          v-if="orderItem.skuImageUrl || orderItem.productImageUrl"
                          :src="orderItem.skuImageUrl || orderItem.productImageUrl"
                          :alt="orderItem.productName"
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
                        <h4 class="font-semibold mb-1">{{ orderItem.productName }}</h4>
                        <p v-if="orderItem.spec" class="text-sm text-base-content/60 mb-1">
                          规格：{{ formatSpec(orderItem.spec) }}
                        </p>
                        <p class="text-sm text-base-content/60">
                          数量：{{ orderItem.num }}
                        </p>
                      </div>

                      <!-- 价格 -->
                      <div class="text-right">
                        <div class="text-lg font-bold mb-1" style="color: rgb(255, 202, 57);">
                          ¥{{ (orderItem.priceSnapshot || 0).toFixed(2) }}
                        </div>
                        <div class="text-sm text-base-content/60">
                          小计：¥{{ (orderItem.subtotal || 0).toFixed(2) }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="flex justify-end mt-4 pt-4 border-t border-base-300">
                    <div class="text-right">
                      <span class="text-sm text-base-content/60">店铺小计：</span>
                      <span class="text-xl font-bold ml-2" style="color: rgb(255, 202, 57);">
                        ¥{{ (order.total || 0).toFixed(2) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-center gap-4">
            <button class="btn btn-ghost" @click="$router.push('/')">继续购物</button>
            <button class="btn btn-primary" @click="$router.push('/orders')">查看订单</button>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import { apiGetBatchOrder } from '../api/order'
import type { BatchOrderVO } from '../api/generated/models/batch-order-vo'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const error = ref<string | null>(null)
const batchOrder = ref<BatchOrderVO | null>(null)

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

function formatTime(timeStr?: string): string {
  if (!timeStr) return '-'
  try {
    const date = new Date(timeStr)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch {
    return timeStr
  }
}

async function loadOrder() {
  const batchOrderId = route.query.batchOrderId
  if (!batchOrderId || isNaN(Number(batchOrderId))) {
    error.value = '订单ID无效'
    return
  }

  loading.value = true
  error.value = null
  try {
    batchOrder.value = await apiGetBatchOrder(Number(batchOrderId))
  } catch (e: any) {
    if (e.message && e.message.includes('未登录')) {
      router.push('/login')
    } else {
      error.value = e.message || '加载订单信息失败'
      console.error('Failed to load order:', e)
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
</style>





