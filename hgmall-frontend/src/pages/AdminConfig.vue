<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">系统配置</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <span class="loading loading-spinner loading-lg"></span>
      <p class="mt-4 text-base-content/60">加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="alert alert-error">
      <span>{{ error }}</span>
      <button class="btn btn-sm" @click="loadConfig">重试</button>
    </div>

    <!-- 配置内容 -->
    <div v-else-if="config" class="space-y-6">
      <div class="card bg-base-100 shadow-md">
        <div class="card-body">
          <h3 class="text-lg font-bold mb-4">审核配置</h3>
          
          <!-- 商家审核开关 -->
          <div class="flex items-center justify-between p-4 bg-base-200 rounded-lg mb-4">
            <div class="flex-1">
              <h4 class="font-semibold mb-1">商家审核开关</h4>
              <p class="text-sm text-base-content/60">
                开启后，商家申请需要通过管理员审核才能生效
              </p>
            </div>
            <input
              type="checkbox"
              class="toggle toggle-primary"
              :checked="config.merchantAuditEnabled"
              @change="updateMerchantAuditConfig($event.target.checked)"
              :disabled="updating"
            />
          </div>

          <!-- 店铺审核开关 -->
          <div class="flex items-center justify-between p-4 bg-base-200 rounded-lg">
            <div class="flex-1">
              <h4 class="font-semibold mb-1">店铺审核开关</h4>
              <p class="text-sm text-base-content/60">
                开启后，店铺创建需要通过管理员审核才能生效
              </p>
            </div>
            <input
              type="checkbox"
              class="toggle toggle-primary"
              :checked="config.shopAuditEnabled"
              @change="updateShopAuditConfig($event.target.checked)"
              :disabled="updating"
            />
          </div>
        </div>
      </div>

      <!-- 提示信息 -->
      <div class="alert alert-info">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span>系统配置修改后立即生效，请谨慎操作。</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  apiGetSystemConfig,
  apiUpdateMerchantAuditConfig,
  apiUpdateShopAuditConfig
} from '../api/admin'
import type { SystemConfigVO } from '../api/generated-admin/models'

const loading = ref(false)
const error = ref<string | null>(null)
const updating = ref(false)
const config = ref<SystemConfigVO | null>(null)

async function loadConfig() {
  loading.value = true
  error.value = null
  try {
    config.value = await apiGetSystemConfig()
  } catch (e: any) {
    error.value = e.message || '加载系统配置失败'
    console.error('Failed to load config:', e)
  } finally {
    loading.value = false
  }
}

async function updateMerchantAuditConfig(enabled: boolean) {
  updating.value = true
  try {
    await apiUpdateMerchantAuditConfig(enabled)
    pushToast(`商家审核${enabled ? '已开启' : '已关闭'}`, 'success')
    await loadConfig()
  } catch (e: any) {
    pushToast(`更新失败：${e.message || e}`, 'error')
    // 恢复原状态
    await loadConfig()
  } finally {
    updating.value = false
  }
}

async function updateShopAuditConfig(enabled: boolean) {
  updating.value = true
  try {
    await apiUpdateShopAuditConfig(enabled)
    pushToast(`店铺审核${enabled ? '已开启' : '已关闭'}`, 'success')
    await loadConfig()
  } catch (e: any) {
    pushToast(`更新失败：${e.message || e}`, 'error')
    // 恢复原状态
    await loadConfig()
  } finally {
    updating.value = false
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
  loadConfig()
})
</script>

<style scoped>
</style>



