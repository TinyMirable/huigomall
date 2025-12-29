<template>
  <div class="card bg-base-100 shadow-md rounded-xl overflow-hidden relative cursor-pointer hover:shadow-lg transition-shadow" @click="handleClick">
    <!-- 折扣标签 -->
    <div v-if="saleOff" class="absolute top-2 left-2 z-10 bg-primary text-white px-2 py-1 rounded text-xs font-semibold">
      优惠 {{ saleOff }}%
    </div>
    <!-- 收藏图标 -->
    <button class="absolute top-2 right-2 z-10 btn btn-ghost btn-circle btn-sm" @click.stop>
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" :class="isFavorite ? 'text-error fill-current' : 'text-base-content'" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
      </svg>
    </button>
    <!-- 产品图片 -->
    <figure class="bg-base-200 h-48 flex items-center justify-center overflow-hidden">
      <slot name="image">
        <img 
          v-if="imageUrl" 
          :src="imageUrl" 
          :alt="name" 
          class="w-full h-full object-cover"
        />
        <div v-else class="w-full h-full flex items-center justify-center">
          <svg class="w-24 h-24 text-base-content/30" fill="currentColor" viewBox="0 0 20 20">
            <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
          </svg>
        </div>
      </slot>
    </figure>
    <!-- 产品信息 -->
    <div class="card-body p-4">
      <h3 class="card-title text-base font-semibold line-clamp-2 mb-2">{{ name }}</h3>
      
      <!-- 商家信息 -->
      <div v-if="merchantName" class="flex items-center gap-1.5 mb-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-base-content/50" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
        <span 
          v-if="shopId"
          class="text-xs text-base-content/60 truncate cursor-pointer hover:text-primary transition-colors"
          @click.stop="goToShop(shopId)"
        >
          {{ merchantName }}
        </span>
        <span v-else class="text-xs text-base-content/60 truncate">{{ merchantName }}</span>
      </div>
      
      <p class="text-lg font-bold mb-2" style="color: rgb(255, 202, 57);">¥{{ price }}</p>
      <div v-if="rating" class="flex items-center gap-1 mb-2">
        <svg v-for="i in rating" :key="i" class="w-4 h-4 text-warning" fill="currentColor" viewBox="0 0 20 20">
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
        <svg v-for="i in (5 - rating)" :key="i" class="w-4 h-4 text-base-300" fill="currentColor" viewBox="0 0 20 20">
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
      </div>
      <p v-if="itemsSold !== undefined" class="text-sm text-base-content/60">已售 {{ itemsSold }} 件</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const props = defineProps<{
  name: string
  price: number
  rating?: number
  itemsSold?: number
  saleOff?: string
  isFavorite?: boolean
  imageUrl?: string
  productId?: number
  merchantName?: string
  shopId?: number
}>()

const router = useRouter()

function handleClick() {
  if (props.productId) {
    router.push(`/product/${props.productId}`)
  }
}

function goToShop(shopId: number) {
  router.push(`/shop/${shopId}`)
}
</script>

<style scoped>
</style>
