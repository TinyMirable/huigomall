
<script setup lang="ts">
</script>

<template>
  <!-- 背景装饰圆，纯样式，无交互 -->
  <div class="pointer-events-none fixed inset-0 overflow-hidden z-0">
    <!-- 左下大圆 -->
    <div class="absolute left-6 bottom-6 w-48 h-48 rounded-full bg-yellow-400/90"></div>
    <!-- 右上大圆 -->
    <div class="absolute right-6 top-2 w-40 h-40 rounded-full bg-yellow-300"></div>
    <!-- 右上叠加小圆 -->
    <div class="absolute right-24 top-16 w-14 h-14 rounded-full bg-yellow-200"></div>
    <!-- 左下浅色叠加 -->
    <div class="absolute left-0 bottom-28 w-20 h-20 rounded-full bg-yellow-200/70"></div>
  </div>
</template>

<style scoped>
</style>


