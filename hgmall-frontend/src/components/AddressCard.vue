<template>
  <div
    class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow"
    :class="{ 'border-2 border-primary': address.isDefault }"
  >
    <div class="card-body">
      <div class="flex items-start justify-between">
        <div class="flex-1">
          <div class="flex items-center gap-2 mb-2">
            <span class="font-semibold text-lg">{{ address.receiverName }}</span>
            <span class="text-base-content/60">{{ address.receiverPhone }}</span>
            <span v-if="address.isDefault" class="badge badge-primary badge-sm">默认</span>
          </div>
          <p class="text-base-content/80">{{ address.detail }}</p>
        </div>
        <div class="flex gap-2">
          <button
            v-if="!address.isDefault"
            class="btn btn-ghost btn-sm"
            @click="$emit('set-default', address.addressId!)"
            title="设为默认"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
            </svg>
            设为默认
          </button>
          <button
            class="btn btn-ghost btn-sm"
            @click="$emit('edit', address)"
            title="编辑"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
            </svg>
            编辑
          </button>
          <button
            class="btn btn-ghost btn-sm text-error"
            @click="$emit('delete', address.addressId!)"
            title="删除"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
            删除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AddressVO } from '../api/generated/models/address-vo'

defineProps<{
  address: AddressVO
}>()

defineEmits<{
  'set-default': [addressId: number]
  'edit': [address: AddressVO]
  'delete': [addressId: number]
}>()
</script>

<style scoped>
</style>










