<template>
  <div class="login-page">
    <a-card title="用户登录" class="login-card">
      <a-form :model="loginForm" :rules="rules" @finish="handleLogin" layout="vertical">
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="loginForm.username" placeholder="请输入用户名" size="large">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item label="密码" name="password">
          <a-input-password
            v-model:value="loginForm.password"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            登录
          </a-button>
        </a-form-item>
      </a-form>

      <div class="login-tips">
        <p>测试账号：</p>
        <p>用户名：admin</p>
        <p>密码：123456</p>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

// 使用多词组件名避免ESLint警告
defineOptions({
  name: 'LoginPage',
})

const router = useRouter()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
  ],
}

const handleLogin = async () => {
  loading.value = true
  try {
    // 模拟登录请求
    await new Promise((resolve) => setTimeout(resolve, 1000))

    if (loginForm.username === 'admin' && loginForm.password === '123456') {
      message.success('登录成功！')
      router.push('/')
    } else {
      message.error('用户名或密码错误！')
    }
  } catch {
    message.error('登录失败，请重试！')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 600px;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.login-tips {
  margin-top: 24px;
  padding: 16px;
  background: #f0f2f5;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
}

.login-tips p {
  margin: 4px 0;
}
</style>
