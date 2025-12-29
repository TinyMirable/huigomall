<template>
  <header class="navbar bg-base-100 shadow-sm px-4 lg:px-8 py-4 sticky top-0 z-50">
    <!-- Logo -->
    <div class="flex-none">
      <a class="btn btn-ghost text-xl flex items-center gap-2" @click="$router.push('/')">
        <img :src="logoUrl" alt="HuiGoMall" class="w-8 h-8" />
        <span class="text-primary font-bold">HuiGoMall</span>
      </a>
    </div>
    <!-- 搜索栏 - 居中 -->
    <div class="flex-1 flex justify-center hidden md:flex">
      <div class="form-control w-full max-w-2xl">
        <div class="relative">
          <input 
            type="text" 
            placeholder="Search" 
            class="input input-bordered w-full bg-base-200 pl-10 pr-10 cursor-pointer" 
            @click="showFeatureInDevelopment"
            readonly
          />
          <!-- 左侧放大镜图标 -->
          <div class="absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-base-content/40" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <!-- 右侧相机图标 -->
          <button 
            class="absolute right-3 top-1/2 -translate-y-1/2 btn btn-ghost btn-sm p-0 h-auto min-h-0"
            @click="showFeatureInDevelopment"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-base-content/40" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
    <!-- 右侧图标 -->
    <div class="flex-none gap-2">
               <!-- 购物车图标 -->
               <button class="btn btn-ghost btn-circle" @click="$router.push('/cart')">
                 <div class="indicator">
                   <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                     <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                   </svg>
                 </div>
               </button>
      <!-- 通知图标 -->
      <button class="btn btn-ghost btn-circle" @click="showFeatureInDevelopment">
        <div class="indicator">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
        </div>
      </button>
      <!-- 用户头像/登录状态 -->
      <div v-if="isLoggedIn" class="dropdown dropdown-end relative z-50">
        <div tabindex="0" role="button" class="btn btn-ghost btn-circle">
          <div class="w-10 h-10 rounded-full bg-pink-500 flex items-center justify-center p-0">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
        </div>
        <ul tabindex="0" class="mt-3 z-[9999] p-0 shadow-lg menu dropdown-content bg-base-100 rounded-box w-72 border border-base-300 min-w-max overflow-visible">
          <!-- 用户信息区域 -->
          <li class="px-4 py-3 border-b border-base-300">
            <div class="flex items-center gap-3 w-full">
              <div class="w-12 h-12 rounded-full bg-pink-500 flex items-center justify-center flex-shrink-0">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <div class="font-semibold text-base truncate">{{ displayName }}</div>
                <div v-if="userSummary.email || userInfo.email" class="text-sm text-base-content/60 truncate">
                  {{ userSummary.email || userInfo.email }}
                </div>
                <div v-if="userSummary.phoneNumber" class="text-xs text-base-content/50 truncate mt-0.5">
                  {{ userSummary.phoneNumber }}
                </div>
              </div>
            </div>
            <!-- 统计信息 -->
            <div v-if="userSummary.orderCount !== undefined || userSummary.shopCount !== undefined" class="flex gap-4 mt-3 text-xs text-base-content/60">
              <div 
                v-if="userSummary.orderCount !== undefined" 
                class="flex items-center gap-1 cursor-pointer hover:text-primary transition-colors"
                @click.stop="$router.push('/orders')"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
                <span>订单：{{ userSummary.orderCount }}</span>
              </div>
              <div v-if="userSummary.shopCount !== undefined && userSummary.shopCount > 0" class="flex items-center gap-1">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
                <span>店铺：{{ userSummary.shopCount }}</span>
              </div>
            </div>
          </li>
          <!-- 商家状态提示 -->
          <li v-if="userSummary.merchantStatus === 'PENDING'" class="px-4 py-2 bg-warning/10 border-b border-base-300">
            <div class="flex items-center gap-2 text-warning text-sm w-full">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              商家申请通过
            </div>
          </li>
          <li v-else-if="userSummary.merchantStatus === 'REJECTED'" class="px-4 py-2 bg-error/10 border-b border-base-300">
            <div class="flex items-center gap-2 text-error text-sm w-full">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              商家申请已拒绝
            </div>
          </li>
          <!-- 注册为商家按钮（仅非商家显示） -->
          <li v-if="!isMerchant" class="px-2 py-2">
            <button class="btn btn-primary btn-sm w-full" @click="onRegisterMerchant">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
              注册为商家
            </button>
          </li>
          <!-- 管理面板（仅管理员显示） -->
          <li v-if="isAdmin">
            <a @click.prevent="$router.push('/admin')" class="flex items-center gap-2 w-full py-2 px-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
              管理面板
            </a>
          </li>
          <!-- 我的店铺（仅商家显示，非管理员） -->
          <li v-if="isMerchant && !isAdmin">
            <a @click.prevent="$router.push('/merchant/shop')" class="flex items-center gap-2 w-full py-2 px-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
              我的店铺
            </a>
          </li>
          <!-- 分隔线 -->
          <li><hr class="my-1" /></li>
          <!-- 修改用户名 -->
          <li>
            <a @click.prevent="openUpdateUsernameModal" class="flex items-center gap-2 w-full py-2 px-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
              </svg>
              修改用户名
            </a>
          </li>
          <!-- 个人设置 -->
          <li>
            <a @click.prevent="$router.push('/settings')" class="flex items-center gap-2 w-full py-2 px-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              个人设置
            </a>
          </li>
          <!-- 我的订单 -->
          <li>
            <a @click.prevent="$router.push('/orders')" class="flex items-center gap-2 w-full py-2 px-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              我的订单
            </a>
          </li>
          <!-- 收货地址管理 -->
          <li>
            <a @click.prevent="$router.push('/addresses')" class="flex items-center gap-2 w-full py-2 px-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              收货地址管理
            </a>
          </li>
          <!-- 退出登录 -->
          <li>
            <a @click="onLogout" class="flex items-center gap-2 text-error cursor-pointer w-full py-2 px-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              退出登录
            </a>
          </li>
        </ul>
      </div>
      <button v-else class="btn btn-ghost text-sm" @click="$router.push('/login')">
        请先登录/注册
      </button>
    </div>

    <!-- 修改用户名弹窗 -->
    <div v-if="showUpdateUsernameModal" class="modal modal-open">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">修改用户名</h3>
        <form @submit.prevent="handleUpdateUsername">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">新用户名</span>
            </label>
            <input
              v-model="updateUsernameForm.username"
              type="text"
              placeholder="请输入新用户名（3-50个字符）"
              class="input input-bordered"
              required
            />
          </div>
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="showUpdateUsernameModal = false">取消</button>
            <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': updateUsernameLoading }">
              {{ updateUsernameLoading ? '修改中...' : '确认修改' }}
            </button>
          </div>
        </form>
      </div>
      <div class="modal-backdrop" @click="showUpdateUsernameModal = false"></div>
    </div>

    <!-- 注册商家弹窗 -->
    <div v-if="showRegisterMerchantModal" class="modal modal-open">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">申请成为商家</h3>
        <form @submit.prevent="handleRegisterMerchant">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">商家名称 <span class="text-error">*</span></span>
            </label>
            <input
              v-model="registerMerchantForm.merchantName"
              type="text"
              placeholder="请输入商家名称（2-100个字符）"
              class="input input-bordered"
              required
            />
          </div>
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">负责人姓名（可选）</span>
            </label>
            <input
              v-model="registerMerchantForm.owner"
              type="text"
              placeholder="请输入负责人姓名（最多50个字符）"
              class="input input-bordered"
            />
          </div>
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="showRegisterMerchantModal = false">取消</button>
            <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': registerMerchantLoading }">
              {{ registerMerchantLoading ? '提交中...' : '确认申请' }}
            </button>
          </div>
        </form>
      </div>
      <div class="modal-backdrop" @click="showRegisterMerchantModal = false"></div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import logoUrl from '../assets/logo.png'
