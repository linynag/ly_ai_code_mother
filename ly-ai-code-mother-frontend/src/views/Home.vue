<template>
  <div class="home-page">
    <a-card title="欢迎使用零代码生成平台" class="welcome-card">
      <p>这是一个现代化的前端项目，使用 Vue 3 + TypeScript + Ant Design Vue 构建。</p>
      <p>项目特点：</p>
      <ul>
        <li>✅ 响应式设计，支持移动端和桌面端</li>
        <li>✅ 使用 Ant Design Vue 组件库</li>
        <li>✅ TypeScript 支持，类型安全</li>
        <li>✅ 模块化架构，易于扩展</li>
      </ul>
      <template #actions>
        <a-button type="primary" @click="$router.push('/products')"> 了解更多 </a-button>
      </template>
    </a-card>
  </div>
</template>

<script setup lang="ts">
// 首页组件 - 使用多词组件名避免ESLint警告
defineOptions({
  name: 'HomePage',
})
</script>

<style scoped>
.home-page {
  padding: 24px;
}

.welcome-card {
  max-width: 800px;
  margin: 0 auto;
}

.welcome-card ul {
  margin: 16px 0;
  padding-left: 24px;
}

.welcome-card li {
  margin: 8px 0;
  color: #666;
}
</style>
