<template>
  <div class="products-page">
    <a-card title="产品展示" class="products-card">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="8" :lg="6" v-for="i in 8" :key="i">
          <a-card hoverable class="product-card">
            <template #cover>
              <img
                alt="产品图片"
                :src="`https://via.placeholder.com/200x150/1890ff/ffffff?text=产品${i}`"
              />
            </template>
            <a-card-meta
              :title="'产品 ' + i"
              description="这是一个优质的产品，具有出色的性能和用户体验。"
            >
            </a-card-meta>
            <template #actions>
              <a-button type="link">查看详情</a-button>
              <a-button type="primary">立即购买</a-button>
            </template>
          </a-card>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
// 产品展示页面 - 使用多词组件名避免ESLint警告
defineOptions({
  name: 'ProductsPage',
})
</script>

<style scoped>
.products-page {
  padding: 24px;
}

.products-card {
  max-width: 1200px;
  margin: 0 auto;
}

.product-card {
  height: 100%;
}

.product-card img {
  height: 150px;
  object-fit: cover;
}
</style>
