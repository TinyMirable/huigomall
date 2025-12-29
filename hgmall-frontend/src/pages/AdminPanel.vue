<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 flex">
      <!-- 左侧菜单栏 -->
      <div class="w-64 bg-base-200 flex-shrink-0 border-r border-base-300">
        <div class="p-4 border-b border-base-300">
          <h2 class="text-xl font-bold">管理员面板</h2>
        </div>
        <div class="p-2">
          <ul class="menu menu-vertical w-full">
            
            <li>
              <a
                :class="{ 'active': currentView === 'users' }"
                @click="currentView = 'users'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
                用户管理
              </a>
            </li>
            <li>
              <a
                :class="{ 'active': currentView === 'shops' }"
                @click="currentView = 'shops'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
                店铺管理
              </a>
            </li>
            <li>
              <a
                :class="{ 'active': currentView === 'categories' }"
                @click="currentView = 'categories'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
                分类管理
              </a>
            </li>
          
            <li>
              <a
                :class="{ 'active': currentView === 'orders' }"
                @click="currentView = 'orders'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
                订单管理
              </a>
            </li>
            <li>
              <a
                :class="{ 'active': currentView === 'audit-logs' }"
                @click="currentView = 'audit-logs'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                审计日志
              </a>
            </li>
            <!-- 没有实现先不展示 -->
            <!-- <li>
              <a
                :class="{ 'active': currentView === 'config' }"
                @click="currentView = 'config'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                系统配置
              </a>
            </li> -->
          </ul>
        </div>
      </div>

      <!-- 右侧内容区域 -->
      <div class="flex-1 overflow-auto">
        <div class="p-6">
          <AdminUsers v-if="currentView === 'users'" />
          <AdminCategories v-if="currentView === 'categories'" />
          <AdminShop v-if="currentView === 'shops'" />
          <AdminOrders v-if="currentView === 'orders'" />
          <AdminAuditLogs v-if="currentView === 'audit-logs'" />
          <AdminConfig v-if="currentView === 'config'" />
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import AdminUsers from './AdminUsers.vue'
import AdminCategories from './AdminCategories.vue'
import AdminShop from './AdminShop.vue'
import AdminOrders from './AdminOrders.vue'
import AdminAuditLogs from './AdminAuditLogs.vue'
import AdminConfig from './AdminConfig.vue'

const route = useRoute()
const currentView = ref<'users' | 'categories' | 'shops' | 'orders' | 'audit-logs' | 'config'>('users')

// 根据路由参数设置当前视图
if (route.query.view) {
  const view = route.query.view as string
  if (['users', 'categories', 'shops', 'orders', 'audit-logs', 'config'].includes(view)) {
    currentView.value = view as any
  }
}
</script>

<style scoped>
</style>

