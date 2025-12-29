<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 flex items-center justify-center py-12 px-4">
      <div class="card w-full max-w-md bg-base-100 shadow-xl">
        <div class="card-body">
        <div class="flex flex-col items-center mb-6">
          <h2 class="text-2xl font-bold">注册新账号</h2>
        </div>
        
        <form @submit.prevent="onSubmit">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">用户名</span>
            </label>
            <input
              v-model="username"
              type="text"
              placeholder="请输入用户名"
              class="input input-bordered"
              autocomplete="username"
            />
          </div>
          
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">邮箱</span>
            </label>
            <input
              v-model="email"
              type="email"
              placeholder="请输入邮箱"
              class="input input-bordered"
              autocomplete="email"
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
              autocomplete="new-password"
            />
          </div>
        
        <VerificationCodeInput
          v-model="code"
          :email="email"
          :send-code-fn="sendCode"
          hint="验证码将发送到您的邮箱"
          @enter="onSubmit"
          @code-send-error="(e) => pushToast(e.message, 'error')"
        />
        
          <button 
            type="submit"
            class="btn btn-primary w-full" 
            :class="{ 'btn-disabled': loading }"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>
        
        <div class="text-center mt-4">
          <a class="link link-primary" @click="$router.push('/login')">已有账号？去登录</a>
        </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { apiRegister, apiSendRegisterCode } from '../api/auth'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import VerificationCodeInput from '../components/VerificationCodeInput.vue'

const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const code = ref('')
const loading = ref(false)

function pushToast(message: string, type: 'success' | 'error' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : 'alert-error'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

async function sendCode(emailAddr: string): Promise<void> {
  // 注册场景使用注册验证码API（需要用户不存在）
  await apiSendRegisterCode({ contact: emailAddr })
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function onSubmit() {
  if (!username.value || !email.value || !password.value || !code.value) {
    pushToast('请填写所有必填项', 'error')
    return
  }
  loading.value = true
  try {
    const data = await apiRegister({
      username: username.value,
      password: password.value,
      email: email.value,
      code: code.value,
    })
    localStorage.setItem('access_token', data.access_token)
    if (data.user?.username) {
      localStorage.setItem('user_nickname', data.user.username)
    }
    if (data.user?.email) {
      localStorage.setItem('user_email', data.user.email)
    }
    pushToast('注册成功，已为您自动登录', 'success')
    setTimeout(() => router.push('/'), 800)
  } catch (e: any) {
    pushToast(`注册失败：${e.message || e}`, 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
</style>
