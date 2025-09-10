<template>
  <div id="app">
    <div class="app-content">
      <router-view />
    </div>
    <GlobalFooter />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from './stores/user'
import GlobalFooter from './components/GlobalFooter.vue'

const userStore = useUserStore()

onMounted(async () => {
  // 应用启动时尝试获取当前用户信息
  await userStore.getCurrentUser()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-content {
  flex: 1;
}
</style>
