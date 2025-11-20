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
        <!-- 已登录状态 -->
        <div v-if="loginUserStore.isLoggedIn()" class="user-info">
          <a-dropdown>
            <a-button type="text" class="user-dropdown-btn">
              <a-avatar :src="loginUserStore.loginUser?.userAvatar" size="small" />
              <span class="user-name">{{ loginUserStore.loginUser?.userName || '用户' }}</span>
              <down-outlined />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="handleProfile">
                  <user-outlined />
                  个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <logout-outlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>

        <!-- 未登录状态 -->
        <div v-else class="login-section">
          <span class="login-status">未登录</span>
          <a-button type="primary" @click="handleLogin">
            登录
          </a-button>
        </div>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  DownOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogout } from '@/api/userController'
import { hasAdminPermission } from '@/utils/auth'

// 获取登录用户信息
const loginUserStore = useLoginUserStore()

const router = useRouter()
const selectedKeys = ref<string[]>(['home'])

// 菜单配置项
  const menuItems = computed(() => {
    const baseMenus = [
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
    ]

    // 如果用户是管理员，添加用户管理菜单
    if (hasAdminPermission()) {
      baseMenus.push({
        key: 'userManagement',
        label: '用户管理',
        path: '/user-management'
      })
      baseMenus.push({
        key: 'adminTest',
        label: '管理员测试',
        path: '/admin-test'
      })
    }

    return baseMenus
  })

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

// 个人中心点击事件
const handleProfile = () => {
  message.info('个人中心功能开发中...')
}

// 退出登录点击事件
const handleLogout = async () => {
  try {
    await userLogout()
    loginUserStore.logout()
    message.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    message.error('退出登录失败')
  }
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
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  padding: 0 12px;
  border: none;
  box-shadow: none;
}

.user-dropdown-btn:hover {
  background-color: #f5f5f5;
}

.user-name {
  color: #333;
  font-size: 14px;
}

.login-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.login-status {
  color: #666;
  font-size: 14px;
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

  .user-section {
    gap: 8px;
  }

  .login-section {
    gap: 8px;
  }

  .user-dropdown-btn .user-name {
    display: none;
  }
}

@media (max-width: 576px) {
  .site-title {
    display: none;
  }

  .nav-menu {
    margin: 0 8px;
  }

  .login-status {
    display: none;
  }
}
</style>
