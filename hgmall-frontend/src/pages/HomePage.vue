<template>
  <div class="min-h-screen bg-base-100">
    <!-- Header -->
    <Header />
    
    <!-- Main Content -->
    <main class="container mx-auto px-4 lg:px-8 py-8">
      <!-- Hero Banner -->
      <HeroBanner :top-sales-products="homePageData.topSalesProducts" />
      
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <span class="loading loading-spinner loading-lg"></span>
      </div>
      
      <!-- Error State -->
      <div v-else-if="error" class="alert alert-error">
        <span>{{ error }}</span>
      </div>
      
      <!-- Content -->
      <div v-else>
        <!-- Category Cards -->
        <!-- <div v-if="homePageData.categoryProducts && homePageData.categoryProducts.length > 0" class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
          <CategoryCard 
            v-for="(category, index) in homePageData.categoryProducts.slice(0, 3)" 
            :key="category.categoryId"
            :discount="(10 + index * 10).toString()" 
            :category="category.categoryName || '分类'"
            :bg-color="getCategoryBgColor(index)"
          >
            <template #image>
              <div :class="['w-20 h-20 rounded-lg flex items-center justify-center', getCategoryIconBg(index)]">
                <svg class="w-12 h-12" :class="getCategoryIconColor(index)" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
                </svg>
              </div>
            </template>
          </CategoryCard>
        </div>
         -->
        <!-- Product Grid with Category Selection -->
        <ProductGrid 
          :categories="homePageData.categoryProducts || []"
          :active-category-id="selectedCategoryId ?? undefined"
          @category-change="handleCategoryChange"
        >
          <template #bestseller-image>
            <div class="w-full h-32 bg-base-200 rounded-lg flex items-center justify-center">
              <svg class="w-20 h-20 text-primary/30" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3z"/>
              </svg>
            </div>
          </template>
          
          <template #products>
            <ProductCard
              v-for="product in currentCategoryProducts"
              :key="product.productId"
              :name="product.name || ''"
              :price="product.minPrice || 0"
              :items-sold="product.sales"
              :image-url="product.coverImageUrl"
              :product-id="product.productId"
              :merchant-name="product.merchantName"
              :shop-id="product.shopId"
            />
          </template>
        </ProductGrid>
      </div>
    </main>
    
    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Header from '../components/Header.vue'
import HeroBanner from '../components/HeroBanner.vue'
// import CategoryCard from '../components/CategoryCard.vue'
import ProductCard from '../components/ProductCard.vue'
import ProductGrid from '../components/ProductGrid.vue'
import Footer from '../components/Footer.vue'
import { apiGetHomePage } from '../api/product'
import type { HomePageVO } from '../api/generated/models'

const loading = ref(true)
const error = ref<string | null>(null)
const homePageData = ref<HomePageVO>({
  categoryProducts: [],
  topSalesProducts: []
})
const selectedCategoryId = ref<number | string | null>(null)


// const categoryBgColors = ['bg-purple-50', 'bg-pink-50', 'bg-orange-50']
// const categoryIconBgs = ['bg-base-content', 'bg-pink-100', 'bg-pink-200']
// const categoryIconColors = ['text-white', 'text-pink-500', 'text-pink-600']

// function getCategoryBgColor(index: number): string {
//   return categoryBgColors[index % categoryBgColors.length] || categoryBgColors[0]
// }

// function getCategoryIconBg(index: number): string {
//   return categoryIconBgs[index % categoryIconBgs.length] || categoryIconBgs[0]
// }

// function getCategoryIconColor(index: number): string {
//   return categoryIconColors[index % categoryIconColors.length] || categoryIconColors[0]
// }

const currentCategoryProducts = computed(() => {
  // 如果分类商品列表为空，使用销量Top商品作为默认展示
  if (!homePageData.value.categoryProducts || homePageData.value.categoryProducts.length === 0) {
    // 如果没有分类商品，但有Top销量商品，则显示Top销量商品
    if (homePageData.value.topSalesProducts && homePageData.value.topSalesProducts.length > 0) {
      return homePageData.value.topSalesProducts
    }
    return []
  }
  
  // 如果没有选择分类，默认显示第一个分类的商品
  if (selectedCategoryId.value === null || selectedCategoryId.value === undefined) {
    const firstCategory = homePageData.value.categoryProducts[0]
    if (firstCategory?.categoryId !== undefined && firstCategory?.categoryId !== null) {
      selectedCategoryId.value = firstCategory.categoryId
    }
    return firstCategory?.products || []
  }
  
  // 找到选中的分类（处理类型转换问题）
  const selectedCategory = homePageData.value.categoryProducts.find(
    cat => {
      const catId = cat.categoryId
      const selectedId = selectedCategoryId.value
      return catId !== undefined && catId !== null && 
             selectedId !== undefined && selectedId !== null &&
             String(catId) === String(selectedId)
    }
  )
  
  return selectedCategory?.products || []
})

function handleCategoryChange(categoryId: number | string | null) {
  selectedCategoryId.value = categoryId
}

async function loadHomePage() {
  loading.value = true
  error.value = null
  try {
    const data = await apiGetHomePage()
    homePageData.value = data
    // 默认选择第一个分类
    if (data.categoryProducts && data.categoryProducts.length > 0 && data.categoryProducts[0]) {
      selectedCategoryId.value = data.categoryProducts[0].categoryId ?? null
    }
  } catch (e: any) {
    error.value = e.message || '加载首页数据失败'
    console.error('Failed to load home page:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadHomePage()
})
</script>

<style scoped>
</style>
