<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-7xl">
        <div class="flex items-center justify-between mb-6">
          <h1 class="text-3xl font-bold">我的店铺</h1>
        </div>

        <div class="flex gap-6">
          <!-- 左侧菜单栏 -->
          <div class="w-64 flex-shrink-0">
            <div class="card bg-base-100 shadow-lg sticky top-24">
              <div class="card-body p-4">
                <ul class="menu menu-vertical w-full">
                  <li>
                    <a
                      :class="{ 'active': currentView === 'shops' }"
                      @click="currentView = 'shops'"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                      </svg>
                      店铺管理
                    </a>
                  </li>
                  <li>
                    <a
                      :class="{ 'active': currentView === 'orders' }"
                      @click="currentView = 'orders'"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                      </svg>
                      订单管理
                    </a>
                  </li>
                  <li>
                    <a
                      :class="{ 'active': currentView === 'statistics' }"
                      @click="currentView = 'statistics'"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                      </svg>
                      销售报表
                    </a>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <!-- 右侧内容区域 -->
          <div class="flex-1">
            <!-- 店铺管理视图 -->
            <div v-if="currentView === 'shops'">
              <div class="flex items-center justify-between mb-6">
                <h2 class="text-2xl font-bold">店铺管理</h2>
                <button
                  class="btn btn-primary"
                  @click="showCreateShopModal = true"
                  :disabled="shops.length >= 3"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                  </svg>
                  创建店铺
                </button>
              </div>

        <!-- 店铺数量提示 -->
        <div v-if="shops.length >= 3" class="alert alert-info mb-6">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>您已达到店铺数量上限（最多3个店铺）</span>
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
        <div v-else-if="shops.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="shop in shops"
            :key="shop.shopId"
            class="card bg-base-100 shadow-lg hover:shadow-xl transition-shadow cursor-pointer"
            @click="goToShopDetail(shop.shopId)"
          >
            <div class="card-body">
              <div class="flex items-start gap-2 mb-4">
                <h2 class="card-title text-xl flex-1 min-w-0 truncate">{{ shop.name }}</h2>
                <ShopStatusBadge :status="shop.status" />
              </div>

              <p v-if="shop.description" class="text-base-content/70 mb-4 break-words line-clamp-3">
                {{ shop.description }}
              </p>
              <p v-else class="text-base-content/50 mb-4 italic">暂无描述</p>

              <div class="text-sm text-base-content/60 space-y-1 mb-4">
                <p class="truncate">店铺ID：{{ shop.shopId }}</p>
                <p v-if="shop.createTime" class="truncate">创建时间：{{ formatDate(shop.createTime) }}</p>
              </div>

              <div class="flex justify-between items-center gap-2 mt-auto">
                <button
                  class="btn btn-primary btn-sm flex-shrink-0"
                  @click.stop="goToShopDetail(shop.shopId)"
                >
                  进入店铺
                </button>
                <button
                  v-if="shop.status === 1"
                  class="btn btn-ghost btn-xs text-xs text-base-content/50 hover:text-base-content/70 flex-shrink-0 whitespace-nowrap"
                  @click.stop="openCloseShopModal(shop)"
                  :disabled="closing"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                  关闭店铺
                </button>
                <button
                  v-if="shop.status === 2"
                  class="btn btn-success btn-xs text-xs flex-shrink-0 whitespace-nowrap"
                  @click.stop="openResumeShopModal(shop)"
                  :disabled="resuming"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                  重新开启
                </button>
              </div>
            </div>
          </div>
        </div>

              <!-- 空状态 -->
              <div v-else class="text-center py-12">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-base-content/30 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
                <p class="text-lg text-base-content/60 mb-4">暂无店铺</p>
                <button class="btn btn-primary" @click="showCreateShopModal = true">创建第一个店铺</button>
              </div>
            </div>

            <!-- 订单管理视图 -->
            <MerchantOrders v-if="currentView === 'orders'" :shops="shops" />

            <!-- 销售报表视图 -->
            <MerchantStatistics v-if="currentView === 'statistics'" :shops="shops" />
          </div>
        </div>
      </div>
    </div>
    <Footer />

    <!-- 创建店铺弹窗 -->
    <CreateShopModal
      v-model="showCreateShopModal"
      :loading="creating"
      :email="userEmail"
      :send-code-fn="sendCreateShopCode"
      @submit="handleCreateShop"
      @code-error="(e) => pushToast(e.message, 'error')"
    />

    <!-- 关闭店铺弹窗 -->
    <CloseShopModal
      v-model="showCloseShopModal"
      :loading="closing"
      :shop="selectedShop"
      :email="userEmail"
      :send-code-fn="sendCloseShopCode"
      @submit="handleCloseShop"
      @code-error="(e) => pushToast(e.message, 'error')"
    />

    <!-- 重新开启店铺弹窗 -->
    <ResumeShopModal
      v-model="showResumeShopModal"
      :loading="resuming"
      :shop="selectedShop"
      :email="userEmail"
      :send-code-fn="sendResumeShopCode"
      @submit="handleResumeShop"
      @code-error="(e) => pushToast(e.message, 'error')"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import MerchantOrders from './MerchantOrders.vue'
