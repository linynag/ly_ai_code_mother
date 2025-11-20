<template>
  <div class="admin-test">
    <h1>管理员测试页面</h1>
    <p>只有管理员才能看到这个页面</p>
    <p>当前用户角色: {{ loginUserStore.loginUser?.userRole || '未登录' }}</p>
  </div>
</template>

<script setup lang="ts">
import { useLoginUserStore } from '@/stores/loginUser'
import { onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { hasAdminPermission } from '@/utils/auth'
import { useRouter } from 'vue-router'

const loginUserStore = useLoginUserStore()
const router = useRouter()

onMounted(() => {
  if (!hasAdminPermission()) {
    message.error('没有权限访问此页面')
    router.push('/')
  }
})
</script>

<style scoped>
.admin-test {
  padding: 24px;
  text-align: center;
}
</style>