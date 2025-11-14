<template>
  <a-layout class="basic-layout">
    <!-- 全局头部 -->
    <GlobalHeader />

    <!-- 内容区域 -->
    <a-layout-content class="layout-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </a-layout-content>

    <!-- 全局底部 -->
    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '../components/GlobalHeader.vue'
import GlobalFooter from '../components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-content {
  flex: 1;
  padding: 24px;
  background: #f5f5f5;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 600px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-content {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
  }
}
</style>