import MerchantStatistics from './MerchantStatistics.vue'
import ShopStatusBadge from '../components/ShopStatusBadge.vue'
import CreateShopModal from '../components/CreateShopModal.vue'
import CloseShopModal from '../components/CloseShopModal.vue'
import ResumeShopModal from '../components/ResumeShopModal.vue'
import { apiGetShops, apiCreateShop, apiCloseShop, apiResumeShop } from '../api/shop'
import { apiSendVerificationCode } from '../api/auth'
import { apiGetUserSummary } from '../api/user'
import type { ShopVO } from '../api/generated/models'

const router = useRouter()

const currentView = ref<'shops' | 'orders' | 'statistics'>('shops')
const loading = ref(false)
const error = ref<string | null>(null)
const shops = ref<ShopVO[]>([])
const creating = ref(false)
const closing = ref(false)
const resuming = ref(false)
const userEmail = ref<string>('')

const showCreateShopModal = ref(false)
const showCloseShopModal = ref(false)
const showResumeShopModal = ref(false)
const selectedShop = ref<ShopVO | null>(null)

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


async function loadShops() {
  loading.value = true
  error.value = null
  try {
    shops.value = await apiGetShops()
  } catch (e: any) {
    if (e.message && e.message.includes('未登录')) {
      error.value = '请先登录'
    } else {
      error.value = e.message || '加载店铺列表失败'
    }
    console.error('Failed to load shops:', e)
  } finally {
    loading.value = false
  }
}

async function sendCreateShopCode(emailAddr: string): Promise<void> {
  if (!emailAddr) {
    throw new Error('无法获取邮箱地址，请先完善个人信息')
  }
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleCreateShop(data: { name: string; description?: string; code: string }) {
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

  creating.value = true
  try {
    await apiCreateShop({
      name: trimmedName,
      description: data.description,
      code: data.code
    })
    pushToast('店铺创建成功', 'success')
    showCreateShopModal.value = false
    await loadShops()
  } catch (e: any) {
    pushToast(`创建失败：${e.message || e}`, 'error')
  } finally {
    creating.value = false
  }
}

function openCloseShopModal(shop: ShopVO) {
  selectedShop.value = shop
  showCloseShopModal.value = true
}

async function sendCloseShopCode(emailAddr: string): Promise<void> {
  if (!emailAddr) {
    throw new Error('无法获取邮箱地址，请先完善个人信息')
  }
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleCloseShop(code: string) {
  if (!selectedShop.value?.shopId) {
    pushToast('店铺信息错误', 'error')
    return
  }

  if (!confirm('确定要关闭这个店铺吗？关闭后该店铺下的所有商品将自动下架，未支付的订单将被取消。')) {
    return
  }

  closing.value = true
  try {
    await apiCloseShop(selectedShop.value.shopId, code)
    pushToast('店铺已关闭', 'success')
    showCloseShopModal.value = false
    selectedShop.value = null
    await loadShops()
  } catch (e: any) {
    pushToast(`关闭失败：${e.message || e}`, 'error')
  } finally {
    closing.value = false
  }
}

function goToShopDetail(shopId?: number) {
  if (shopId) {
    router.push(`/shop/${shopId}`)
  }
}

// 重新开启店铺相关
function openResumeShopModal(shop: ShopVO) {
  selectedShop.value = shop
  showResumeShopModal.value = true
}

async function sendResumeShopCode(emailAddr: string): Promise<void> {
  if (!emailAddr) {
    throw new Error('无法获取邮箱地址，请先完善个人信息')
  }
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleResumeShop(code: string) {
  if (!selectedShop.value?.shopId) {
    pushToast('店铺信息错误', 'error')
    return
  }

  resuming.value = true
  try {
    await apiResumeShop(selectedShop.value.shopId, code)
    pushToast('店铺已重新开启', 'success')
    showResumeShopModal.value = false
    selectedShop.value = null
    await loadShops()
  } catch (e: any) {
    pushToast(`重新开启失败：${e.message || e}`, 'error')
  } finally {
    resuming.value = false
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

async function loadUserEmail() {
  try {
    const summary = await apiGetUserSummary()
    userEmail.value = summary.email || localStorage.getItem('user_email') || ''
    if (summary.email) {
      localStorage.setItem('user_email', summary.email)
    }
  } catch (e: any) {
    console.error('Failed to load user email:', e)
    userEmail.value = localStorage.getItem('user_email') || ''
  }
}

onMounted(async () => {
  await loadUserEmail()
  await loadShops()
})
</script>

<style scoped>
</style>

