<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">用户管理</h2>

    <!-- 筛选栏 -->
    <div class="card bg-base-100 shadow-md mb-6">
      <div class="card-body p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="form-control">
            <label class="label">
              <span class="label-text">角色</span>
            </label>
            <select v-model="filters.roleId" class="select select-bordered" @change="handleFilterChange">
              <option :value="undefined">全部角色</option>
              <option :value="1">管理员</option>
              <option :value="2">普通用户</option>
              <option :value="3">商家</option>
            </select>
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">状态</span>
            </label>
            <select v-model="filters.status" class="select select-bordered" @change="handleFilterChange">
              <option :value="undefined">全部状态</option>
              <option :value="0">禁用</option>
              <option :value="1">正常</option>
            </select>
          </div>
          <div class="form-control">
            <label class="label">
              <span class="label-text">关键词搜索</span>
            </label>
            <input
              v-model="filters.keyword"
              type="text"
              placeholder="用户名、邮箱"
              class="input input-bordered"
              @keyup.enter="handleFilterChange"
            />
          </div>
          <div class="form-control flex items-end">
            <button class="btn btn-primary" @click="handleFilterChange">搜索</button>
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
      <button class="btn btn-sm" @click="loadUsers">重试</button>
    </div>

    <!-- 用户列表 -->
    <div v-else-if="userList.users && userList.users.length > 0" class="space-y-4">
      <div
        v-for="user in userList.users"
        :key="user.userId"
        class="card bg-base-100 shadow-md"
      >
        <div class="card-body">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-4 mb-2">
                <h3 class="text-xl font-bold">{{ user.userName }}</h3>
                <div class="badge badge-lg" :class="getStatusBadgeClass(user.status)">
                  {{ user.statusText || getStatusText(user.status) }}
                </div>
                <div class="badge badge-outline">{{ user.roleName || user.roleCode }}</div>
              </div>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-base-content/70">
                <div>
                  <span class="text-base-content/50">邮箱：</span>
                  <span>{{ user.email || '-' }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">手机：</span>
                  <span>{{ user.phoneNumber || '-' }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">订单数：</span>
                  <span>{{ user.orderCount || 0 }}</span>
                </div>
                <div>
                  <span class="text-base-content/50">创建时间：</span>
                  <span>{{ formatDate(user.createTime) }}</span>
                </div>
                <div v-if="user.merchantName">
                  <span class="text-base-content/50">商家：</span>
                  <span>{{ user.merchantName }}</span>
                </div>
              </div>
            </div>
            <div class="flex gap-2">
              <button class="btn btn-sm btn-primary" @click="viewUserDetail(user.userId!)">
                查看详情
              </button>
              <button
                v-if="user.status === 0"
                class="btn btn-sm btn-success"
                @click="updateUserStatus(user.userId!, 1)"
              >
                启用
              </button>
              <button
                v-else
                class="btn btn-sm btn-error"
                @click="updateUserStatus(user.userId!, 0)"
              >
                禁用
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center items-center gap-2 mt-6">
        <button
          class="btn btn-sm"
          :disabled="userList.page === 1"
          @click="changePage(userList.page! - 1)"
        >
          上一页
        </button>
        <span class="text-base-content/60">
          第 {{ userList.page }} / {{ totalPages }} 页，共 {{ userList.total }} 条
        </span>
        <button
          class="btn btn-sm"
          :disabled="userList.page === totalPages"
          @click="changePage(userList.page! + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <p class="text-lg text-base-content/60">暂无用户</p>
    </div>

    <!-- 用户详情弹窗 -->
    <dialog ref="userDetailModal" class="modal">
      <div class="modal-box max-w-4xl">
        <h3 class="font-bold text-lg mb-4">用户详情</h3>
        <div v-if="selectedUser" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-base-content/60">用户ID</p>
              <p class="font-semibold">{{ selectedUser.userId }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">用户名</p>
              <p class="font-semibold">{{ selectedUser.userName }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">邮箱</p>
              <p>{{ selectedUser.email || '-' }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">手机号</p>
              <p>{{ selectedUser.phoneNumber || '-' }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">角色</p>
              <p>{{ selectedUser.roleName || selectedUser.roleCode }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">状态</p>
              <div class="badge badge-lg mt-1" :class="getStatusBadgeClass(selectedUser.status)">
                {{ selectedUser.statusText || getStatusText(selectedUser.status) }}
              </div>
            </div>
            <div v-if="selectedUser.merchantName">
              <p class="text-sm text-base-content/60">商家</p>
              <p>{{ selectedUser.merchantName }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">订单数</p>
              <p>{{ selectedUser.orderCount || 0 }}</p>
            </div>
            <div>
              <p class="text-sm text-base-content/60">创建时间</p>
              <p>{{ formatDate(selectedUser.createTime) }}</p>
            </div>
          </div>
        </div>
        <div class="modal-action">
          <button class="btn btn-ghost" @click="closeUserDetailModal">关闭</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeUserDetailModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { apiGetUsers, apiGetUserDetail, apiUpdateUserStatus } from '../api/admin'
import type { UserListVO, UserVO } from '../api/generated-admin/models'

const loading = ref(false)
const error = ref<string | null>(null)
const userList = ref<UserListVO>({ users: [], total: 0, page: 1, size: 20 })
const selectedUser = ref<UserVO | null>(null)

const filters = ref({
  roleId: undefined as number | undefined,
  status: undefined as number | undefined,
  keyword: ''
})

const userDetailModal = ref<HTMLDialogElement | null>(null)

const pageSize = 20
const totalPages = computed(() => {
  if (!userList.value.total || !userList.value.size) return 1
  return Math.ceil(userList.value.total / userList.value.size)
})

function formatDate(dateStr?: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function getStatusText(status?: number): string {
  const statusMap: Record<number, string> = {
    0: '禁用',
    1: '正常'
  }
  return statusMap[status ?? -1] || '未知状态'
}

function getStatusBadgeClass(status?: number): string {
  const classMap: Record<number, string> = {
    0: 'badge-error',
    1: 'badge-success'
  }
  return classMap[status ?? -1] || 'badge-ghost'
}

function handleFilterChange() {
  userList.value.page = 1
  loadUsers()
}

function changePage(page: number) {
  userList.value.page = page
  loadUsers()
}

async function loadUsers() {
  loading.value = true
  error.value = null
  try {
    const params: any = {
      page: userList.value.page,
      size: pageSize
    }
    if (filters.value.roleId !== undefined) {
      params.roleId = filters.value.roleId
    }
    if (filters.value.status !== undefined) {
      params.status = filters.value.status
    }
    if (filters.value.keyword) {
      params.keyword = filters.value.keyword
    }
    userList.value = await apiGetUsers(params)
  } catch (e: any) {
    error.value = e.message || '加载用户列表失败'
    console.error('Failed to load users:', e)
  } finally {
    loading.value = false
  }
}

async function viewUserDetail(userId: number) {
  try {
    selectedUser.value = await apiGetUserDetail(userId)
    userDetailModal.value?.showModal()
  } catch (e: any) {
    pushToast(`获取用户详情失败：${e.message || e}`, 'error')
  }
}

function closeUserDetailModal() {
  userDetailModal.value?.close()
  selectedUser.value = null
}

async function updateUserStatus(userId: number, status: number) {
  const reason = prompt('请输入操作原因（可选）')
  if (!confirm(`确定要${status === 1 ? '启用' : '禁用'}该用户吗？`)) {
    return
  }
  try {
    await apiUpdateUserStatus(userId, status, reason || undefined)
    pushToast(`${status === 1 ? '启用' : '禁用'}成功`, 'success')
    await loadUsers()
  } catch (e: any) {
    pushToast(`操作失败：${e.message || e}`, 'error')
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
  loadUsers()
})
</script>

<style scoped>
</style>

