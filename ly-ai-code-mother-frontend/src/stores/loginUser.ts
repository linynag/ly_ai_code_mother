import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUser } from '@/api/userController.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  // 初始状态为 null，表示未登录
  const loginUser = ref<API.LoginUserVO | null>(null)

  // 获取登录用户信息
  async function fetchLoginUser() {
    try {
      const res = await getLoginUser()
      if (res.data.code === 0 && res.data.data) {
        loginUser.value = res.data.data
      } else {
        // 如果请求失败或未登录，清空用户信息
        loginUser.value = null
      }
    } catch (error) {
      // 发生错误时，清空用户信息
      loginUser.value = null
    }
  }

  // 更新登录用户信息
  function setLoginUser(newLoginUser: API.LoginUserVO | null) {
    loginUser.value = newLoginUser
  }

  // 退出登录
  function logout() {
    loginUser.value = null
  }

  // 检查是否已登录
  function isLoggedIn() {
    return loginUser.value !== null && !!loginUser.value.id
  }

  return {
    loginUser,
    setLoginUser,
    fetchLoginUser,
    logout,
    isLoggedIn
  }
})
