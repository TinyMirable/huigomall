<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-2xl font-bold">分类管理</h2>
      <button class="btn btn-primary" @click="openCreateCategoryModal">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
        </svg>
        创建分类
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <span class="loading loading-spinner loading-lg"></span>
      <p class="mt-4 text-base-content/60">加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="alert alert-error">
      <span>{{ error }}</span>
      <button class="btn btn-sm" @click="loadCategories">重试</button>
    </div>

    <!-- 分类树 -->
    <div v-else-if="categories.length > 0" class="space-y-2">
      <div
        v-for="category in categories"
        :key="category.categoryId"
        class="card bg-base-100 shadow-md"
      >
        <div class="card-body">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-4">
              <h3 class="text-lg font-bold">{{ category.name }}</h3>
              <div class="badge badge-outline">ID: {{ category.categoryId }}</div>
              <div v-if="category.sortOrder !== undefined" class="badge badge-ghost">
                排序: {{ category.sortOrder }}
              </div>
            </div>
            <div class="flex gap-2">
              <button class="btn btn-sm btn-primary" @click="openEditCategoryModal(category)">
                编辑
              </button>
              <button class="btn btn-sm btn-error" @click="deleteCategory(category.categoryId!)">
                删除
              </button>
            </div>
          </div>
          <!-- 子分类 -->
          <div v-if="category.children && category.children.length > 0" class="mt-4 pl-6 border-l-2 border-base-300">
            <div
              v-for="child in category.children"
              :key="child.categoryId"
              class="py-2 border-b border-base-200 last:border-b-0"
            >
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-4">
                  <span class="text-base-content/70">{{ child.name }}</span>
                  <div class="badge badge-outline badge-sm">ID: {{ child.categoryId }}</div>
                  <div v-if="child.sortOrder !== undefined" class="badge badge-ghost badge-sm">
                    排序: {{ child.sortOrder }}
                  </div>
                </div>
                <div class="flex gap-2">
                  <button class="btn btn-xs btn-primary" @click="openEditCategoryModal(child)">
                    编辑
                  </button>
                  <button class="btn btn-xs btn-error" @click="deleteCategory(child.categoryId!)">
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <p class="text-lg text-base-content/60">暂无分类</p>
    </div>

    <!-- 创建/编辑分类弹窗 -->
    <dialog ref="categoryModal" class="modal">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">{{ isEditMode ? '编辑分类' : '创建分类' }}</h3>
        <form @submit.prevent="handleSubmit">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">父分类</span>
            </label>
            <select v-model="categoryForm.parentId" class="select select-bordered">
              <option :value="0">一级分类（无父分类）</option>
              <option
                v-for="cat in categories"
                :key="cat.categoryId"
                :value="cat.categoryId"
              >
                {{ cat.name }}
              </option>
            </select>
          </div>
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">分类名称 <span class="text-error">*</span></span>
            </label>
            <input
              v-model="categoryForm.name"
              type="text"
              placeholder="请输入分类名称"
              class="input input-bordered"
              required
            />
          </div>
          <!-- <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">排序序号</span>
            </label>
            <input
              v-model.number="categoryForm.sortOrder"
              type="number"
              placeholder="0"
              class="input input-bordered"
            />
          </div> -->
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="closeCategoryModal">取消</button>
            <button type="submit" class="btn btn-primary" :class="{ 'btn-disabled': submitting }">
              <span v-if="submitting" class="loading loading-spinner loading-sm"></span>
              <span v-else>{{ isEditMode ? '保存' : '创建' }}</span>
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeCategoryModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  apiGetCategoryTree,
  apiCreateCategory,
  apiUpdateCategory,
  apiDeleteCategory
} from '../api/admin'
import type { CategoryVO } from '../api/generated-admin/models'

const loading = ref(false)
const error = ref<string | null>(null)
const categories = ref<CategoryVO[]>([])
const submitting = ref(false)
const isEditMode = ref(false)
const editingCategoryId = ref<number | null>(null)

const categoryModal = ref<HTMLDialogElement | null>(null)
const categoryForm = ref({
  parentId: 0,
  name: '',
  sortOrder: 0
})

async function loadCategories() {
  loading.value = true
  error.value = null
  try {
    categories.value = await apiGetCategoryTree()
  } catch (e: any) {
    error.value = e.message || '加载分类失败'
    console.error('Failed to load categories:', e)
  } finally {
    loading.value = false
  }
}

function openCreateCategoryModal() {
  isEditMode.value = false
  editingCategoryId.value = null
  categoryForm.value = { parentId: 0, name: '', sortOrder: 0 }
  categoryModal.value?.showModal()
}

function openEditCategoryModal(category: CategoryVO) {
  isEditMode.value = true
  editingCategoryId.value = category.categoryId!
  categoryForm.value = {
    parentId: category.parentId || 0,
    name: category.name || '',
    sortOrder: category.sortOrder || 0
  }
  categoryModal.value?.showModal()
}

function closeCategoryModal() {
  categoryModal.value?.close()
  categoryForm.value = { parentId: 0, name: '', sortOrder: 0 }
  isEditMode.value = false
  editingCategoryId.value = null
}

async function handleSubmit() {
  if (!categoryForm.value.name.trim()) {
    pushToast('请输入分类名称', 'error')
    return
  }

  submitting.value = true
  try {
    if (isEditMode.value && editingCategoryId.value) {
      await apiUpdateCategory(editingCategoryId.value, {
        name: categoryForm.value.name.trim(),
        parentId: categoryForm.value.parentId
      })
      pushToast('更新分类成功', 'success')
    } else {
      await apiCreateCategory({
        parentId: categoryForm.value.parentId,
        name: categoryForm.value.name.trim(),
        sortOrder: categoryForm.value.sortOrder
      })
      pushToast('创建分类成功', 'success')
    }
    closeCategoryModal()
    await loadCategories()
  } catch (e: any) {
    pushToast(`操作失败：${e.message || e}`, 'error')
  } finally {
    submitting.value = false
  }
}

async function deleteCategory(categoryId: number) {
  if (!confirm('确定要删除该分类吗？如果该分类下有商品或子分类，将无法删除。')) {
    return
  }
  try {
    await apiDeleteCategory(categoryId)
    pushToast('删除分类成功', 'success')
    await loadCategories()
  } catch (e: any) {
    pushToast(`删除失败：${e.message || e}`, 'error')
  }
}

function pushToast(message: string, type: 'success' | 'error' | 'info' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : type === 'error' ? 'alert-error' : 'alert-info'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
</style>


