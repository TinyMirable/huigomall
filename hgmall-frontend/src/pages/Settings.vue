<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-4xl">
      <!-- 标题 -->
      <h1 class="text-3xl font-bold text-center mb-8">个人中心</h1>

      <!-- 用户信息卡片 -->
      <div class="card bg-base-100 shadow-lg mb-8">
        <div class="card-body">
          <div class="flex items-center gap-6">
            <!-- 头像 -->
            <div class="w-20 h-20 rounded-full bg-pink-500 flex items-center justify-center flex-shrink-0">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <!-- 用户信息 -->
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <h2 class="text-2xl font-bold">{{ displayName }}</h2>
                <button class="btn btn-ghost btn-sm btn-circle" @click="openUpdateUsernameModal" title="修改用户名">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                  </svg>
                </button>
              </div>
              <div class="flex flex-col gap-2 text-base-content/60">
                <div class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                  </svg>
                  <span>{{ userSummary.phoneNumber || '未设置' }}</span>
                  <button class="btn btn-ghost btn-xs btn-circle" @click.stop="openUpdatePhoneModal" title="修改手机号">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                  </button>
                </div>
                <div class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                  <span>{{ userSummary.email || userInfo.email || '未设置' }}</span>
                  <button class="btn btn-ghost btn-xs btn-circle" @click.stop="openUpdateEmailModal" title="修改邮箱">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能卡片网格 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <!-- 订单管理 -->
        <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow cursor-pointer" @click="handleOrderManagement">
          <div class="card-body items-center text-center">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
            </div>
            <h3 class="font-semibold">订单管理</h3>
          </div>
        </div>

        <!-- 收货地址管理 -->
        <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow cursor-pointer" @click="handleAddressManagement">
          <div class="card-body items-center text-center">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </div>
            <h3 class="font-semibold">收货地址管理</h3>
          </div>
        </div>

        <!-- 优惠券 -->
        <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow cursor-pointer" @click="handleCoupons">
          <div class="card-body items-center text-center">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="font-semibold">优惠券</h3>
          </div>
        </div>

        <!-- 个人信息编辑 -->
        <!-- <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow cursor-pointer" @click="handleEditProfile">
          <div class="card-body items-center text-center">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
              </svg>
            </div>
            <h3 class="font-semibold">个人信息编辑</h3>
          </div>
        </div> -->

        <!-- 修改密码 -->
        <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow cursor-pointer" @click="showChangePasswordModal = true">
          <div class="card-body items-center text-center">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
              </svg>
            </div>
            <h3 class="font-semibold">修改密码</h3>
          </div>
        </div>

        <!-- 申请成为商家（仅非管理员且非商家显示） -->
        <div v-if="!isAdmin && !isMerchant" class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow cursor-pointer" @click="handleMerchantApplication">
          <div class="card-body items-center text-center">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
            </div>
            <h3 class="font-semibold">申请成为商家</h3>
          </div>
        </div>
      </div>

      <!-- 退出登录按钮 -->
      <div class="text-center">
        <button class="btn btn-outline btn-error" @click="handleLogout">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          退出登录
        </button>
      </div>
      </div>
    </div>
    <Footer />

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

    <!-- 修改密码弹窗 -->
    <div v-if="showChangePasswordModal" class="modal modal-open">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">修改密码</h3>
        <form @submit.prevent="handleChangePassword">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">邮箱</span>
            </label>
            <input
              v-model="changePasswordForm.email"
              type="email"
              placeholder="请输入您的邮箱"
              class="input input-bordered"
              required
            />
          </div>
          <VerificationCodeInput
            ref="changePasswordCodeInput"
            v-model="changePasswordForm.code"
            :email="changePasswordForm.email"
            :send-code-fn="sendChangePasswordCode"
            @code-send-error="(e) => pushToast(e.message, 'error')"
          />
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">新密码</span>
            </label>
            <input
              v-model="changePasswordForm.newPassword"
              type="password"
              placeholder="请输入新密码（至少6位）"
              class="input input-bordered"
              autocomplete="new-password"
              required
            />
          </div>
          <div class="form-control mb-6">
            <label class="label">
              <span class="label-text">确认新密码</span>
            </label>
            <input
              v-model="changePasswordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              class="input input-bordered"
              autocomplete="new-password"
              required
            />
          </div>
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="showChangePasswordModal = false">取消</button>
            <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': loading }">
              {{ loading ? '修改中...' : '确认修改' }}
            </button>
          </div>
        </form>
      </div>
      <div class="modal-backdrop" @click="showChangePasswordModal = false"></div>
    </div>

    <!-- 修改邮箱弹窗 -->
    <div v-if="showUpdateEmailModal" class="modal modal-open">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">修改邮箱</h3>
        <form @submit.prevent="handleUpdateEmail">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">新邮箱地址</span>
            </label>
            <input
              v-model="updateEmailForm.email"
              type="email"
              placeholder="请输入新邮箱地址"
              class="input input-bordered"
              required
            />
          </div>
          <div v-if="!isAdmin">
            <VerificationCodeInput
              ref="updateEmailCodeInput"
              v-model="updateEmailForm.code"
              :email="updateEmailForm.email"
              :send-code-fn="sendUpdateEmailCode"
              label="验证码"
              hint="验证码将发送到新邮箱"
              @code-send-error="(e) => pushToast(e.message, 'error')"
            />
          </div>
          <div v-else class="alert alert-info mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>管理员无需验证码</span>
          </div>
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="showUpdateEmailModal = false">取消</button>
            <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': updateEmailLoading }">
              {{ updateEmailLoading ? '修改中...' : '确认修改' }}
            </button>
          </div>
        </form>
      </div>
      <div class="modal-backdrop" @click="showUpdateEmailModal = false"></div>
    </div>

    <!-- 修改手机号弹窗 -->
    <div v-if="showUpdatePhoneModal" class="modal modal-open">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">修改手机号</h3>
        <form @submit.prevent="handleUpdatePhone">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">新手机号</span>
            </label>
            <input
              v-model="updatePhoneForm.phoneNumber"
              type="tel"
              placeholder="请输入新手机号（11位数字）"
              class="input input-bordered"
              required
            />
          </div>
          <div v-if="!isAdmin">
            <div v-if="userSummary.email || userInfo.email">
              <VerificationCodeInput
                ref="updatePhoneCodeInput"
                v-model="updatePhoneForm.code"
                :email="userSummary.email || userInfo.email || ''"
                :send-code-fn="sendUpdatePhoneCode"
                label="邮箱验证码"
                hint="验证码将发送到您的邮箱"
                @code-send-error="(e) => pushToast(e.message, 'error')"
              />
            </div>
            <div v-else class="alert alert-warning mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <span>您还没有设置邮箱，请先设置邮箱后再修改手机号</span>
            </div>
          </div>
          <div v-else class="alert alert-info mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>管理员无需验证码</span>
          </div>
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="showUpdatePhoneModal = false">取消</button>
            <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': updatePhoneLoading || (!isAdmin && !(userSummary.email || userInfo.email)) }">
              {{ updatePhoneLoading ? '修改中...' : '确认修改' }}
            </button>
          </div>
        </form>
      </div>
      <div class="modal-backdrop" @click="showUpdatePhoneModal = false"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import VerificationCodeInput from '../components/VerificationCodeInput.vue'
