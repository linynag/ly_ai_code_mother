<template>
  <a-layout-header class="global-header">
    <div class="header-container">
      <!-- Logo和标题区域 -->
      <div class="logo-section">
        <img src="/logo.svg" alt="Logo" class="logo" />
        <h1 class="site-title">零代码生成平台</h1>
      </div>
      
      <!-- 导航菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        class="nav-menu"
        :items="menuItems"
        @click="handleMenuClick"
      />
      
      <!-- 用户区域 -->
      <div class="user-section">
        <a-button type="primary" @click="handleLogin">
          登录
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedKeys = ref<string[]>(['home'])

// 菜单配置项
const menuItems = ref([
  {
    key: 'home',
    label: '首页',
    path: '/'
  },
  {
    key: 'products',
    label: '产品',
    path: '/products'
  },
  {
    key: 'about',
    label: '关于我们',
    path: '/about'
  },
  {
    key: 'contact',
    label: '联系我们',
    path: '/contact'
  }
])

// 菜单点击事件
const handleMenuClick = ({ key }: { key: string }) => {
  const menuItem = menuItems.value.find(item => item.key === key)
  if (menuItem && menuItem.path) {
    router.push(menuItem.path)
  }
}

// 登录按钮点击事件
const handleLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.global-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
  line-height: 64px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 32px;
  width: auto;
}

.site-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
  margin: 0 24px;
}

.user-section {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }
  
  .site-title {
    font-size: 16px;
  }
  
  .nav-menu {
    margin: 0 12px;
  }
}

@media (max-width: 576px) {
  .site-title {
    display: none;
  }
  
  .nav-menu {
    margin: 0 8px;
  }
}
</style>