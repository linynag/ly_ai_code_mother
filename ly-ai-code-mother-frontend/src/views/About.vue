<template>
  <div class="about-page">
    <a-card title="关于我们" class="about-card">
      <a-typography>
        <a-typography-title :level="2">公司介绍</a-typography-title>
        <a-typography-paragraph>
          我们是一家专注于零代码开发平台的技术公司，致力于让每个人都能轻松创建自己的应用程序，无需编写复杂的代码。
        </a-typography-paragraph>

        <a-typography-title :level="2">我们的使命</a-typography-title>
        <a-typography-paragraph>
          通过提供直观、强大的零代码工具，让应用开发变得简单、快速、有趣，帮助企业和个人快速实现数字化转型。
        </a-typography-paragraph>

        <a-typography-title :level="2">核心优势</a-typography-title>
        <a-typography-paragraph>
          <ul>
            <li>拖拽式界面设计，所见即所得</li>
            <li>丰富的组件库和模板</li>
            <li>强大的数据处理能力</li>
            <li>完善的权限管理系统</li>
            <li>支持多平台部署</li>
          </ul>
        </a-typography-paragraph>

        <a-typography-title :level="2">联系我们</a-typography-title>
        <a-typography-paragraph>
          如果您有任何问题或建议，欢迎随时联系我们。我们期待与您的合作！
        </a-typography-paragraph>
      </a-typography>
    </a-card>
  </div>
</template>

<script setup lang="ts">
// 关于我们页面 - 使用多词组件名避免ESLint警告
defineOptions({
  name: 'AboutPage',
})
</script>

<style scoped>
.about-page {
  padding: 24px;
}

.about-card {
  max-width: 800px;
  margin: 0 auto;
}
</style>
