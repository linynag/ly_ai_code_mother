<template>
  <div class="login-page">
    <a-card :title="isRegister ? '用户注册' : '用户登录'" class="login-card">
      <a-form :model="loginForm" :rules="rules" @finish="handleSubmit" layout="vertical">
        <a-form-item label="账号" name="userAccount">
          <a-input v-model:value="loginForm.userAccount" placeholder="请输入账号" size="large">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item label="密码" name="userPassword">
          <a-input-password
            v-model:value="loginForm.userPassword"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item v-if="isRegister" label="确认密码" name="checkPassword">
          <a-input-password
            v-model:value="loginForm.checkPassword"
            placeholder="请再次输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            {{ isRegister ? '注册' : '登录' }}
          </a-button>
        </a-form-item>

        <div class="form-switch">
          <a-button type="link" @click="switchMode">
            {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
          </a-button>
        </div>
      </a-form>

      <div class="login-tips">
        <p>开发测试：</p>
        <p>可以使用任意账号密码进行注册测试</p>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { userLogin, userRegister } from '@/api/userController'
import { useLoginUserStore } from '@/stores/loginUser'

// 使用多词组件名避免ESLint警告
defineOptions({
  name: 'LoginPage',
})

const router = useRouter()
const route = useRoute()
const loginUserStore = useLoginUserStore()
const loading = ref(false)
const isRegister = ref(false)

const loginForm = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

// 动态验证规则
const rules = reactive({
  userAccount: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
  ],
  checkPassword: [
    {
      validator: (_: any, value: string) => {
        if (isRegister.value && value !== loginForm.userPassword) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur',
    },
  ],
})

// 切换登录/注册模式
const switchMode = () => {
  isRegister.value = !isRegister.value
  // 清空表单
  loginForm.userAccount = ''
  loginForm.userPassword = ''
  loginForm.checkPassword = ''
}

// 获取重定向路径
const redirect = route.query.redirect as string || '/'

const handleSubmit = async () => {
  loading.value = true
  try {
    if (isRegister.value) {
      // 注册逻辑
      const response = await userRegister({
        userAccount: loginForm.userAccount,
        userPassword: loginForm.userPassword,
        checkPassword: loginForm.checkPassword,
      })
      
      if (response.data.code === 0) {
        message.success('注册成功！请登录')
        isRegister.value = false
        // 清空密码字段
        loginForm.userPassword = ''
        loginForm.checkPassword = ''
      } else {
        message.error(response.data.message || '注册失败')
      }
    } else {
      // 登录逻辑
      const response = await userLogin({
        userAccount: loginForm.userAccount,
        userPassword: loginForm.userPassword,
      })
      
      if (response.data.code === 0 && response.data.data) {
        message.success('登录成功！')
        // 直接使用登录返回的用户信息更新状态
        loginUserStore.setLoginUser(response.data.data)
        // 跳转到重定向页面或首页
        router.push(redirect)
      } else {
        message.error(response.data.message || '登录失败')
      }
    }
  } catch (error: any) {
    console.error('操作失败:', error)
    message.error(error.response?.data?.message || '操作失败，请重试')
  } finally {
    loading.value = false
  }
}

// 监听模式变化，更新验证规则
watch(isRegister, () => {
  // 强制重新渲染表单验证
  Object.keys(rules).forEach(key => {
    const rule = rules[key as keyof typeof rules]
    if (rule && Array.isArray(rule)) {
      rule.forEach(r => {
        if (r.trigger) {
          r.trigger = [...(Array.isArray(r.trigger) ? r.trigger : [r.trigger])]
        }
      })
    }
  })
})
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

.form-switch {
  text-align: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
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
