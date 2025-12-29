<template>
  <dialog ref="modalRef" class="modal">
    <div class="modal-box max-w-md">
      <h3 class="font-bold text-lg mb-4">编辑店铺信息</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text">店铺名称 <span class="text-error">*</span></span>
          </label>
          <input
            v-model="form.name"
            type="text"
            placeholder="请输入店铺名称（2-100个字符）"
            class="input input-bordered"
            required
            maxlength="100"
          />
        </div>
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text">店铺描述（可选）</span>
          </label>
          <textarea
            v-model="form.description"
            placeholder="请输入店铺描述"
            class="textarea textarea-bordered h-24"
            maxlength="500"
          ></textarea>
        </div>
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
          <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': loading }">
            <span v-if="loading" class="loading loading-spinner loading-sm"></span>
            <span v-else>确认修改</span>
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
  'submit': [data: { name: string; description?: string; code: string }]
  'code-error': [error: Error]
}>()

const modalRef = ref<HTMLDialogElement | null>(null)
const codeInputRef = ref<InstanceType<typeof VerificationCodeInput> | null>(null)

const form = ref({
  name: '',
  description: '',
  code: ''
})

watch(() => props.modelValue, (open) => {
  if (open) {
    modalRef.value?.showModal()
    if (props.shop) {
      form.value = {
        name: props.shop.name || '',
        description: props.shop.description || '',
        code: ''
      }
    }
  } else {
    modalRef.value?.close()
  }
})

function close() {
  emit('update:modelValue', false)
  form.value = { name: '', description: '', code: '' }
}

function handleCodeError(error: Error) {
  emit('code-error', error)
}

function handleSubmit() {
  const trimmedName = form.value.name.trim()
  if (!trimmedName) {
    return
  }

  // 从输入框组件直接获取当前值，确保获取最新的验证码
  let code = form.value.code
  if (codeInputRef.value && typeof codeInputRef.value.getCurrentValue === 'function') {
    code = codeInputRef.value.getCurrentValue()
  }

  if (!code || !code.trim()) {
    return
  }

  emit('submit', {
    name: trimmedName,
    description: form.value.description?.trim(),
    code: code.trim()
  })
}

defineExpose({
  clearForm: () => {
    form.value = { name: '', description: '', code: '' }
    if (codeInputRef.value) {
      codeInputRef.value.clearCountdown()
    }
  }
})
</script>

<style scoped>
</style>


