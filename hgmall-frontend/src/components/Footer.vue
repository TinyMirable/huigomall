<template>
  <footer class="bg-base-100 border-t border-base-300 mt-16">
    <div class="container mx-auto px-4 lg:px-8 py-12">
      <!-- Logo -->
      <div class="text-center mb-8">
        <a class="btn btn-ghost text-xl flex items-center gap-2 justify-center">
          <img :src="logoUrl" alt="HuiGoMall" class="w-8 h-8" />
          <span class="text-primary font-bold">HuiGoMall</span>
        </a>
      </div>
      
      <!-- 主要内容区域 -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
        <!-- 客户服务 -->
        <div>
          <h3 class="font-semibold text-lg mb-4">客户服务</h3>
          <ul class="space-y-2">
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">帮助中心</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">如何购买</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">支付方式</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">配送方式</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">退货退款</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">保修政策</a></li>
          </ul>
        </div>
        
        <!-- 关于我们 -->
        <div>
          <h3 class="font-semibold text-lg mb-4">关于我们</h3>
          <ul class="space-y-2">
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">联系我们</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">关于我们</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">隐私政策</a></li>
            <li><a class="link link-hover text-sm cursor-pointer" @click="showFeatureInDevelopment">商家中心</a></li>
          </ul>
        </div>
        
        <!-- 支付方式 -->
        <div>
          <h3 class="font-semibold text-lg mb-4">支付方式</h3>
          <div class="flex flex-wrap gap-3">
            <div class="badge badge-outline px-3 py-2">VISA</div>
            <div class="badge badge-outline px-3 py-2">Mastercard</div>
            <div class="badge badge-outline px-3 py-2">American Express</div>
            <div class="badge badge-outline px-3 py-2">PayPal</div>
            <div class="badge badge-outline px-3 py-2">微信支付</div>
            <div class="badge badge-outline px-3 py-2">支付宝</div>
          </div>
        </div>
        
        <!-- 下载应用 -->
        <div>
          <h3 class="font-semibold text-lg mb-4">下载应用</h3>
          <div class="bg-base-200 w-24 h-24 rounded flex items-center justify-center">
            <svg class="w-16 h-16 text-base-content/20" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h8a2 2 0 012 2v12a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm3 1h6v4H7V5zm6 6H7v2h6v-2z" clip-rule="evenodd" />
            </svg>
          </div>
        </div>
      </div>
      
      <!-- 底部栏 -->
      <div class="border-t border-base-300 pt-6">
        <div class="flex flex-col md:flex-row items-center justify-between gap-4">
          <!-- 语言选择(暂时不支持) -->
          <div class="dropdown">
            <div tabindex="0" role="button" class="btn btn-ghost btn-sm gap-2 cursor-pointer" @click="showFeatureInDevelopment">
              中文
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </div>
          </div>
          
          <!-- 版权信息 -->
          <div class="text-sm text-base-content/60 text-center">
            ©2026 HuiGoMall 版权所有
          </div>
          
          <!-- 社交媒体图标 -->
          <div class="flex gap-4">
            <a class="btn btn-ghost btn-circle btn-sm cursor-pointer" @click="showFeatureInDevelopment">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M8.29 20.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0022 5.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.072 4.072 0 012.8 9.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 012 18.407a11.616 11.616 0 006.29 1.84"/>
              </svg>
            </a>
            <a class="btn btn-ghost btn-circle btn-sm cursor-pointer" @click="showFeatureInDevelopment">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
              </svg>
            </a>
            <a class="btn btn-ghost btn-circle btn-sm cursor-pointer" @click="showFeatureInDevelopment">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
              </svg>
            </a>
            <a class="btn btn-ghost btn-circle btn-sm cursor-pointer" @click="showFeatureInDevelopment">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/>
              </svg>
            </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import logoUrl from '../assets/logo.png'

function pushToast(message: string, type: 'success' | 'error' | 'info' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : type === 'error' ? 'alert-error' : 'alert-info'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

function showFeatureInDevelopment() {
  pushToast('该功能还在开发中，敬请期待', 'info')
}
</script>

<style scoped>
</style>
