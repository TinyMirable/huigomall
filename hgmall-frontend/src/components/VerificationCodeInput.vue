<template>
  <div class="form-control">
    <label v-if="label" class="label">
      <span class="label-text">{{ label }}</span>
    </label>
    <div class="flex gap-2">
      <input
        ref="inputRef"
        :value="modelValue"
        @input="handleInput"
        @change="handleChange"
        @paste="handlePaste"
        type="text"
        :placeholder="placeholder"
        class="input input-bordered flex-1"
        :class="inputClass"
        :autocomplete="autocomplete"
        @keyup.enter="$emit('enter')"
      />
      <button
        type="button"
        class="btn btn-outline"
        :class="[
          { 'btn-disabled': loading || countdown > 0 },
          typeof props.buttonClass === 'string' ? props.buttonClass : props.buttonClass
        ]"
        @click="handleSendCode"
      >
        {{ countdown > 0 ? `${countdown}秒` : buttonText }}
      </button>
    </div>
    <label v-if="hint" class="label">
      <span class="label-text-alt text-base-content/60">{{ hint }}</span>
    </label>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'

interface Props {
  modelValue: string
  label?: string
  placeholder?: string
  hint?: string
  buttonText?: string
  inputClass?: string | Record<string, boolean>
  buttonClass?: string | Record<string, boolean>
  autocomplete?: string
  email?: string
  sendCodeFn?: (email: string) => Promise<void>
}

const props = withDefaults(defineProps<Props>(), {
  label: '验证码',
  placeholder: '请输入验证码',
  hint: '',
  buttonText: '获取验证码',
  autocomplete: 'one-time-code',
  email: '',
  sendCodeFn: undefined
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'enter': []
  'code-sent': []
  'code-send-error': [error: Error]
}>()

const loading = ref(false)
const countdown = ref(0)
const countdownTimer = ref<number | null>(null)
const inputRef = ref<HTMLInputElement | null>(null)

// 处理输入事件
function handleInput(event: Event) {
  const target = event.target as HTMLInputElement
  const value = target.value
  emit('update:modelValue', value)
}

// 处理change事件（用于浏览器自动填充等情况）
function handleChange(event: Event) {
  const target = event.target as HTMLInputElement
  const value = target.value
  if (value !== props.modelValue) {
    emit('update:modelValue', value)
  }
}

// 处理粘贴事件
function handlePaste(event: ClipboardEvent) {
  // 让浏览器先处理粘贴，然后在下一个tick中更新值
  setTimeout(() => {
    const target = event.target as HTMLInputElement
    const value = target.value
    if (value !== props.modelValue) {
      emit('update:modelValue', value)
    }
  }, 0)
}

// 清除倒计时
function clearCountdown() {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
  }
  countdown.value = 0
}

// 获取当前输入框的值（确保获取最新值）
function getCurrentValue(): string {
  return inputRef.value?.value || props.modelValue
}

// 暴露清除倒计时的方法和获取当前值的方法，供父组件调用
defineExpose({
  clearCountdown,
  getCurrentValue
})

async function handleSendCode() {
  if (!props.sendCodeFn) {
    console.warn('VerificationCodeInput: sendCodeFn is not provided')
    return
  }

  const email = props.email
  if (!email) {
    emit('code-send-error', new Error('邮箱地址不能为空'))
    return
  }

  // 验证邮箱格式
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailPattern.test(email)) {
    emit('code-send-error', new Error('请输入正确的邮箱地址'))
    return
  }

  loading.value = true
  try {
    await props.sendCodeFn(email)
    emit('code-sent')
    // 清除之前的倒计时
    clearCountdown()
    // 开始新的倒计时
    countdown.value = 60
    countdownTimer.value = window.setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearCountdown()
      }
    }, 1000)
  } catch (e: any) {
    emit('code-send-error', e instanceof Error ? e : new Error(String(e)))
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  clearCountdown()
})
</script>

