<template>
  <div class="relative bg-warning rounded-2xl p-8 lg:p-12 mb-8 overflow-hidden">
    <div class="flex flex-col lg:flex-row items-center justify-between relative z-10">
      <!-- 左侧内容 -->
      <div class="flex-1 mb-6 lg:mb-0">
        <div class="relative mb-4">
          <h1 class="text-4xl lg:text-5xl font-bold text-base-content">限时抢购</h1>
        </div>
        <p class="text-2xl lg:text-3xl font-semibold text-base-content mb-6">{{ todayDate }}</p>
        <button class="btn btn-primary gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          立即购买
        </button>
      </div>
      <!-- 中间产品图片轮播 -->
      <div class="flex-1 flex justify-center items-center relative min-h-[200px] lg:min-h-[300px]">
        <div 
          v-for="(product, index) in displayProducts" 
          :key="product.productId"
          v-show="currentIndex === index"
          class="absolute inset-0 flex justify-center items-center transition-opacity duration-500 cursor-pointer"
          :class="currentIndex === index ? 'opacity-100' : 'opacity-0'"
          @click="goToProductDetail(product.productId!)"
        >
          <div class="relative">
            <!-- 评分气泡 -->
            <div class="absolute -top-8 -left-4 bg-white rounded-full px-4 py-1 shadow-lg flex items-center gap-1 z-10">
              <svg v-for="i in 5" :key="i" class="w-4 h-4 text-warning" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
            </div>
            <!-- 商品图片 - 更大更醒目 -->
            <div class="w-48 h-48 sm:w-56 sm:h-56 md:w-64 md:h-64 lg:w-80 lg:h-80 bg-base-200 rounded-xl overflow-hidden shadow-2xl">
              <img 
                v-if="product.coverImageUrl" 
                :src="product.coverImageUrl" 
                :alt="product.name"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center bg-base-300">
                <svg class="w-32 h-32 text-base-content/30" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 分页指示器 -->
    <div class="flex justify-center gap-2 mt-6">
      <button
        v-for="(product, index) in displayProducts"
        :key="product.productId"
        @click="currentIndex = index"
        class="w-2 h-2 rounded-full transition-all"
        :class="currentIndex === index ? 'bg-primary w-6' : 'bg-base-300'"
      ></button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import type { ProductVO } from '../api/generated/models'

const router = useRouter()

const props = defineProps<{
  topSalesProducts?: ProductVO[]
}>()

const currentIndex = ref(0)
let autoPlayTimer: number | null = null

// 获取今日日期，格式为 MM.DD
const todayDate = computed(() => {
  const today = new Date()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${month}.${day}`
})

const displayProducts = computed(() => {
  if (!props.topSalesProducts || props.topSalesProducts.length === 0) {
    return []
  }
  // 只显示前3个热销商品
  return props.topSalesProducts.slice(0, 3)
})

function startAutoPlay() {
  if (displayProducts.value.length <= 1) return
  stopAutoPlay() // 确保没有重复的定时器
  autoPlayTimer = window.setInterval(() => {
    currentIndex.value = (currentIndex.value + 1) % displayProducts.value.length
  }, 3000) // 每3秒切换一次
}

function stopAutoPlay() {
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer)
    autoPlayTimer = null
  }
}

// 监听商品数据变化，重新启动轮播
watch(displayProducts, (newProducts) => {
  if (newProducts.length > 0) {
    startAutoPlay()
  } else {
    stopAutoPlay()
  }
}, { immediate: true })

onMounted(() => {
  // 确保有商品数据后再启动轮播
  if (displayProducts.value.length > 0) {
    startAutoPlay()
  }
})

function goToProductDetail(productId: number) {
  router.push(`/product/${productId}`)
}

onUnmounted(() => {
  stopAutoPlay()
})
</script>

<style scoped>
</style>
