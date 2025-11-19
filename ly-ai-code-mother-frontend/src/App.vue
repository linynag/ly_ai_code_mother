<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import {useLoginUserStore} from '@/stores/loginUser'
import {onMounted} from 'vue'
// 初始化登录用户信息
const loginUserStore = useLoginUserStore()
onMounted(() => {
  loginUserStore.fetchLoginUser()
})
</script>


<template>
  <BasicLayout/>
</template>

<style scoped></style>
