<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">审计日志</h2>

    <!-- 筛选栏 -->
    <div class="card bg-base-100 shadow-md mb-6">
      <div class="card-body p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="form-control">
            <label class="label">
              <span class="label-text">目标类型</span>
            </label>
            <select v-model="filters.targetType" class="select select-bordered" @change="handleFilterChange">
              <option :value="undefined">全部类型</option>
              <option value="merchant">商家</option>
              <option value="shop">店铺</option>
              <option value="product">商品</option>
              <option value="order">订单</option>
              <option value="user">用户</option>
              <option value="category">分类</option>
            </select>
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">操作类型</span>
            </label>
            <select v-model="filters.operationType" class="select select-bordered" @change="handleFilterChange">
              <option :value="undefined">全部操作</option>
              <option value="create">创建</option>
              <option value="update">更新</option>
              <option value="delete">删除</option>
              <option value="audit">审核</option>
              <option value="status">状态变更</option>
            </select>
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">目标ID</span>
            </label>
            <input
              v-model.number="filters.targetId"
              type="number"
              placeholder="目标对象ID"
              class="input input-bordered"
            />
          </div>
          <div class="form-control flex items-end">
            <button class="btn btn-primary" @click="handleFilterChange">搜索</button>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4 mt-4">
          <div class="form-control">
            <label class="label">
              <span class="label-text">开始时间</span>
            </label>
            <input
              v-model="filters.startTime"
              type="datetime-local"
              class="input input-bordered"
              @change="handleFilterChange"
            />
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">结束时间</span>
            </label>
            <input
              v-model="filters.endTime"
              type="datetime-local"
              class="input input-bordered"
              @change="handleFilterChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <span class="loading loading-spinner loading-lg"></span>
      <p class="mt-4 text-base-content/60">加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="alert alert-error">
      <span>{{ error }}</span>
      <button class="btn btn-sm" @click="loadLogs">重试</button>
    </div>

    <!-- 日志列表 -->
    <div v-else-if="logList.auditLogs && logList.auditLogs.length > 0" class="space-y-4">
      <div
        v-for="log in logList.auditLogs"
        :key="log.auditId"
        class="card bg-base-100 shadow-md"
      >
        <div class="card-body">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-4 mb-2">
                <h3 class="text-lg font-bold">{{ log.operationDesc || '操作' }}</h3>
                <div class="badge badge-outline">{{ log.targetType }}</div>
                <div class="badge badge-ghost">{{ log.operationType }}</div>
              </div>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-base-content/70">
                <div>
                  <span class="text-base-content/50">操作人：</span>
                  <span>{{ log.adminUserName || `ID: ${log.adminUserId}` }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">目标ID：</span>
                  <span>{{ log.targetId || '-' }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">操作时间：</span>
                  <span>{{ formatDate(log.createTime) }}</span>
                </div>
              </div>
            </div>
            <button class="btn btn-sm btn-primary" @click="viewLogDetail(log.auditId!)">
              查看详情
            </button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center items-center gap-2 mt-6">
        <button
          class="btn btn-sm"
          :disabled="logList.page === 1"
          @click="changePage(logList.page! - 1)"
        >
          上一页
        </button>
        <span class="text-base-content/60">
          第 {{ logList.page }} / {{ totalPages }} 页，共 {{ logList.total }} 条
        </span>
        <button
          class="btn btn-sm"
          :disabled="logList.page === totalPages"
          @click="changePage(logList.page! + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <p class="text-lg text-base-content/60">暂无审计日志</p>
    </div>

    <!-- 日志详情弹窗 -->
    <dialog ref="logDetailModal" class="modal">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">审计日志详情</h3>
        <div v-if="selectedLog" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-base-content/60">日志ID</p>
              <p class="font-semibold">{{ selectedLog.auditId }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">操作人</p>
              <p>{{ selectedLog.adminUserName || `ID: ${selectedLog.adminUserId}` }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">目标类型</p>
              <p>{{ selectedLog.targetType }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">目标ID</p>
              <p>{{ selectedLog.targetId || '-' }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">操作类型</p>
              <p>{{ selectedLog.operationType }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">操作时间</p>
              <p>{{ formatDate(selectedLog.createTime) }}</p>
            </div>
          </div>
          <div>
            <p class="text-sm text-base-content/60 mb-2">操作描述</p>
            <p class="p-3 bg-base-200 rounded-lg">{{ selectedLog.operationDesc || '-' }}</p>
          </div>
        </div>
        <div class="modal-action">
          <button class="btn btn-ghost" @click="closeLogDetailModal">关闭</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeLogDetailModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { apiGetAuditLogs, apiGetAuditLogDetail } from '../api/admin'
import type { AuditLogListVO, AuditLogVO } from '../api/generated-admin/models'

const loading = ref(false)
const error = ref<string | null>(null)
const logList = ref<AuditLogListVO>({ auditLogs: [], total: 0, page: 1, size: 20 })
const selectedLog = ref<AuditLogVO | null>(null)

const filters = ref({
  targetType: undefined as string | undefined,
  operationType: undefined as string | undefined,
  adminUserId: undefined as number | undefined,
  targetId: undefined as number | undefined,
  startTime: undefined as string | undefined,
  endTime: undefined as string | undefined
})

const logDetailModal = ref<HTMLDialogElement | null>(null)

const pageSize = 20
const totalPages = computed(() => {
  if (!logList.value.total || !logList.value.size) return 1
  return Math.ceil(logList.value.total / logList.value.size)
})

function formatDate(dateStr?: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

function handleFilterChange() {
  logList.value.page = 1
  loadLogs()
}

function changePage(page: number) {
  logList.value.page = page
  loadLogs()
}

async function loadLogs() {
  loading.value = true
  error.value = null
  try {
    const params: any = {
      page: logList.value.page,
      size: pageSize
    }
    if (filters.value.targetType) {
      params.targetType = filters.value.targetType
    }
    if (filters.value.operationType) {
      params.operationType = filters.value.operationType
    }
    if (filters.value.adminUserId !== undefined) {
      params.adminUserId = filters.value.adminUserId
    }
    if (filters.value.targetId !== undefined) {
      params.targetId = filters.value.targetId
    }
    if (filters.value.startTime) {
      params.startTime = new Date(filters.value.startTime).toISOString()
    }
    if (filters.value.endTime) {
      params.endTime = new Date(filters.value.endTime).toISOString()
    }
    logList.value = await apiGetAuditLogs(params)
  } catch (e: any) {
    error.value = e.message || '加载审计日志失败'
    console.error('Failed to load audit logs:', e)
  } finally {
    loading.value = false
  }
}

async function viewLogDetail(auditId: number) {
  try {
    selectedLog.value = await apiGetAuditLogDetail(auditId)
    logDetailModal.value?.showModal()
  } catch (e: any) {
    pushToast(`获取日志详情失败：${e.message || e}`, 'error')
  }
}

function closeLogDetailModal() {
  logDetailModal.value?.close()
  selectedLog.value = null
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
  loadLogs()
})
</script>

<style scoped>
</style>