import { apiGetUserSummary, apiUpdateUsername } from '../api/user'
import { apiRegisterMerchant } from '../api/auth'
import type { UserSummaryVO } from '../api/generated/models'

const router = useRouter()

// 使用ref来管理登录状态，确保响应式更新
const loginStatus = ref(!!localStorage.getItem('access_token'))

// 检查登录状态的函数
function checkLoginStatus() {
  loginStatus.value = !!localStorage.getItem('access_token')
}

// 计算属性，基于ref状态
const isLoggedIn = computed(() => loginStatus.value)

// 用户信息
const userInfo = ref<{ username: string; email: string }>({
  username: '',
  email: ''
})

const userSummary = ref<UserSummaryVO>({})

// 判断是否为管理员（角色为ADMIN）
const isAdmin = computed(() => {
  return userSummary.value.role === 'ADMIN'
})

// 判断是否为商家（商家状态为APPROVED）
const isMerchant = computed(() => {
  return userSummary.value.merchantStatus === 'APPROVED'
})

// 显示名称
const displayName = computed(() => {
  return userSummary.value.userName || userInfo.value.username || localStorage.getItem('user_nickname') || '用户'
})

// 从localStorage获取用户信息（作为fallback）
function loadUserInfoFromStorage() {
  const username = localStorage.getItem('user_nickname')
  const email = localStorage.getItem('user_email') || ''
  
  userInfo.value = {
    username: username || '用户',
    email: email
  }
}

// 加载用户概要信息
async function loadUserSummary() {
  if (!isLoggedIn.value) {
    return
  }
  
  try {
    const summary = await apiGetUserSummary()
    userSummary.value = summary
    
    // 更新localStorage
    if (summary.userName) {
      localStorage.setItem('user_nickname', summary.userName)
      userInfo.value.username = summary.userName
    }
    if (summary.email) {
      localStorage.setItem('user_email', summary.email)
      userInfo.value.email = summary.email
    }
  } catch (e: any) {
    console.error('Failed to load user summary:', e)
    // 如果获取失败，使用localStorage中的信息
    loadUserInfoFromStorage()
  }
}

