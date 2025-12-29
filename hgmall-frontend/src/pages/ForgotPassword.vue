<template>
  <div class="min-h-screen flex items-center justify-center bg-base-100 py-12 px-4">
    <div class="card w-full max-w-md bg-base-100 shadow-xl">
      <div class="card-body">
        <div class="flex flex-col items-center mb-6">
          <h2 class="text-2xl font-bold">忘记密码</h2>
          <p class="text-sm text-base-content/60 mt-2">请输入您的邮箱，我们将发送验证码帮您重置密码</p>
        </div>

        <form @submit.prevent="onSubmit">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">邮箱</span>
            </label>
            <input
              v-model="email"
              type="email"
              placeholder="请输入注册邮箱"
              class="input input-bordered"
              autocomplete="email"
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

          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">新密码</span>
            </label>
            <input
              v-model="newPassword"
              type="password"
              placeholder="请输入新密码"
              class="input input-bordered"
              autocomplete="new-password"
              @keyup.enter="onSubmit"
            />
          </div>

          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">确认密码</span>
            </label>
            <input
              v-model="confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              class="input input-bordered"
              autocomplete="new-password"
              @keyup.enter="onSubmit"
            />
          </div>

          <button
            type="submit"
            class="btn btn-primary w-full"
            :class="{ 'btn-disabled': loading }"
          >
            {{ loading ? '重置中...' : '重置密码' }}
          </button>
        </form>

        <div class="text-center mt-4">
          <a class="link link-primary" @click="$router.push('/login')">返回登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { apiForgotPassword, apiSendVerificationCode } from '../api/auth'
import VerificationCodeInput from '../components/VerificationCodeInput.vue'

const router = useRouter()

const email = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
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
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送到您的邮箱', 'success')
}

async function onSubmit() {
  if (!email.value || !code.value || !newPassword.value || !confirmPassword.value) {
    pushToast('请填写所有必填项', 'error')
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    pushToast('两次输入的密码不一致', 'error')
    return
  }

  if (newPassword.value.length < 6) {
    pushToast('密码长度至少为6位', 'error')
    return
  }

  loading.value = true
  try {
    await apiForgotPassword({
      email: email.value,
      code: code.value,
      newPassword: newPassword.value,
    })
    pushToast('密码重置成功，请使用新密码登录', 'success')
    setTimeout(() => router.push('/login'), 1500)
  } catch (e: any) {
    pushToast(`重置失败：${e.message || e}`, 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
</style>

