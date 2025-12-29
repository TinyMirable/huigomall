<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">订单管理</h2>

    <!-- 筛选栏 -->
    <div class="card bg-base-100 shadow-md mb-6">
      <div class="card-body p-4">
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
          <div class="form-control">
            <label class="label">
              <span class="label-text">店铺ID</span>
            </label>
            <input
              v-model.number="filters.shopId"
              type="number"
              placeholder="店铺ID"
              class="input input-bordered"
            />
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">商家ID</span>
            </label>
            <input
              v-model.number="filters.merchantId"
              type="number"
              placeholder="商家ID"
              class="input input-bordered"
            />
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">订单状态</span>
            </label>
            <select v-model="filters.status" class="select select-bordered" @change="handleFilterChange">
              <option :value="undefined">全部状态</option>
              <option :value="0">待支付</option>
              <option :value="1">已支付</option>
              <option :value="2">已发货</option>
              <option :value="3">已完成</option>
              <option :value="4">已取消</option>
              <option :value="5">退款中</option>
              <option :value="6">已退款</option>
            </select>
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">关键词</span>
            </label>
            <input
              v-model="filters.keyword"
              type="text"
              placeholder="订单ID、用户名"
              class="input input-bordered"
              @keyup.enter="handleFilterChange"
            />
          </div>
          <div class="form-control flex items-end">
            <button class="btn btn-primary" @click="handleFilterChange">搜索</button>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4 mt-4">
          <div class="form-control">
            <label class="label">
              <span class="label-text">开始时间</span>
            </label>
            <input
              v-model="filters.startTime"
              type="datetime-local"
              class="input input-bordered"
              @change="handleFilterChange"
            />
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">结束时间</span>
            </label>
            <input
              v-model="filters.endTime"
              type="datetime-local"
              class="input input-bordered"
              @change="handleFilterChange"
            />
          </div>
        </div>
      </div>
    </div>

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
    <div v-else-if="orderList.orders && orderList.orders.length > 0" class="space-y-4">
      <div
        v-for="order in orderList.orders"
        :key="order.orderId"
        class="card bg-base-100 shadow-md"
      >
        <div class="card-body">
          <div class="flex items-center justify-between mb-4 pb-4 border-b border-base-300">
            <div class="flex items-center gap-4">
              <div>
                <p class="text-sm text-base-content/60">订单号</p>
                <p class="font-semibold">{{ order.orderId }}</p>
              </div>
              <div>
                <p class="text-sm text-base-content/60">店铺</p>
                <p class="font-semibold">{{ order.shopName }}</p>
              </div>
              <div>
                <p class="text-sm text-base-content/60">买家</p>
                <p class="text-sm">{{ order.userName || '用户' }}</p>
              </div>
              <div>
                <p class="text-sm text-base-content/60">下单时间</p>
                <p class="text-sm">{{ formatDate(order.createTime) }}</p>
              </div>
            </div>
            <div class="text-right">
              <div class="badge badge-lg" :class="getStatusBadgeClass(order.status)">
                {{ order.statusText || getStatusText(order.status) }}
              </div>
              <p class="text-lg font-bold mt-2" style="color: rgb(255, 202, 57);">
                总计：¥{{ order.total?.toFixed(2) || '0.00' }}
              </p>
            </div>
          </div>

          <!-- 订单项 -->
          <div class="space-y-2 mb-4">
            <div
              v-for="item in order.orderItems"
              :key="item.itemId"
              class="flex items-center gap-4 p-3 bg-base-200 rounded-lg"
            >
              <div class="w-16 h-16 rounded-lg overflow-hidden bg-base-300 flex-shrink-0">
                <img
                  v-if="item.productImageUrl"
                  :src="item.productImageUrl"
                  :alt="item.productName"
                  class="w-full h-full object-cover"
                />
              </div>
              <div class="flex-1">
                <p class="font-semibold">{{ item.productName }}</p>
                <p v-if="item.spec" class="text-sm text-base-content/60">
                  规格：{{ formatSpec(item.spec) }}
                </p>
                <p class="text-sm">
                  单价：¥{{ item.priceSnapshot?.toFixed(2) || '0.00' }} × {{ item.num }}
                </p>
              </div>
              <p class="font-semibold">¥{{ item.subtotal?.toFixed(2) || '0.00' }}</p>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-end gap-2 pt-4 border-t border-base-300">
            <button
              v-if="order.status !== 4"
              class="btn btn-error"
              @click="cancelOrder(order.orderId!)"
              :disabled="cancelling"
            >
              <span v-if="cancelling" class="loading loading-spinner loading-sm"></span>
              <span v-else>取消订单</span>
            </button>
            <button class="btn btn-ghost" @click="viewOrderDetail(order.orderId!)">
              查看详情
            </button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center items-center gap-2 mt-6">
        <button
          class="btn btn-sm"
          :disabled="orderList.page === 1"
          @click="changePage(orderList.page! - 1)"
        >
          上一页
        </button>
        <span class="text-base-content/60">
          第 {{ orderList.page }} / {{ totalPages }} 页，共 {{ orderList.total }} 条
        </span>
        <button
          class="btn btn-sm"
          :disabled="orderList.page === totalPages"
          @click="changePage(orderList.page! + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <p class="text-lg text-base-content/60">暂无订单</p>
    </div>

    <!-- 订单详情弹窗（复用MerchantOrders的样式） -->
    <dialog ref="orderDetailModal" class="modal">
      <div class="modal-box max-w-4xl">
        <h3 class="font-bold text-lg mb-4">订单详情</h3>
        <div v-if="selectedOrder" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-base-content/60">订单号</p>
              <p class="font-semibold">{{ selectedOrder.orderId }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">订单状态</p>
              <div class="badge badge-lg mt-1" :class="getStatusBadgeClass(selectedOrder.status)">
                {{ selectedOrder.statusText || getStatusText(selectedOrder.status) }}
              </div>
            </div>
            <div>
              <p class="text-sm text-base-content/60">下单时间</p>
              <p class="text-sm">{{ formatDate(selectedOrder.createTime) }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">订单金额</p>
              <p class="font-semibold text-lg" style="color: rgb(255, 202, 57);">
                ¥{{ selectedOrder.total?.toFixed(2) || '0.00' }}
              </p>
            </div>
          </div>
          <div v-if="selectedOrder.address" class="p-3 bg-base-200 rounded-lg">
            <p class="text-sm text-base-content/60 mb-1">收货地址</p>
            <p class="font-semibold">{{ selectedOrder.address.receiverName }} {{ selectedOrder.address.receiverPhone }}</p>
            <p class="text-sm">{{ selectedOrder.address.detail }}</p>
          </div>
          <div>
            <p class="text-sm text-base-content/60 mb-2">订单商品</p>
            <div class="space-y-2">
              <div
                v-for="item in selectedOrder.orderItems"
                :key="item.itemId"
                class="flex items-center gap-4 p-3 bg-base-200 rounded-lg"
              >
                <div class="w-16 h-16 rounded-lg overflow-hidden bg-base-300 flex-shrink-0">
                  <img
                    v-if="item.productImageUrl"
                    :src="item.productImageUrl"
                    :alt="item.productName"
                    class="w-full h-full object-cover"
                  />
                </div>
                <div class="flex-1">
                  <p class="font-semibold">{{ item.productName }}</p>
                  <p v-if="item.spec" class="text-sm text-base-content/60">
                    规格：{{ formatSpec(item.spec) }}
                  </p>
                  <p class="text-sm">
                    单价：¥{{ item.priceSnapshot?.toFixed(2) || '0.00' }} × {{ item.num }}
                  </p>
                </div>
                <p class="font-semibold">¥{{ item.subtotal?.toFixed(2) || '0.00' }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-action">
          <button class="btn btn-ghost" @click="closeOrderDetailModal">关闭</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeOrderDetailModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { apiGetAdminOrders, apiGetAdminOrderDetail, apiCancelAdminOrder } from '../api/admin'
import type { MerchantOrderListVO, MerchantOrderVO } from '../api/generated-admin/models'

const loading = ref(false)
const error = ref<string | null>(null)
const cancelling = ref(false)
const orderList = ref<MerchantOrderListVO>({ orders: [], total: 0, page: 1, size: 20 })
const selectedOrder = ref<MerchantOrderVO | null>(null)

const filters = ref({
  shopId: undefined as number | undefined,
  merchantId: undefined as number | undefined,
  status: undefined as number | undefined,
  startTime: undefined as string | undefined,
  endTime: undefined as string | undefined,
  keyword: ''
})

const orderDetailModal = ref<HTMLDialogElement | null>(null)

const pageSize = 20
const totalPages = computed(() => {
  if (!orderList.value.total || !orderList.value.size) return 1
  return Math.ceil(orderList.value.total / orderList.value.size)
})

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

function formatSpec(specJson?: string): string {
  if (!specJson) return ''
  try {
    const spec = JSON.parse(specJson)
    return Object.entries(spec)
      .map(([key, value]) => `${key}: ${value}`)
      .join(', ')
  } catch {
    return specJson
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
    6: 'badge-ghost'
  }
  return classMap[status ?? -1] || 'badge-ghost'
}

function handleFilterChange() {
  orderList.value.page = 1
  loadOrders()
}

function changePage(page: number) {
  orderList.value.page = page
  loadOrders()
}

async function loadOrders() {
  loading.value = true
  error.value = null
  try {
    const params: any = {
      page: orderList.value.page,
      size: pageSize
    }
    if (filters.value.shopId !== undefined) {
      params.shopId = filters.value.shopId
    }
    if (filters.value.merchantId !== undefined) {
      params.merchantId = filters.value.merchantId
    }
    if (filters.value.status !== undefined) {
      params.status = filters.value.status
    }
    if (filters.value.startTime) {
      params.startTime = new Date(filters.value.startTime).toISOString()
    }
    if (filters.value.endTime) {
      params.endTime = new Date(filters.value.endTime).toISOString()
    }
    if (filters.value.keyword) {
      params.keyword = filters.value.keyword
    }
    orderList.value = await apiGetAdminOrders(params)
  } catch (e: any) {
    error.value = e.message || '加载订单列表失败'
    console.error('Failed to load orders:', e)
  } finally {
    loading.value = false
  }
}

async function viewOrderDetail(orderId: number) {
  try {
    selectedOrder.value = await apiGetAdminOrderDetail(orderId)
    orderDetailModal.value?.showModal()
  } catch (e: any) {
    pushToast(`获取订单详情失败：${e.message || e}`, 'error')
  }
}

function closeOrderDetailModal() {
  orderDetailModal.value?.close()
  selectedOrder.value = null
}

async function cancelOrder(orderId: number) {
  const reason = prompt('请输入取消原因（必填）')
  if (!reason) {
    pushToast('取消原因不能为空', 'error')
    return
  }
  if (!confirm('确定要取消该订单吗？取消后将恢复商品库存。')) {
    return
  }
  cancelling.value = true
  try {
    await apiCancelAdminOrder(orderId, reason)
    pushToast('订单已取消', 'success')
    await loadOrders()
  } catch (e: any) {
    pushToast(`取消订单失败：${e.message || e}`, 'error')
  } finally {
    cancelling.value = false
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

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
</style>


