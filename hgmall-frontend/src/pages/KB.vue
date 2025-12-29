<!-- code by 程柱豪(czh) -->
<script setup lang="ts">
import { onMounted, ref, computed, watch, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { apiCreateKb, apiUpdateKb, apiListKb, apiListKbDocs, apiRebuildIndex, apiDeleteKb, type KbDocItem, type KbItem, type RebuildIndexResponse } from '../api/kb'
import KbListPanel from '../components/kb/KbListPanel.vue'
import KbHeader from '../components/kb/KbHeader.vue'
import KbDocTable from '../components/kb/KbDocTable.vue'
import UploadPlaceholder from '../components/kb/UploadPlaceholder.vue'
import IndexStatusAlert from '../components/kb/IndexStatusAlert.vue'
import CreateKbModal from '../components/kb/CreateKbModal.vue'
import EditKbModal from '../components/kb/EditKbModal.vue'
import UploadDialog from '../components/kb/UploadDialog.vue'
import DeleteKbConfirmModal from '../components/kb/DeleteKbConfirmModal.vue'
import DocSearchInput from '../components/kb/DocSearchInput.vue'

const route = useRoute()

const search = ref('')
const kbList = ref<KbItem[]>([])
const activeKbId = ref<string>('')
const docs = ref<KbDocItem[]>([])
const loadingKb = ref(false)
const loadingDocs = ref(false)
const createModalOpen = ref(false)
const creating = ref(false)
const createForm = ref({ name: '', description: '' })
const editModalOpen = ref(false)
const updating = ref(false)
const editForm = ref({ name: '', description: '' })
const errorMessage = ref<string | null>(null)
const uploadOpen = ref(false)
const rebuilding = ref(false)
const rebuildResult = ref<RebuildIndexResponse | null>(null)
let rebuildResultTimer: ReturnType<typeof setTimeout> | null = null
const deleteConfirmOpen = ref(false)
const deleting = ref(false)

// 搜索逻辑下放到 KbListPanel 内部

const activeKb = computed(() => kbList.value.find(k => k.id === activeKbId.value))

// 文档格式化逻辑已在 KbDocTable 内部处理

async function loadKb() {
  loadingKb.value = true
  errorMessage.value = null
  try {
    const list = await apiListKb()
    kbList.value = list
    
    // 优先使用路由参数中的 kbId，否则使用列表第一个
    const routeKbId = route.query.kbId as string | undefined
    if (routeKbId && list.some(kb => kb.id === routeKbId)) {
      activeKbId.value = routeKbId
    } else {
      const first = list && list.length ? list[0] : undefined
      if (!activeKbId.value && first) activeKbId.value = first.id
    }
  } catch (e: any) {
    errorMessage.value = e?.message || '加载知识库失败'
  } finally {
    loadingKb.value = false
  }
}

// 监听路由参数变化，如果 kbId 改变则切换选中的知识库
watch(() => route.query.kbId, (newKbId) => {
  if (newKbId && typeof newKbId === 'string' && kbList.value.some(kb => kb.id === newKbId)) {
    if (activeKbId.value !== newKbId) {
      activeKbId.value = newKbId
      loadDocs()
    }
  }
})

async function loadDocs() {
  if (!activeKbId.value) return
  loadingDocs.value = true
  try {
    docs.value = await apiListKbDocs(activeKbId.value)
  } catch {
    docs.value = []
  } finally {
    loadingDocs.value = false
  }
}

function selectKb(id: string) {
  activeKbId.value = id
  loadDocs()
}

function openCreateModal() {
  createForm.value = { name: '', description: '' }
  createModalOpen.value = true
}

async function submitCreate() {
  if (!createForm.value.name.trim()) return
  creating.value = true
  try {
    await apiCreateKb({
      name: createForm.value.name.trim(),
      description: createForm.value.description.trim(),
    })
    createModalOpen.value = false
    await loadKb()
    await loadDocs()
  } finally {
    creating.value = false
  }
}

function openEditModal() {
  if (!activeKb.value) return
  editForm.value = { 
    name: activeKb.value.name || '', 
    description: activeKb.value.description || '' 
  }
  editModalOpen.value = true
}

async function submitEdit() {
  if (!activeKbId.value || !editForm.value.name.trim()) return
  updating.value = true
  errorMessage.value = null
  try {
    await apiUpdateKb(activeKbId.value, {
      name: editForm.value.name.trim(),
      description: editForm.value.description.trim(),
    })
    editModalOpen.value = false
    await loadKb()
  } catch (e: any) {
    errorMessage.value = e?.message || '更新知识库失败'
  } finally {
    updating.value = false
  }
}

async function rebuildIndex() {
  if (!activeKbId.value || rebuilding.value) return
  
  // 保存当前知识库ID，防止在索引过程中切换知识库导致状态混乱
  const currentKbId = activeKbId.value
  
  rebuilding.value = true
  rebuildResult.value = null
  errorMessage.value = null
  
  // 清除之前的定时器
  if (rebuildResultTimer) {
    clearTimeout(rebuildResultTimer)
    rebuildResultTimer = null
  }
  
  try {
    // 确保在整个索引过程中 rebuilding 状态保持为 true
    const result = await apiRebuildIndex(currentKbId)
    
    // 只有在当前知识库仍然是活动知识库时才更新结果
    if (activeKbId.value === currentKbId) {
      rebuildResult.value = result
      // 索引重建后重新加载文档列表
      await loadDocs()
      
      // 3秒后自动清除成功提示
      rebuildResultTimer = setTimeout(() => {
        rebuildResult.value = null
        rebuildResultTimer = null
      }, 3000)
    }
  } catch (e: any) {
    // 只有在当前知识库仍然是活动知识库时才显示错误
    if (activeKbId.value === currentKbId) {
      // 检查是否是超时或取消错误
      if (e?.name === 'AbortError' || e?.message?.includes('aborted')) {
        errorMessage.value = '索引操作超时，请稍后检查索引状态'
      } else {
        errorMessage.value = e?.message || '索引重建失败'
      }
    }
  } finally {
    // 只有在当前知识库仍然是活动知识库时才重置状态
    // 这确保即使索引过程中切换了知识库，也不会影响当前知识库的状态
    if (activeKbId.value === currentKbId) {
      rebuilding.value = false
    } else {
      // 如果切换了知识库，也要重置状态，避免状态残留
      rebuilding.value = false
    }
  }
}

onMounted(async () => {
  await loadKb()
  await loadDocs()
})

function handleEditFromList(kbId: string) {
  // 如果选中的知识库就是要编辑的，直接打开编辑弹窗
  if (activeKbId.value === kbId) {
    openEditModal()
  } else {
    // 否则先选中该知识库，然后打开编辑弹窗
    selectKb(kbId)
    // 等待选中后再打开编辑弹窗
    setTimeout(() => {
      openEditModal()
    }, 100)
  }
}

function handleDeleteFromList(kbId: string) {
  // 如果选中的知识库就是要删除的，直接打开删除确认
  if (activeKbId.value === kbId) {
    openDeleteConfirm()
  } else {
    // 否则先选中该知识库，然后打开删除确认
    selectKb(kbId)
    // 等待选中后再打开删除确认
    setTimeout(() => {
      openDeleteConfirm()
    }, 100)
  }
}

function openDeleteConfirm() {
  if (!activeKb.value) return
  deleteConfirmOpen.value = true
}

async function confirmDelete() {
  if (!activeKbId.value || deleting.value) return
  deleting.value = true
  errorMessage.value = null
  
  try {
    await apiDeleteKb(activeKbId.value)
    deleteConfirmOpen.value = false
    // 删除后重新加载知识库列表
    await loadKb()
    // 清空当前选中的知识库和文档列表
    activeKbId.value = ''
    docs.value = []
  } catch (e: any) {
    errorMessage.value = e?.message || '删除知识库失败'
  } finally {
    deleting.value = false
  }
}

onUnmounted(() => {
  // 组件卸载时清除定时器
  if (rebuildResultTimer) {
    clearTimeout(rebuildResultTimer)
    rebuildResultTimer = null
  }
})
</script>

<template>
  <div class="px-6 md:px-10 py-6">
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
      <!-- 左侧：知识库列表 -->
      <aside class="md:col-span-3">
        <KbListPanel
          :kb-list="kbList"
          :loading="loadingKb"
          :active-id="activeKbId"
          :search="search"
          @update:search="val => search = val"
          @select="selectKb"
          @create="openCreateModal"
          @edit="handleEditFromList"
          @delete="handleDeleteFromList"
        />
      </aside>

      <!-- 右侧：文档列表 -->
      <section class="md:col-span-9">
        <KbHeader :kb="activeKb" :docs-count="docs.length" @edit="openEditModal" @delete="openDeleteConfirm" />

        <div class="card bg-base-100 shadow-sm">
          <div class="card-body">
            <div class="flex items-center justify-between mb-2">
              <div class="text-base font-semibold">文档列表</div>
              <DocSearchInput v-model="search" />
            </div>

            <KbDocTable 
              :docs="docs" 
              :loading="loadingDocs" 
              :kb-id="activeKbId"
              @deleted="loadDocs"
            />

            <!-- 上传区域（仅 UI，占位，按要求不接上传接口） -->
            <div class="mt-6">
              <div class="flex items-center justify-between mb-2">
                <div class="text-base font-semibold">上传新文档</div>
                <button class="btn btn-warning btn-sm" @click="uploadOpen = true">选择文件</button>
              </div>
              <div @click="uploadOpen = true" class="cursor-pointer">
                <UploadPlaceholder />
              </div>
            </div>

            <!-- 索引状态 -->
            <div class="mt-6">
              <div class="text-base font-semibold mb-2">索引状态</div>
              <IndexStatusAlert 
                :rebuilding="rebuilding"
                :result="rebuildResult"
                :last-indexed-at="activeKb?.last_indexed_at"
                @rebuild="rebuildIndex"
              />
            </div>
          </div>
        </div>

        <div v-if="errorMessage" class="mt-4">
          <div class="alert alert-error text-sm"><span>{{ errorMessage }}</span></div>
        </div>
      </section>
    </div>
  </div>

  <!-- 创建知识库弹窗 -->
  <CreateKbModal
    v-model="createModalOpen"
    :creating="creating"
    @submit="(p) => { createForm = p; submitCreate() }"
  />

  <!-- 编辑知识库弹窗 -->
  <EditKbModal
    v-model="editModalOpen"
    :kb="activeKb"
    :updating="updating"
    @submit="(p) => { editForm = p; submitEdit() }"
  />

  <!-- 文件上传弹窗 -->
  <UploadDialog 
    v-model="uploadOpen" 
    :kb-id="activeKbId"
    @uploaded="loadDocs"
  />

  <!-- 删除确认弹窗 -->
  <DeleteKbConfirmModal
    v-model:open="deleteConfirmOpen"
    :kb-name="activeKb?.name"
    :deleting="deleting"
    @confirm="confirmDelete"
  />
</template>

<style scoped>
</style>
