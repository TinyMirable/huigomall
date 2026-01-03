<template>
  <div v-if="modelValue" class="modal modal-open">
    <div class="modal-box">
      <h3 class="font-bold text-lg mb-4">{{ editingAddress ? '编辑地址' : '添加地址' }}</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text">收货人姓名 <span class="text-error">*</span></span>
          </label>
          <input
            v-model="form.receiverName"
            type="text"
            placeholder="请输入收货人姓名"
            class="input input-bordered"
            required
            maxlength="50"
          />
        </div>
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text">收货人电话 <span class="text-error">*</span></span>
          </label>
          <input
            v-model="form.receiverPhone"
            type="tel"
            placeholder="请输入手机号"
            class="input input-bordered"
            required
            pattern="^1[3-9]\d{9}$"
          />
        </div>
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text">详细地址 <span class="text-error">*</span></span>
          </label>
          <textarea
            v-model="form.detail"
            class="textarea textarea-bordered h-24"
            placeholder="请输入详细地址"
            required
            maxlength="500"
          ></textarea>
        </div>
        <div class="form-control mb-6">
          <label class="label cursor-pointer justify-start gap-2">
            <input
              v-model="form.isDefault"
              type="checkbox"
              class="checkbox checkbox-primary"
            />
            <span class="label-text">设为默认地址</span>
          </label>
        </div>
        <div class="modal-action">
          <button type="button" class="btn btn-ghost" @click="close">取消</button>
          <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': loading }">
            {{ loading ? '提交中...' : '确认' }}
          </button>
        </div>
      </form>
    </div>
    <div class="modal-backdrop" @click="close"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { AddressVO } from '../api/generated/models/address-vo'

const props = defineProps<{
  modelValue: boolean
  loading?: boolean
  editingAddress?: AddressVO | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [data: { receiverName: string; receiverPhone: string; detail: string; isDefault: boolean }]
}>()

const form = ref({
  receiverName: '',
  receiverPhone: '',
  detail: '',
  isDefault: false
})

watch(() => props.modelValue, (open) => {
  if (open) {
    if (props.editingAddress) {
      form.value = {
        receiverName: props.editingAddress.receiverName || '',
        receiverPhone: props.editingAddress.receiverPhone || '',
        detail: props.editingAddress.detail || '',
        isDefault: props.editingAddress.isDefault || false
      }
    } else {
      form.value = {
        receiverName: '',
        receiverPhone: '',
        detail: '',
        isDefault: false
      }
    }
  }
})

watch(() => props.editingAddress, (address) => {
  if (address && props.modelValue) {
    form.value = {
      receiverName: address.receiverName || '',
      receiverPhone: address.receiverPhone || '',
      detail: address.detail || '',
      isDefault: address.isDefault || false
    }
  }
}, { deep: true })

function close() {
  emit('update:modelValue', false)
}

function handleSubmit() {
  if (!form.value.receiverName || !form.value.receiverPhone || !form.value.detail) {
    return
  }

  // 验证手机号格式
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(form.value.receiverPhone)) {
    return
  }

  emit('submit', { ...form.value })
}
</script>

<style scoped>
</style>










