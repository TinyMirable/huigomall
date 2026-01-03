<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">销售报表</h2>

    <!-- 筛选栏 -->
    <div class="card bg-base-100 shadow-md mb-6">
      <div class="card-body p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <!-- 店铺选择 -->
          <div class="form-control">
            <label class="label">
              <span class="label-text">店铺</span>
            </label>
            <select v-model="filters.shopId" class="select select-bordered" @change="loadStatistics">
              <option :value="undefined">全部店铺</option>
              <option v-for="shop in shops" :key="shop.shopId" :value="shop.shopId">
                {{ shop.name }}
              </option>
            </select>
          </div>

          <!-- 开始时间 -->
          <div class="form-control">
            <label class="label">
              <span class="label-text">开始时间</span>
            </label>
            <input
              v-model="filters.startTime"
              type="datetime-local"
              class="input input-bordered"
              @change="loadStatistics"
            />
          </div>

          <!-- 结束时间 -->
          <div class="form-control">
            <label class="label">
              <span class="label-text">结束时间</span>
            </label>
            <input
              v-model="filters.endTime"
              type="datetime-local"
              class="input input-bordered"
              @change="loadStatistics"
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
      <button class="btn btn-sm" @click="loadStatistics">重试</button>
    </div>

    <!-- 统计数据 -->
    <div v-else-if="statistics" class="space-y-6">
      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">总订单数</h3>
            <p class="text-3xl font-bold">{{ statistics.totalOrders || 0 }}</p>
          </div>
        </div>
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">已支付订单</h3>
            <p class="text-3xl font-bold text-info">{{ statistics.paidOrders || 0 }}</p>
          </div>
        </div>
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">已发货订单</h3>
            <p class="text-3xl font-bold text-primary">{{ statistics.shippedOrders || 0 }}</p>
          </div>
        </div>
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">已完成订单</h3>
            <p class="text-3xl font-bold text-success">{{ statistics.completedOrders || 0 }}</p>
          </div>
        </div>
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">已取消订单</h3>
            <p class="text-3xl font-bold text-error">{{ statistics.cancelledOrders || 0 }}</p>
          </div>
        </div>
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">总销售额</h3>
            <p class="text-3xl font-bold" style="color: rgb(255, 202, 57);">
              ¥{{ (statistics.totalAmount || 0).toFixed(2) }}
            </p>
          </div>
        </div>
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">已支付金额</h3>
            <p class="text-3xl font-bold text-info">
              ¥{{ (statistics.paidAmount || 0).toFixed(2) }}
            </p>
          </div>
        </div>
        <div class="card bg-base-100 shadow-md">
          <div class="card-body">
            <h3 class="text-sm text-base-content/60 mb-2">已完成金额</h3>
            <p class="text-3xl font-bold text-success">
              ¥{{ (statistics.completedAmount || 0).toFixed(2) }}
            </p>
          </div>
        </div>
      </div>

      <!-- 提示信息 -->
      <!-- <div class="alert alert-info">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span>统计数据基于筛选条件计算，如需查看详细趋势图表，请使用专业数据分析工具。</span>
      </div> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { apiGetOrderStatistics } from '../api/merchantOrder'
import type { ShopVO, OrderStatisticsVO } from '../api/generated/models'

interface Props {
  shops: ShopVO[]
}

defineProps<Props>()

const loading = ref(false)
const error = ref<string | null>(null)
const statistics = ref<OrderStatisticsVO | null>(null)

const filters = ref({
  shopId: undefined as number | undefined,
  startTime: undefined as string | undefined,
  endTime: undefined as string | undefined
})

async function loadStatistics() {
  loading.value = true
  error.value = null
  try {
    const params: any = {}
    
    // shopId是必填参数，如果不传则传0表示查询所有店铺
    params.shopId = filters.value.shopId ?? 0
    
    if (filters.value.startTime) {
      params.startTime = new Date(filters.value.startTime).toISOString()
    }
    if (filters.value.endTime) {
      params.endTime = new Date(filters.value.endTime).toISOString()
    }
    
    statistics.value = await apiGetOrderStatistics(params)
  } catch (e: any) {
    error.value = e.message || '加载统计数据失败'
    console.error('Failed to load statistics:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
</style>

