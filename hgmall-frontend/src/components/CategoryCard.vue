<template>
  <div class="card shadow-md rounded-xl overflow-hidden" :class="bgColor || 'bg-base-100'">
    <div class="card-body p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-2xl font-bold text-primary mb-1">{{ discount }}% OFF</p>
          <p class="text-xl font-semibold text-primary mb-3">{{ category }}</p>
          <a class="text-primary font-medium hover:underline">Shop now →</a>
        </div>
        <div class="w-24 h-24 flex items-center justify-center flex-shrink-0">
          <slot name="image"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  discount: string
  category: string
  bgColor?: string
}>()
</script>

<style scoped>
</style>
