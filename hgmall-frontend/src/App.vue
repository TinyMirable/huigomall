
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'


const route = useRoute()
// const hideNavOnLogin = computed(() => route.name === 'login')
const hideFooterOnChat = computed(() => route.name === 'chat')
</script>

<template>
  <div :class="['bg-base-200 flex flex-col', hideFooterOnChat ? 'h-screen overflow-hidden' : 'min-h-screen']">
    <!-- <NavBar v-if="!hideNavOnLogin" /> -->
    <main :class="['bg-white', hideFooterOnChat ? 'flex-1 overflow-hidden' : 'flex-1']">
      <router-view />
    </main>
    <!-- <FooterBar v-if="!hideFooterOnChat" /> -->
  </div>
  <!-- toast 容器 -->
  <div class="toast toast-top toast-center fixed left-1/2 -translate-x-1/2 top-2 z-50" id="global-toast"></div>
</template>

<style scoped>
</style>
