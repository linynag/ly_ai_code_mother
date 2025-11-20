import { useLoginUserStore } from '@/stores/loginUser'
import { message } from 'ant-design-vue'
import type { Router } from 'vue-router'

// 是否为首次获取登录用户
let firstFetchLoginUser = true

/**
 * 全局权限校验
 */
export function setupAuthGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    const loginUserStore = useLoginUserStore()
    let loginUser = loginUserStore.loginUser

    // 确保页面刷新，首次加载时，能够等后端返回用户信息后再校验权限
    if (firstFetchLoginUser) {
      await loginUserStore.fetchLoginUser()
      loginUser = loginUserStore.loginUser
      firstFetchLoginUser = false
    }

    const toUrl = to.fullPath

    // 管理员权限校验
    if (toUrl.startsWith('/admin') || to.meta?.requiresAdmin) {
      if (!loginUser || loginUser.userRole !== 'admin') {
        message.error('没有权限访问该页面')
        next(`/login?redirect=${to.fullPath}`)
        return
      }
    }

    // 登录权限校验
    if (to.meta?.requiresAuth && !loginUserStore.isLoggedIn()) {
      message.error('请先登录')
      next(`/login?redirect=${to.fullPath}`)
      return
    }

    next()
  })
}

/**
 * 检查是否有管理员权限
 */
export function hasAdminPermission(): boolean {
  const loginUserStore = useLoginUserStore()
  const loginUser = loginUserStore.loginUser
  return loginUser && loginUser.userRole === 'admin'
}

/**
 * 检查是否已登录
 */
export function isAuthenticated(): boolean {
  const loginUserStore = useLoginUserStore()
  return loginUserStore.isLoggedIn()
}
