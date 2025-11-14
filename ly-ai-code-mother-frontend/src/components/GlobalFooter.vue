<template>
  <a-layout-footer class="global-footer">
    <div class="footer-container">
      <div class="footer-content">
        <p class="copyright">
          零代码生成平台项目 by linynag
        </p>
        <p class="copyright-sub">
          © 2024 All Rights Reserved
        </p>
      </div>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 全局底部组件
</script>

<style scoped>
.global-footer {
  background: #001529;
  color: #fff;
  padding: 24px 0;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-content {
  text-align: center;
}

.copyright {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #fff;
}

.copyright-sub {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.65);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .global-footer {
    padding: 16px 0;
  }
  
  .footer-container {
    padding: 0 16px;
  }
  
  .copyright {
    font-size: 12px;
  }
}
</style>