// 监听localStorage变化（跨标签页同步）
function handleStorageChange(e: StorageEvent) {
  if (e.key === 'access_token') {
    checkLoginStatus()
  }
}

// 监听登录状态变化
watch(isLoggedIn, (loggedIn) => {
  if (loggedIn) {
    loadUserSummary()
  } else {
    userSummary.value = {}
    userInfo.value = { username: '', email: '' }
  }
}, { immediate: true })

function pushToast(message: string, type: 'success' | 'error' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : 'alert-error'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2200)
}

// 显示功能开发中提示
function showFeatureInDevelopment() {
  pushToast('该功能还在开发中，敬请期待', 'error')
}

// 修改用户名相关
const showUpdateUsernameModal = ref(false)
const updateUsernameForm = ref({
  username: ''
})
const updateUsernameLoading = ref(false)

async function handleUpdateUsername() {
  const trimmedUsername = updateUsernameForm.value.username.trim()
  if (!trimmedUsername) {
    pushToast('请输入用户名', 'error')
    return
  }
  if (trimmedUsername.length < 3) {
    pushToast('用户名长度至少为3位', 'error')
    return
  }
  if (trimmedUsername.length > 50) {
    pushToast('用户名长度不能超过50位', 'error')
    return
  }

  updateUsernameLoading.value = true
  try {
    const result = await apiUpdateUsername(trimmedUsername)
    localStorage.setItem('user_nickname', result.username)
    userInfo.value.username = result.username
    userSummary.value.userName = result.username
    pushToast('用户名修改成功', 'success')
    showUpdateUsernameModal.value = false
    updateUsernameForm.value.username = ''
  } catch (e: any) {
    pushToast(`修改失败：${e.message || e}`, 'error')
  } finally {
    updateUsernameLoading.value = false
  }
}

function openUpdateUsernameModal() {
  updateUsernameForm.value.username = displayName.value
  showUpdateUsernameModal.value = true
}

// 注册商家相关
const showRegisterMerchantModal = ref(false)
const registerMerchantForm = ref({
  merchantName: '',
  owner: ''
})
const registerMerchantLoading = ref(false)

function onRegisterMerchant() {
  showRegisterMerchantModal.value = true
}

async function handleRegisterMerchant() {
  const trimmedMerchantName = registerMerchantForm.value.merchantName.trim()
  if (!trimmedMerchantName) {
    pushToast('请输入商家名称', 'error')
    return
  }
  if (trimmedMerchantName.length < 2) {
    pushToast('商家名称长度至少为2位', 'error')
    return
  }
  if (trimmedMerchantName.length > 100) {
    pushToast('商家名称长度不能超过100位', 'error')
    return
  }

  registerMerchantLoading.value = true
  try {
    await apiRegisterMerchant({
      merchantName: trimmedMerchantName,
      owner: registerMerchantForm.value.owner?.trim() || undefined
    })
    pushToast('商家注册申请已提交，请等待审核', 'success')
    showRegisterMerchantModal.value = false
    registerMerchantForm.value = {
      merchantName: '',
      owner: ''
    }
    // 重新加载用户信息以更新商家状态
    await loadUserSummary()
  } catch (e: any) {
    pushToast(`注册失败：${e.message || e}`, 'error')
  } finally {
    registerMerchantLoading.value = false
  }
}

function onLogout(e?: Event) {
  // 阻止默认行为和事件冒泡
  if (e) {
    e.preventDefault()
    e.stopPropagation()
  }
  
  // 清除所有登录相关的localStorage
  localStorage.removeItem('access_token')
  localStorage.removeItem('user_nickname')
  localStorage.removeItem('user_email')
  
  // 立即更新登录状态
  loginStatus.value = false
  
  pushToast('已退出登录', 'success')
  
  // 跳转到首页，确保显示未登录状态
  // 使用replace避免在历史记录中留下登录状态的记录
  router.replace('/').then(() => {
    // 强制刷新页面，确保所有组件都重新加载并读取最新的localStorage状态
    window.location.reload()
  }).catch(() => {
    // 如果路由跳转失败，直接刷新当前页面
    window.location.href = '/'
  })
}

onMounted(() => {
  // 初始化时检查登录状态
  checkLoginStatus()
  // 如果已登录，加载用户信息
  if (isLoggedIn.value) {
    loadUserSummary()
  } else {
    loadUserInfoFromStorage()
  }
  // 监听localStorage变化（用于跨标签页同步）
  window.addEventListener('storage', handleStorageChange)
})
</script>

<style scoped>
/* 确保dropdown菜单在最上层且可交互 */
.dropdown.dropdown-end {
  position: relative;
}

.dropdown-content {
  position: absolute !important;
  z-index: 9999 !important;
  pointer-events: auto !important;
}

.dropdown-content li {
  pointer-events: auto !important;
}

.dropdown-content li * {
  pointer-events: auto !important;
}
</style>
