<template>
  <div class="badge badge-lg" :class="badgeClass">
    {{ statusText }}
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  status?: number
}>()

const statusText = computed(() => {
  const statusMap: Record<number, string> = {
    1: '正常营业',
    2: '商家关闭',
    3: '管理员强制关闭'
  }
  return statusMap[props.status ?? -1] || '未知状态'
})

const badgeClass = computed(() => {
  const classMap: Record<number, string> = {
    1: 'badge-success',
    2: 'badge-warning',
    3: 'badge-error'
  }
  return classMap[props.status ?? -1] || 'badge-ghost'
})
</script>

<style scoped>
</style>



