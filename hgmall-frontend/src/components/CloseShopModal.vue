<template>
  <dialog ref="modalRef" class="modal">
    <div class="modal-box max-w-md">
      <h3 class="font-bold text-lg mb-4 text-error">关闭店铺</h3>
      <div class="alert alert-warning mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <div>
          <h4 class="font-bold">警告</h4>
          <p class="text-sm">关闭店铺后，该店铺下的所有商品将自动下架，未支付的订单将被取消。您可以稍后重新开启店铺。</p>
        </div>
      </div>
      <div v-if="shop" class="mb-4 p-4 bg-base-200 rounded-lg">
        <p class="font-semibold mb-2">店铺信息：</p>
        <p>店铺名称：{{ shop.name }}</p>
        <p class="text-sm text-base-content/60">店铺ID：{{ shop.shopId }}</p>
      </div>
      <form @submit.prevent="handleSubmit">
        <VerificationCodeInput
          ref="codeInputRef"
          v-model="form.code"
          :email="email"
          :send-code-fn="sendCodeFn"
          label="邮箱验证码"
          hint="验证码将发送到您的注册邮箱"
          @code-send-error="handleCodeError"
        />
        <div class="modal-action">
          <button type="button" class="btn btn-ghost" @click="close">取消</button>
          <button type="submit" class="btn btn-error" :class="{ 'btn-disabled': loading }">
            <span v-if="loading" class="loading loading-spinner loading-sm"></span>
            <span v-else>确认关闭</span>
          </button>
        </div>
      </form>
    </div>
    <form method="dialog" class="modal-backdrop">
      <button @click="close">关闭</button>
    </form>
  </dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import VerificationCodeInput from './VerificationCodeInput.vue'
import type { ShopVO } from '../api/generated/models'

const props = defineProps<{
  modelValue: boolean
  loading?: boolean
  shop: ShopVO | null
  email: string
  sendCodeFn: (email: string) => Promise<void>
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [code: string]
  'code-error': [error: Error]
}>()

const modalRef = ref<HTMLDialogElement | null>(null)
const codeInputRef = ref<InstanceType<typeof VerificationCodeInput> | null>(null)

const form = ref({
  code: ''
})

watch(() => props.modelValue, (open) => {
  if (open) {
    modalRef.value?.showModal()
    form.value = { code: '' }
  } else {
    modalRef.value?.close()
  }
})

function close() {
  emit('update:modelValue', false)
  form.value = { code: '' }
}

function handleCodeError(error: Error) {
  emit('code-error', error)
}

function handleSubmit() {
  // 从输入框组件直接获取当前值，确保获取最新的验证码
  let code = form.value.code
  if (codeInputRef.value && typeof codeInputRef.value.getCurrentValue === 'function') {
    code = codeInputRef.value.getCurrentValue()
  }

  if (!code || !code.trim()) {
    return
  }

  emit('submit', code.trim())
}

defineExpose({
  clearForm: () => {
    form.value = { code: '' }
    if (codeInputRef.value) {
      codeInputRef.value.clearCountdown()
    }
  }
})
</script>

<style scoped>
</style>