import { apiGetUserSummary, apiUpdateUsername, apiSendEmailCodeForUpdate, apiUpdateEmail, apiUpdatePhone } from '../api/user'
import { apiChangePassword, apiSendVerificationCode, apiRegisterMerchant } from '../api/auth'
import type { UserSummaryVO } from '../api/generated/models'

const router = useRouter()

const userInfo = ref<{ username: string; email: string }>({
  username: '',
  email: ''
})

const userSummary = ref<UserSummaryVO>({})

const displayName = computed(() => {
  return userSummary.value.userName || userInfo.value.username || localStorage.getItem('user_nickname') || '用户'
})

// 判断是否为管理员（角色为ADMIN）
const isAdmin = computed(() => {
  return userSummary.value.role === 'ADMIN'
})

// 判断是否为商家（商家状态为APPROVED）
const isMerchant = computed(() => {
  return userSummary.value.merchantStatus === 'APPROVED'
})

// 修改用户名相关
const showUpdateUsernameModal = ref(false)
const updateUsernameForm = ref({
  username: ''
})
const updateUsernameLoading = ref(false)

// 注册商家相关
const showRegisterMerchantModal = ref(false)
const registerMerchantForm = ref({
  merchantName: '',
  owner: ''
})
const registerMerchantLoading = ref(false)

