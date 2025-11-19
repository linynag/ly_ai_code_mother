import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Products from '@/views/Products.vue'
import About from '@/views/About.vue'
import Login from '@/views/Login.vue'
import UserManagement from '@/views/UserManagement.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/products',
      name: 'products',
      component: Products
    },
    {
      path: '/about',
      name: 'about',
      component: About
    },
    {
      path: '/contact',
      name: 'contact',
      component: About // 暂时使用关于页面作为联系页面
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/user-management',
      name: 'userManagement',
      component: UserManagement,
      meta: { requiresAuth: true, title: '用户管理' }
    }
  ],
})

export default router
