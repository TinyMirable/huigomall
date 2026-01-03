<template>
  <dialog ref="modalRef" class="modal">
    <div class="modal-box max-w-md">
      <h3 class="font-bold text-lg mb-4">下架商品</h3>
      <div v-if="product" class="mb-4">
        <p class="font-semibold mb-2">商品：{{ product.name }}</p>
      </div>
      <form @submit.prevent="handleSubmit">
        <VerificationCodeInput
          ref="codeInputRef"
          v-model="form.code"
          :email="email"
          :send-code-fn="sendCodeFn"
          label="邮箱验证码"
          @code-send-error="handleCodeError"
        />
        <div class="modal-action">
          <button type="button" class="btn btn-ghost" @click="close">取消</button>
          <button type="submit" class="btn btn-warning" :class="{ 'btn-disabled': loading }">
            <span v-if="loading" class="loading loading-spinner loading-sm"></span>
            <span v-else>确认下架</span>
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
// import type { ShopProductVO } from '../api/generated/models'

const props = defineProps<{
  modelValue: boolean
  loading?: boolean
  product: { name?: string } | null
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