// 修改密码相关
const showChangePasswordModal = ref(false)
const changePasswordForm = ref({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})
const loading = ref(false)
const changePasswordCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)

// 修改邮箱相关
const showUpdateEmailModal = ref(false)
const updateEmailForm = ref({
  email: '',
  code: ''
})
const updateEmailLoading = ref(false)
const updateEmailCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)

// 修改手机号相关
const showUpdatePhoneModal = ref(false)
const updatePhoneForm = ref({
  phoneNumber: '',
  code: ''
})
const updatePhoneLoading = ref(false)
const updatePhoneCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)

function pushToast(message: string, type: 'success' | 'error' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : 'alert-error'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

async function loadUserSummary() {
  try {
    const summary = await apiGetUserSummary()
    userSummary.value = summary
    
    if (summary.userName) {
      localStorage.setItem('user_nickname', summary.userName)
      userInfo.value.username = summary.userName
    }
    if (summary.email) {
      localStorage.setItem('user_email', summary.email)
      userInfo.value.email = summary.email
      // 初始化修改密码表单的邮箱
      changePasswordForm.value.email = summary.email
    }
  } catch (e: any) {
    console.error('Failed to load user summary:', e)
    // 从localStorage获取fallback信息
    const username = localStorage.getItem('user_nickname')
    const email = localStorage.getItem('user_email') || ''
    userInfo.value = {
      username: username || '用户',
      email: email
    }
    changePasswordForm.value.email = email
  }
}

function openUpdateUsernameModal() {
  updateUsernameForm.value.username = displayName.value
  showUpdateUsernameModal.value = true
}

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
    // 重新加载用户信息
    await loadUserSummary()
  } catch (e: any) {
    pushToast(`修改失败：${e.message || e}`, 'error')
  } finally {
    updateUsernameLoading.value = false
  }
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
    pushToast('商家注册成功！', 'success')
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

