<template>
  <div class="min-h-screen flex items-center justify-center bg-base-100 py-12 px-4">
    <div class="card w-full max-w-md bg-base-100 shadow-xl">
      <div class="card-body">
        <div class="flex flex-col items-center mb-6">
          <h2 class="text-2xl font-bold">登录</h2>
        </div>
        
        <!-- 切换管理员/普通用户 -->
        <div class="tabs tabs-boxed mb-4">
          <a 
            class="tab flex-1" 
            :class="{ 'tab-active': !isAdmin }"
            @click="isAdmin = false"
          >
            普通用户
          </a>
          <a 
            class="tab flex-1" 
            :class="{ 'tab-active': isAdmin }"
            @click="isAdmin = true"
          >
            管理员
          </a>
        </div>
        
        <!-- 登录方式切换 -->
        <div class="tabs tabs-boxed mb-6">
          <a 
            class="tab flex-1" 
            :class="{ 'tab-active': loginMethod === 'password' }"
            @click="loginMethod = 'password'"
          >
            密码登录
          </a>
          <a 
            class="tab flex-1" 
            :class="{ 'tab-active': loginMethod === 'code' }"
            @click="loginMethod = 'code'"
          >
            验证码登录
          </a>
        </div>
        
        <!-- 密码登录表单 -->
        <form v-if="loginMethod === 'password'" @submit.prevent="onPasswordLogin">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">用户名/邮箱/手机号</span>
            </label>
            <input
              v-model="identifier"
              type="text"
              placeholder="请输入用户名、邮箱或手机号"
              class="input input-bordered"
              autocomplete="username"
            />
          </div>
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">密码</span>
            </label>
            <input
              v-model="password"
              type="password"
              placeholder="请输入密码"
              class="input input-bordered"
              autocomplete="current-password"
              @keyup.enter="onPasswordLogin"
            />
          </div>
          <button 
            type="submit"
            class="btn btn-primary w-full" 
            :class="{ 'btn-disabled': loading }"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>
        
        <!-- 验证码登录表单 -->
        <div v-if="loginMethod === 'code'">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">邮箱</span>
            </label>
            <input
              v-model="contact"
              type="email"
              placeholder="请输入邮箱地址"
              class="input input-bordered"
            />
          </div>
          <VerificationCodeInput
            v-model="code"
            :email="contact"
            :send-code-fn="sendCode"
            @enter="onCodeLogin"
            @code-send-error="(e) => pushToast(e.message, 'error')"
          />
          <button 
            class="btn btn-primary w-full" 
            :class="{ 'btn-disabled': loading }"
            @click="onCodeLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
        
        <!-- 注册和忘记密码链接（仅普通用户显示） -->
        <div v-if="!isAdmin" class="flex justify-between items-center mt-4">
          <a class="link link-primary" @click="$router.push('/register')">还没有账号？去注册</a>
          <a class="link link-hover" @click="$router.push('/forgot-password')">忘记密码？</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { apiLogin, apiLoginByCode, apiSendVerificationCode } from '../api/auth'
import VerificationCodeInput from '../components/VerificationCodeInput.vue'


const router = useRouter()

const isAdmin = ref(false)
const loginMethod = ref<'password' | 'code'>('password')
const identifier = ref('')
const password = ref('')
const contact = ref('')
const code = ref('')
const loading = ref(false)
// const codeLoading = ref(false)
// const countdown = ref(0)

function pushToast(message: string, type: 'success' | 'error' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : 'alert-error'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

async function onPasswordLogin() {
  if (!identifier.value || !password.value) {
    pushToast('请输入用户名/邮箱/手机号和密码', 'error')
    return
  }
  loading.value = true
  try {
    const data = await apiLogin({
      identifier: identifier.value,
      password: password.value,
    }, isAdmin.value)
    localStorage.setItem('access_token', data.access_token)
    if (data.user?.username) {
      localStorage.setItem('user_nickname', data.user.username)
    }
    if (data.user?.email) {
      localStorage.setItem('user_email', data.user.email)
    }
    pushToast('登录成功', 'success')
    setTimeout(() => router.push('/'), 800)
  } catch (e: any) {
    pushToast(`登录失败：${e.message || e}`, 'error')
  } finally {
    loading.value = false
  }
}

async function onCodeLogin() {
  if (!contact.value || !code.value) {
    pushToast('请输入邮箱和验证码', 'error')
    return
  }
  
  // 验证邮箱格式
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailPattern.test(contact.value)) {
    pushToast('请输入正确的邮箱地址', 'error')
    return
  }
  loading.value = true
  try {
    const data = await apiLoginByCode({
      contact: contact.value,
      code: code.value,
    }, isAdmin.value)
    localStorage.setItem('access_token', data.access_token)
    if (data.user?.username) {
      localStorage.setItem('user_nickname', data.user.username)
    }
    if (data.user?.email) {
      localStorage.setItem('user_email', data.user.email)
    }
    pushToast('登录成功', 'success')
    setTimeout(() => router.push('/'), 800)
  } catch (e: any) {
    pushToast(`登录失败：${e.message || e}`, 'error')
  } finally {
    loading.value = false
  }
}

async function sendCode(email: string): Promise<void> {
  // 登录场景使用通用验证码API（需要用户存在）
  await apiSendVerificationCode({ contact: email }, isAdmin.value)
  pushToast('验证码已发送', 'success')
}
</script>

<style scoped>
</style>
