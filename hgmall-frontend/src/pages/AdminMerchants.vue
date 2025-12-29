<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">商家管理</h2>

    <!-- 筛选栏 -->
    <div class="card bg-base-100 shadow-md mb-6">
      <div class="card-body p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="form-control">
            <label class="label">
              <span class="label-text">状态</span>
            </label>
            <select v-model="filters.status" class="select select-bordered" @change="handleFilterChange">
              <option :value="undefined">全部状态</option>
              <option :value="0">禁用</option>
              <option :value="1">正常</option>
            </select>
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">关键词搜索</span>
            </label>
            <input
              v-model="filters.keyword"
              type="text"
              placeholder="商家名称、负责人"
              class="input input-bordered"
              @keyup.enter="handleFilterChange"
            />
          </div>
          <div class="form-control flex items-end">
            <button class="btn btn-primary" @click="handleFilterChange">搜索</button>
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
      <button class="btn btn-sm" @click="loadMerchants">重试</button>
    </div>

    <!-- 商家列表 -->
    <div v-else-if="merchantList.merchants && merchantList.merchants.length > 0" class="space-y-4">
      <div
        v-for="merchant in merchantList.merchants"
        :key="merchant.merchantId"
        class="card bg-base-100 shadow-md"
      >
        <div class="card-body">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-4 mb-2">
                <h3 class="text-xl font-bold">{{ merchant.merchantName }}</h3>
                <div class="badge badge-lg" :class="getStatusBadgeClass(merchant.status)">
                  {{ merchant.statusText || getStatusText(merchant.status) }}
                </div>
              </div>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-base-content/70">
                <div>
                  <span class="text-base-content/50">负责人：</span>
                  <span>{{ merchant.owner || '-' }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">关联用户：</span>
                  <span>{{ merchant.userName || '-' }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">店铺数量：</span>
                  <span>{{ merchant.shopCount || 0 }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">创建时间：</span>
                  <span>{{ formatDate(merchant.createTime) }}</span>
                </div>
              </div>
            </div>
            <div class="flex gap-2">
              <button class="btn btn-sm btn-primary" @click="viewMerchantDetail(merchant.merchantId!)">
                查看详情
              </button>
              <button
                v-if="merchant.status === 0"
                class="btn btn-sm btn-success"
                @click="updateMerchantStatus(merchant.merchantId!, 1)"
              >
                启用
              </button>
              <button
                v-else
                class="btn btn-sm btn-error"
                @click="updateMerchantStatus(merchant.merchantId!, 0)"
              >
                禁用
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center items-center gap-2 mt-6">
        <button
          class="btn btn-sm"
          :disabled="merchantList.page === 1"
          @click="changePage(merchantList.page! - 1)"
        >
          上一页
        </button>
        <span class="text-base-content/60">
          第 {{ merchantList.page }} / {{ totalPages }} 页，共 {{ merchantList.total }} 条
        </span>
        <button
          class="btn btn-sm"
          :disabled="merchantList.page === totalPages"
          @click="changePage(merchantList.page! + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <p class="text-lg text-base-content/60">暂无商家</p>
    </div>

    <!-- 商家详情弹窗 -->
    <dialog ref="merchantDetailModal" class="modal">
      <div class="modal-box max-w-4xl">
        <h3 class="font-bold text-lg mb-4">商家详情</h3>
        <div v-if="selectedMerchant" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-base-content/60">商家ID</p>
              <p class="font-semibold">{{ selectedMerchant.merchantId }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">商家名称</p>
              <p class="font-semibold">{{ selectedMerchant.merchantName }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">负责人</p>
              <p>{{ selectedMerchant.owner || '-' }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">状态</p>
              <div class="badge badge-lg mt-1" :class="getStatusBadgeClass(selectedMerchant.status)">
                {{ selectedMerchant.statusText || getStatusText(selectedMerchant.status) }}
              </div>
            </div>
            <div>
              <p class="text-sm text-base-content/60">关联用户</p>
              <p>{{ selectedMerchant.userName || '-' }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">用户邮箱</p>
              <p>{{ selectedMerchant.userEmail || '-' }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">店铺数量</p>
              <p>{{ selectedMerchant.shopCount || 0 }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">创建时间</p>
              <p>{{ formatDate(selectedMerchant.createTime) }}</p>
            </div>
          </div>

          <!-- 店铺列表 -->
          <div v-if="selectedMerchant.shops && selectedMerchant.shops.length > 0">
            <p class="text-sm text-base-content/60 mb-2">关联店铺</p>
            <div class="space-y-2">
              <div
                v-for="shop in selectedMerchant.shops"
                :key="shop.shopId"
                class="p-3 bg-base-200 rounded-lg"
              >
                <p class="font-semibold">{{ shop.shopName }}</p>
                <p class="text-sm text-base-content/60">店铺ID：{{ shop.shopId }}</p>
              </div>
            </div>
          </div>

          <!-- 审核操作 -->
          <div v-if="selectedMerchant.status === 0" class="flex gap-2 pt-4 border-t border-base-300">
            <button class="btn btn-success" @click="auditMerchant(selectedMerchant.merchantId!, 'approve')">
              审核通过
            </button>
            <button class="btn btn-error" @click="auditMerchant(selectedMerchant.merchantId!, 'reject')">
              审核拒绝
            </button>
          </div>
        </div>
        <div class="modal-action">
          <button class="btn btn-ghost" @click="closeMerchantDetailModal">关闭</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeMerchantDetailModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  apiGetMerchants,
  apiGetMerchantDetail,
  apiAuditMerchant,
  apiUpdateMerchantStatus
} from '../api/admin'
import type { MerchantListVO, MerchantVO } from '../api/generated-admin/models'

const loading = ref(false)
const error = ref<string | null>(null)
const merchantList = ref<MerchantListVO>({ merchants: [], total: 0, page: 1, size: 20 })
const selectedMerchant = ref<MerchantVO | null>(null)

const filters = ref({
  status: undefined as number | undefined,
  keyword: ''
})

const merchantDetailModal = ref<HTMLDialogElement | null>(null)

const pageSize = 20
const totalPages = computed(() => {
  if (!merchantList.value.total || !merchantList.value.size) return 1
  return Math.ceil(merchantList.value.total / merchantList.value.size)
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

function getStatusText(status?: number): string {
  const statusMap: Record<number, string> = {
    0: '禁用',
    1: '正常'
  }
  return statusMap[status ?? -1] || '未知状态'
}

function getStatusBadgeClass(status?: number): string {
  const classMap: Record<number, string> = {
    0: 'badge-error',
    1: 'badge-success'
  }
  return classMap[status ?? -1] || 'badge-ghost'
}

function handleFilterChange() {
  merchantList.value.page = 1
  loadMerchants()
}

function changePage(page: number) {
  merchantList.value.page = page
  loadMerchants()
}

async function loadMerchants() {
  loading.value = true
  error.value = null
  try {
    const params: any = {
      page: merchantList.value.page,
      size: pageSize
    }
    if (filters.value.status !== undefined) {
      params.status = filters.value.status
    }
    if (filters.value.keyword) {
      params.keyword = filters.value.keyword
    }
    merchantList.value = await apiGetMerchants(params)
  } catch (e: any) {
    error.value = e.message || '加载商家列表失败'
    console.error('Failed to load merchants:', e)
  } finally {
    loading.value = false
  }
}

async function viewMerchantDetail(merchantId: number) {
  try {
    selectedMerchant.value = await apiGetMerchantDetail(merchantId)
    merchantDetailModal.value?.showModal()
  } catch (e: any) {
    pushToast(`获取商家详情失败：${e.message || e}`, 'error')
  }
}

function closeMerchantDetailModal() {
  merchantDetailModal.value?.close()
  selectedMerchant.value = null
}

async function auditMerchant(merchantId: number, action: 'approve' | 'reject') {
  const reason = prompt(action === 'approve' ? '请输入审核通过原因（可选）' : '请输入审核拒绝原因（必填）')
  if (action === 'reject' && !reason) {
    pushToast('拒绝原因不能为空', 'error')
    return
  }
  try {
    await apiAuditMerchant(merchantId, action, reason || undefined)
    pushToast(action === 'approve' ? '审核通过成功' : '审核拒绝成功', 'success')
    closeMerchantDetailModal()
    await loadMerchants()
  } catch (e: any) {
    pushToast(`审核失败：${e.message || e}`, 'error')
  }
}

async function updateMerchantStatus(merchantId: number, status: number) {
  const reason = prompt('请输入操作原因（可选）')
  if (!confirm(`确定要${status === 1 ? '启用' : '禁用'}该商家吗？`)) {
    return
  }
  try {
    await apiUpdateMerchantStatus(merchantId, status, reason || undefined)
    pushToast(`${status === 1 ? '启用' : '禁用'}成功`, 'success')
    await loadMerchants()
  } catch (e: any) {
    pushToast(`操作失败：${e.message || e}`, 'error')
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
  loadMerchants()
})
</script>

<style scoped>
</style>