async function sendChangePasswordCode(emailAddr: string): Promise<void> {
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleChangePassword() {
  if (!changePasswordForm.value.email || !changePasswordForm.value.code || !changePasswordForm.value.newPassword || !changePasswordForm.value.confirmPassword) {
    pushToast('请填写所有必填项', 'error')
    return
  }
  if (changePasswordForm.value.newPassword.length < 6) {
    pushToast('密码长度不能少于6位', 'error')
    return
  }
  if (changePasswordForm.value.newPassword !== changePasswordForm.value.confirmPassword) {
    pushToast('两次输入的密码不一致', 'error')
    return
  }

  loading.value = true
  try {
    await apiChangePassword({
      email: changePasswordForm.value.email,
      code: changePasswordForm.value.code,
      newPassword: changePasswordForm.value.newPassword
    })
    pushToast('密码修改成功', 'success')
    // 验证成功，清除倒计时
    if (changePasswordCodeInput.value) {
      changePasswordCodeInput.value.clearCountdown()
    }
    showChangePasswordModal.value = false
    // 重置表单
    changePasswordForm.value = {
      email: userSummary.value.email || userInfo.value.email || '',
      code: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (e: any) {
    pushToast(`修改失败：${e.message || e}`, 'error')
  } finally {
    loading.value = false
  }
}

function handleOrderManagement() {
  router.push('/orders')
}

function handleAddressManagement() {
  try {
    router.push('/addresses').catch((err) => {
      console.error('路由跳转失败:', err)
      // 如果路由跳转失败，尝试使用 window.location
      window.location.href = '/addresses'
    })
  } catch (error) {
    console.error('跳转地址管理失败:', error)
    window.location.href = '/addresses'
  }
}

function handleCoupons() {
  pushToast('优惠券功能开发中', 'error')
}

// function handleEditProfile() {
//   // 保留此函数以保持向后兼容，但实际功能已通过独立的编辑按钮实现
//   pushToast('请点击手机号或邮箱旁的编辑按钮进行修改', 'error')
// }

function openUpdateEmailModal() {
  updateEmailForm.value.email = userSummary.value.email || userInfo.value.email || ''
  updateEmailForm.value.code = ''
  showUpdateEmailModal.value = true
}

async function sendUpdateEmailCode(emailAddr: string): Promise<void> {
  await apiSendEmailCodeForUpdate(emailAddr)
  pushToast('验证码已发送到新邮箱', 'success')
}

async function handleUpdateEmail() {
  const trimmedEmail = updateEmailForm.value.email.trim()
  if (!trimmedEmail) {
    pushToast('请输入邮箱地址', 'error')
    return
  }
  
  // 验证邮箱格式
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailPattern.test(trimmedEmail)) {
    pushToast('请输入正确的邮箱地址', 'error')
    return
  }
  
  // 检查是否与当前邮箱相同
  const currentEmail = userSummary.value.email || userInfo.value.email
  if (currentEmail && trimmedEmail.toLowerCase() === currentEmail.toLowerCase()) {
    pushToast('新邮箱不能与当前邮箱相同', 'error')
    return
  }
  
  // 如果是管理员，不需要验证码
  const isAdminUser = isAdmin.value
  if (!isAdminUser && !updateEmailForm.value.code) {
    pushToast('请输入验证码', 'error')
    return
  }

  updateEmailLoading.value = true
  try {
    await apiUpdateEmail(trimmedEmail, isAdminUser ? undefined : updateEmailForm.value.code)
    pushToast('邮箱修改成功', 'success')
    // 清除验证码倒计时
    if (updateEmailCodeInput.value) {
      updateEmailCodeInput.value.clearCountdown()
    }
    showUpdateEmailModal.value = false
    updateEmailForm.value = {
      email: '',
      code: ''
    }
    // 重新加载用户信息
    await loadUserSummary()
  } catch (e: any) {
    pushToast(`修改失败：${e.message || e}`, 'error')
  } finally {
    updateEmailLoading.value = false
  }
}

function openUpdatePhoneModal() {
  updatePhoneForm.value.phoneNumber = userSummary.value.phoneNumber || ''
  updatePhoneForm.value.code = ''
  showUpdatePhoneModal.value = true
}

async function sendUpdatePhoneCode(emailAddr: string): Promise<void> {
  // 修改手机号需要使用用户当前的邮箱发送验证码
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function handleUpdatePhone() {
  const trimmedPhone = updatePhoneForm.value.phoneNumber.trim()
  if (!trimmedPhone) {
    pushToast('请输入手机号', 'error')
    return
  }
  
  // 验证手机号格式（简单的11位数字验证）
  const phonePattern = /^1[3-9]\d{9}$/
  if (!phonePattern.test(trimmedPhone)) {
    pushToast('请输入正确的手机号（11位数字）', 'error')
    return
  }
  
  // 检查是否与当前手机号相同
  const currentPhone = userSummary.value.phoneNumber
  if (currentPhone && trimmedPhone === currentPhone) {
    pushToast('新手机号不能与当前手机号相同', 'error')
    return
  }
  
  // 如果是管理员，不需要验证码
  const isAdminUser = isAdmin.value
  const currentEmail = userSummary.value.email || userInfo.value.email
  
  if (!isAdminUser) {
    if (!currentEmail) {
      pushToast('您还没有设置邮箱，请先设置邮箱后再修改手机号', 'error')
      showUpdatePhoneModal.value = false
      openUpdateEmailModal()
      return
    }
    if (!updatePhoneForm.value.code) {
      pushToast('请输入验证码', 'error')
      return
    }
  }

  updatePhoneLoading.value = true
  try {
    await apiUpdatePhone(trimmedPhone, isAdminUser ? undefined : updatePhoneForm.value.code)
    pushToast('手机号修改成功', 'success')
    // 清除验证码倒计时
    if (updatePhoneCodeInput.value) {
      updatePhoneCodeInput.value.clearCountdown()
    }
    showUpdatePhoneModal.value = false
    updatePhoneForm.value = {
      phoneNumber: '',
      code: ''
    }
    // 重新加载用户信息
    await loadUserSummary()
  } catch (e: any) {
    pushToast(`修改失败：${e.message || e}`, 'error')
  } finally {
    updatePhoneLoading.value = false
  }
}

function handleMerchantApplication() {
  showRegisterMerchantModal.value = true
}

function handleLogout() {
  localStorage.removeItem('access_token')
  localStorage.removeItem('user_nickname')
  localStorage.removeItem('user_email')
  pushToast('已退出登录', 'success')
  router.push('/login')
}

onMounted(() => {
  loadUserSummary()
})
</script>

<style scoped>
</style>
