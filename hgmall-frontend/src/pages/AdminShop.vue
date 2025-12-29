<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">店铺管理</h2>

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
              <option :value="1">正常营业</option>
              <option :value="2">商家关闭</option>
              <option :value="3">管理员强制关闭</option>
            </select>
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">关键词搜索</span>
            </label>
            <input
              v-model="filters.keyword"
              type="text"
              placeholder="店铺名称、商家名称"
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
      <button class="btn btn-sm" @click="loadShops">重试</button>
    </div>

    <!-- 店铺列表 -->
    <div v-else-if="shopList.shops && shopList.shops.length > 0" class="space-y-4">
      <div
        v-for="shop in shopList.shops"
        :key="shop.shopId"
        class="card bg-base-100 shadow-md"
      >
        <div class="card-body">
          <div class="flex items-start gap-4">
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-2 flex-wrap">
                <h3 class="text-xl font-bold truncate flex-1 min-w-0">{{ shop.name }}</h3>
                <ShopStatusBadge :status="shop.status" />
              </div>
              <p v-if="shop.description" class="text-base-content/70 mb-3 break-words line-clamp-2">{{ shop.description }}</p>
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-2 text-sm text-base-content/70">
                <div class="truncate">
                  <span class="text-base-content/50">店铺ID：</span>
                  <span>{{ shop.shopId }}</span>
                </div>
                <div class="truncate">
                  <span class="text-base-content/50">商家ID：</span>
                  <span>{{ shop.merchantId || '-' }}</span>
                </div>
                <div class="truncate">
                  <span class="text-base-content/50">创建时间：</span>
                  <span>{{ formatDate(shop.createTime) }}</span>
                </div>
                <div class="truncate">
                  <span class="text-base-content/50">更新时间：</span>
                  <span>{{ formatDate(shop.updateTime) }}</span>
                </div>
              </div>
            </div>
            <div class="flex flex-col gap-2 flex-shrink-0">
              <button class="btn btn-sm btn-primary whitespace-nowrap" @click="viewShopDetail(shop.shopId!)">
                查看详情
              </button>
              <div class="dropdown dropdown-end">
                <div tabindex="0" role="button" class="btn btn-sm btn-ghost whitespace-nowrap">
                  修改状态
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
                  </svg>
                </div>
                <ul tabindex="0" class="dropdown-content menu bg-base-100 rounded-box z-[1] w-52 p-2 shadow-lg border border-base-300">
                  <li>
                    <a
                      @click="updateShopStatus(shop.shopId!, 1)"
                      :class="{ 'active': shop.status === 1 }"
                    >
                      正常营业
                    </a>
                  </li>
                  <li>
                    <a
                      @click="updateShopStatus(shop.shopId!, 2)"
                      :class="{ 'active': shop.status === 2 }"
                    >
                      商家关闭
                    </a>
                  </li>
                  <li>
                    <a
                      @click="updateShopStatus(shop.shopId!, 3)"
                      :class="{ 'active': shop.status === 3, 'text-error': shop.status !== 3 }"
                    >
                      管理员强制关闭
                    </a>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center items-center gap-2 mt-6">
        <button
          class="btn btn-sm"
          :disabled="shopList.page === 1"
          @click="changePage(shopList.page! - 1)"
        >
          上一页
        </button>
        <span class="text-base-content/60">
          第 {{ shopList.page }} / {{ totalPages }} 页，共 {{ shopList.total }} 条
        </span>
        <button
          class="btn btn-sm"
          :disabled="shopList.page === totalPages"
          @click="changePage(shopList.page! + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <p class="text-lg text-base-content/60">暂无店铺</p>
    </div>

    <!-- 店铺详情弹窗 -->
    <dialog ref="shopDetailModal" class="modal">
      <div class="modal-box max-w-4xl">
        <h3 class="font-bold text-lg mb-4">店铺详情</h3>
        <div v-if="selectedShop" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-base-content/60">店铺ID</p>
              <p class="font-semibold">{{ selectedShop.shopId }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">店铺名称</p>
              <p class="font-semibold">{{ selectedShop.name }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">商家ID</p>
              <p class="font-semibold">{{ selectedShop.merchantId || '-' }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">状态</p>
              <ShopStatusBadge :status="selectedShop.status" />
            </div>
            <div v-if="selectedShop.description" class="col-span-2">
              <p class="text-sm text-base-content/60">店铺描述</p>
              <p class="font-semibold">{{ selectedShop.description }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">创建时间</p>
              <p class="font-semibold">{{ formatDate(selectedShop.createTime) }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">更新时间</p>
              <p class="font-semibold">{{ formatDate(selectedShop.updateTime) }}</p>
            </div>
          </div>
        </div>
        <div class="modal-action">
          <button class="btn btn-ghost" @click="closeShopDetailModal">关闭</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeShopDetailModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { apiGetAdminShops, apiUpdateShopStatus } from '../api/admin'
import type { ShopListVO, ShopVO } from '../api/generated-admin/models'
import ShopStatusBadge from '../components/ShopStatusBadge.vue'

const loading = ref(false)
const error = ref<string | null>(null)
const shopList = ref<ShopListVO>({
  shops: [],
  total: 0,
  page: 1,
  size: 20
})

const filters = ref({
  status: undefined as number | undefined,
  keyword: ''
})

const shopDetailModal = ref<HTMLDialogElement | null>(null)
const selectedShop = ref<ShopVO | null>(null)

const totalPages = computed(() => {
  const total = shopList.value.total || 0
  return Math.ceil(total / (shopList.value.size || 20)) || 1
})

function formatDate(dateStr?: string): string {
  if (!dateStr) return '-'
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
    1: '正常营业',
    2: '商家关闭',
    3: '管理员强制关闭'
  }
  return statusMap[status ?? -1] || '未知状态'
}

async function loadShops() {
  loading.value = true
  error.value = null
  try {
    shopList.value = await apiGetAdminShops({
      status: filters.value.status,
      keyword: filters.value.keyword?.trim() || undefined,
      page: shopList.value.page,
      size: shopList.value.size
    })
  } catch (e: any) {
    error.value = e.message || '加载店铺列表失败'
    console.error('Failed to load shops:', e)
  } finally {
    loading.value = false
  }
}

function handleFilterChange() {
  shopList.value.page = 1
  loadShops()
}

function changePage(page: number) {
  shopList.value.page = page
  loadShops()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function viewShopDetail(shopId: number) {
  const shop = shopList.value.shops?.find(s => s.shopId === shopId)
  if (shop) {
    selectedShop.value = shop
    shopDetailModal.value?.showModal()
  }
}

function closeShopDetailModal() {
  shopDetailModal.value?.close()
  selectedShop.value = null
}

async function updateShopStatus(shopId: number, status: number) {
  const statusText = getStatusText(status)
  if (!confirm(`确定要将店铺状态修改为"${statusText}"吗？`)) {
    return
  }

  try {
    await apiUpdateShopStatus(shopId, status)
    pushToast(`店铺状态已更新为"${statusText}"`, 'success')
    await loadShops()
  } catch (e: any) {
    pushToast(`更新失败：${e.message || e}`, 'error')
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
  loadShops()
})
</script>

<style scoped>
</style>

