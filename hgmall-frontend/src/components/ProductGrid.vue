<template>
  <div class="mb-12">
    <!-- 标题 -->
    <h2 class="text-3xl font-bold text-center mb-6">探索新品</h2>
    
    <!-- 分类标签 -->
    <div class="flex justify-center gap-4 mb-8 overflow-x-auto pb-2">
      <button
        v-for="category in categoryList"
        :key="getCategoryKey(category)"
        @click="selectCategory(category)"
        class="px-4 py-2 font-medium transition-all whitespace-nowrap"
        :class="isActiveCategory(category)
          ? 'text-primary border-b-2 border-primary' 
          : 'text-base-content/60 hover:text-primary'"
      >
        {{ getCategoryName(category) }}
      </button>
    </div>
    
    <!-- 产品网格 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      <!-- 产品卡片 -->
      <slot name="products"></slot>
    </div>
    
    <!-- 查看更多 链接 -->
    <div class="text-center mt-8">
      <a 
        class="text-primary font-medium hover:underline text-lg cursor-pointer"
        @click="$router.push('/products')"
      >
        查看更多 >
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { CategoryProductsVO } from '../api/generated/models'

const props = defineProps<{
  categories: Array<CategoryProductsVO | string>
  activeCategoryId?: number | string
}>()

const emit = defineEmits<{
  (e: 'category-change', categoryId: number | string | null): void
}>()

const activeCategory = ref<number | string | null>(props.activeCategoryId || null)

// 监听 prop 变化，同步更新内部的 activeCategory
watch(() => props.activeCategoryId, (newValue) => {
  activeCategory.value = newValue || null
}, { immediate: true })

const categoryList = computed(() => props.categories || [])

function getCategoryName(category: CategoryProductsVO | string): string {
  if (typeof category === 'string') {
    return category
  }
  return category.categoryName || '分类'
}

function getCategoryId(category: CategoryProductsVO | string): number | string | null {
  if (typeof category === 'string') {
    return category
  }
  return category.categoryId || null
}

function getCategoryKey(category: CategoryProductsVO | string): string | number {
  if (typeof category === 'string') {
    return category
  }
  return category.categoryId || `category-${Math.random()}`
}

function isActiveCategory(category: CategoryProductsVO | string): boolean {
  const categoryId = getCategoryId(category)
  if (categoryId === null || categoryId === undefined) return false
  if (activeCategory.value === null || activeCategory.value === undefined) return false
  // 转换为字符串进行比较，避免类型不匹配问题
  return String(activeCategory.value) === String(categoryId)
}

function selectCategory(category: CategoryProductsVO | string) {
  const categoryId = getCategoryId(category)
  activeCategory.value = categoryId
  emit('category-change', categoryId)
}
</script>

<style scoped>
</style>
