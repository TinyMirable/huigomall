<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-4xl">
        <!-- 标题和添加按钮 -->
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-3xl font-bold">收货地址管理</h1>
          <button class="btn btn-primary" @click="openAddModal">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
            添加地址
          </button>
        </div>

        <!-- 地址列表 -->
        <div v-if="loading" class="text-center py-8">
          <span class="loading loading-spinner loading-lg"></span>
        </div>
        <div v-else-if="addresses.length === 0" class="text-center py-12">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-base-content/20 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <p class="text-base-content/60 text-lg">暂无收货地址</p>
          <button class="btn btn-primary mt-4" @click="openAddModal">添加第一个地址</button>
        </div>
        <div v-else class="space-y-4">
          <AddressCard
            v-for="address in addresses"
            :key="address.addressId"
            :address="address"
            @set-default="setDefaultAddress"
            @edit="openEditModal"
            @delete="confirmDelete"
          />
        </div>
      </div>
    </div>
    <Footer />

    <!-- 添加/编辑地址弹窗 -->
    <AddressModal
      v-model="showAddressModal"
      :loading="submitting"
      :editing-address="editingAddress"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import AddressCard from '../components/AddressCard.vue'
import AddressModal from '../components/AddressModal.vue'
import { apiGetAddressList, apiCreateAddress, apiUpdateAddress, apiDeleteAddress, apiSetDefaultAddress, type CreateAddressRequest, type UpdateAddressRequest } from '../api/address'
import type { AddressVO } from '../api/generated/models/address-vo'

const addresses = ref<AddressVO[]>([])
const loading = ref(false)
const submitting = ref(false)
const showAddressModal = ref(false)
const editingAddress = ref<AddressVO | null>(null)

function pushToast(message: string, type: 'success' | 'error' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : 'alert-error'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

async function loadAddresses() {
  loading.value = true
  try {
    addresses.value = await apiGetAddressList()
  } catch (e: any) {
    pushToast(`加载地址列表失败：${e.message || e}`, 'error')
  } finally {
    loading.value = false
  }
}

function openAddModal() {
  editingAddress.value = null
  showAddressModal.value = true
}

function openEditModal(address: AddressVO) {
  editingAddress.value = address
  showAddressModal.value = true
}

async function handleSubmit(data: { receiverName: string; receiverPhone: string; detail: string; isDefault: boolean }) {
  submitting.value = true
  try {
    if (editingAddress.value) {
      // 更新地址
      const updateRequest: UpdateAddressRequest = {
        detail: data.detail,
        receiverName: data.receiverName,
        receiverPhone: data.receiverPhone,
        isDefault: data.isDefault
      }
      await apiUpdateAddress(editingAddress.value.addressId!, updateRequest)
      pushToast('地址更新成功', 'success')
    } else {
      // 创建地址
      const createRequest: CreateAddressRequest = {
        detail: data.detail,
        receiverName: data.receiverName,
        receiverPhone: data.receiverPhone,
        isDefault: data.isDefault
      }
      await apiCreateAddress(createRequest)
      pushToast('地址添加成功', 'success')
    }
    showAddressModal.value = false
    editingAddress.value = null
    await loadAddresses()
  } catch (e: any) {
    pushToast(`${editingAddress.value ? '更新' : '添加'}失败：${e.message || e}`, 'error')
  } finally {
    submitting.value = false
  }
}

async function setDefaultAddress(addressId: number) {
  try {
    await apiSetDefaultAddress(addressId)
    pushToast('设置默认地址成功', 'success')
    await loadAddresses()
  } catch (e: any) {
    pushToast(`设置失败：${e.message || e}`, 'error')
  }
}

function confirmDelete(addressId: number) {
  if (confirm('确定要删除这个地址吗？')) {
    deleteAddress(addressId)
  }
}

async function deleteAddress(addressId: number) {
  try {
    await apiDeleteAddress(addressId)
    pushToast('地址删除成功', 'success')
    await loadAddresses()
  } catch (e: any) {
    pushToast(`删除失败：${e.message || e}`, 'error')
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
</style>

