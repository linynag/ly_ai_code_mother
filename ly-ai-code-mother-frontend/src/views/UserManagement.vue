<template>
  <div class="user-management">
    <div class="page-header">
      <h1>用户管理</h1>
      <a-space>
        <a-button @click="exportUsers">
          <template #icon><DownloadOutlined /></template>
          导出
        </a-button>
        <a-button type="primary" @click="showAddModal = true">
          <template #icon><PlusOutlined /></template>
          添加用户
        </a-button>
      </a-space>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-filters">
      <div class="search-row">
        <div class="search-item compact">
          <label class="search-label">账号:</label>
          <a-input
            v-model:value="searchForm.userAccount"
            placeholder="请输入用户账号"
            allow-clear
            style="width: 120px"
          />
        </div>
        <div class="search-item compact">
          <label class="search-label">姓名:</label>
          <a-input
            v-model:value="searchForm.userName"
            placeholder="请输入用户名"
            allow-clear
            style="width: 120px"
          />
        </div>
        <div class="search-item compact">
          <label class="search-label">角色:</label>
          <a-select
            v-model:value="searchForm.userRole"
            placeholder="请选择角色"
            allow-clear
            style="width: 100px"
          >
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="user">用户</a-select-option>
          </a-select>
        </div>
        <div class="search-item compact">
          <label class="search-label">创建时间:</label>
          <a-date-picker
            v-model:value="searchForm.createTimeRange"
            type="daterange"
            placeholder="选择时间范围"
            style="width: 180px"
          />
        </div>
        <div class="search-actions-inline">
          <a-button type="primary" @click="handleSearch" :loading="loading">
            <template #icon><SearchOutlined /></template>
            搜索
          </a-button>
          <a-button @click="handleReset">
            <template #icon><ReloadOutlined /></template>
            重置
          </a-button>
        </div>
      </div>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedRowKeys.length > 0">
      <a-alert
        :message="`已选择 ${selectedRowKeys.length} 项`"
        type="info"
        show-icon
      />
      <a-space style="margin-left: 16px">
        <a-button danger :loading="batchLoading">
          <template #icon><DeleteOutlined /></template>
          批量删除
        </a-button>
        <a-button @click="selectedRowKeys = []">
          取消选择
        </a-button>
      </a-space>
    </div>

    <!-- 用户列表 -->
    <a-table
      :columns="columns"
      :data-source="userList"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      :row-selection="rowSelection"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="handleView(record)">
              <template #icon><EyeOutlined /></template>
              查看
            </a-button>
            <a-button type="link" @click="handleEdit(record)">
              <template #icon><EditOutlined /></template>
              编辑
            </a-button>
            <a-popconfirm
              title="确定要删除这个用户吗？"
              @confirm="handleDelete(record)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button type="link" danger>
                <template #icon><DeleteOutlined /></template>
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 添加用户弹窗 -->
    <a-modal
      v-model:open="showAddModal"
      title="添加用户"
      width="600px"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirm-loading="submitting"
      :maskClosable="false"
    >
      <a-form
        ref="addFormRef"
        :model="userForm"
        :rules="formRules"
        label-col="{ span: 6 }"
        wrapper-col="{ span: 18 }"
      >
        <a-form-item label="用户账号" name="userAccount">
          <a-input
            v-model:value="userForm.userAccount"
            placeholder="请输入用户账号"
            @blur="checkAccountExists"
          />
        </a-form-item>

        <a-form-item label="用户名" name="userName">
          <a-input
            v-model:value="userForm.userName"
            placeholder="请输入用户名"
          />
        </a-form-item>

        <a-form-item label="用户简介" name="userProfile">
          <a-textarea
            v-model:value="userForm.userProfile"
            placeholder="请输入用户简介"
            :rows="3"
            maxlength="200"
            show-count
          />
        </a-form-item>

        <a-form-item label="用户头像" name="userAvatar">
          <a-input
            v-model:value="userForm.userAvatar"
            placeholder="请输入头像URL"
            addon-before="URL"
          />
        </a-form-item>

        <a-form-item label="用户密码" name="userPassword">
          <a-input-password
            v-model:value="userForm.userPassword"
            placeholder="请输入用户密码"
            strength
          />
        </a-form-item>

        <a-form-item label="用户角色" name="userRole">
          <a-select
            v-model:value="userForm.userRole"
            placeholder="请选择用户角色"
            allow-clear
          >
            <a-select-option value="user">普通用户</a-select-option>
            <a-select-option value="admin">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 编辑用户弹窗 -->
    <a-modal
      v-model:open="showEditModal"
      title="编辑用户"
      width="600px"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirm-loading="submitting"
      :maskClosable="false"
    >
      <a-form
        ref="editFormRef"
        :model="userForm"
        :rules="editFormRules"
        label-col="{ span: 6 }"
        wrapper-col="{ span: 18 }"
      >
        <a-form-item label="用户ID" name="id">
          <a-input v-model:value="userForm.id" disabled />
        </a-form-item>

        <a-form-item label="用户账号" name="userAccount">
          <a-input
            v-model:value="userForm.userAccount"
            placeholder="请输入用户账号"
            disabled
          />
        </a-form-item>

        <a-form-item label="用户名" name="userName">
          <a-input
            v-model:value="userForm.userName"
            placeholder="请输入用户名"
          />
        </a-form-item>

        <a-form-item label="用户简介" name="userProfile">
          <a-textarea
            v-model:value="userForm.userProfile"
            placeholder="请输入用户简介"
            :rows="3"
            maxlength="200"
            show-count
          />
        </a-form-item>

        <a-form-item label="用户头像" name="userAvatar">
          <a-input
            v-model:value="userForm.userAvatar"
            placeholder="请输入头像URL"
            addon-before="URL"
          />
        </a-form-item>

        <a-form-item label="用户密码" name="userPassword">
          <a-input-password
            v-model:value="userForm.userPassword"
            placeholder="留空则不修改密码"
            strength
          />
        </a-form-item>

<!--        <a-form-item label="用户状态" name="userStatus">-->
<!--          <a-radio-group v-model:value="userForm.userStatus">-->
<!--            <a-radio :value="1">启用</a-radio>-->
<!--            <a-radio :value="0">禁用</a-radio>-->
<!--          </a-radio-group>-->
<!--        </a-form-item>-->
      </a-form>
    </a-modal>

    <!-- 用户详情弹窗 -->
    <a-modal
      v-model:open="showDetailModal"
      title="用户详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions v-if="currentUser" :column="2" bordered>
        <a-descriptions-item label="用户ID">
          {{ currentUser.id }}
        </a-descriptions-item>
        <a-descriptions-item label="用户账号">
          {{ currentUser.userAccount }}
        </a-descriptions-item>
        <a-descriptions-item label="用户名">
          {{ currentUser.userName }}
        </a-descriptions-item>
<!--        <a-descriptions-item label="用户状态">-->
<!--          <a-tag :color="currentUser.userStatus === 1 ? 'green' : 'red'">-->
<!--            {{ currentUser.userStatus === 1 ? '启用' : '禁用' }}-->
<!--          </a-tag>-->
<!--        </a-descriptions-item>-->
        <a-descriptions-item label="用户头像">
          <a-avatar :src="currentUser.userAvatar" :size="48">
            {{ currentUser.userName?.charAt(0) }}
          </a-avatar>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ formatDate(currentUser.createTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="用户简介" :span="2">
          {{ currentUser.userProfile || '暂无简介' }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  DownloadOutlined
} from '@ant-design/icons-vue'
import {
  listUserVoByPage,
  getUserVoById,
  addUser,
  updateUser,
  deleteUser
} from '@/api/userController'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const batchLoading = ref(false)
const userList = ref([])
const currentUser = ref(null)
const showAddModal = ref(false)
const showEditModal = ref(false)
const showDetailModal = ref(false)
const selectedRowKeys = ref([])
const addFormRef = ref()
const editFormRef = ref()

// 搜索表单
const searchForm = reactive({
  userAccount: '',
  userName: '',
  userRole: undefined as string | undefined,
  createTimeRange: null as any
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number, range: [number, number]) => {
    return `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
  }
})

// 表格行选择配置
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: string[]) => {
    selectedRowKeys.value = keys
  }
}))

// 表格列定义 - 只显示后端返回的字段
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
    fixed: 'left'
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    key: 'userAvatar',
    width: 80,
    fixed: 'left',
    customRender: ({ text, record }: any) => {
      return text ?
        h('div', { class: 'user-avatar-container' }, [
          h('img', {
            src: text,
            alt: record.userName,
            style: {
              width: '100%',
              height: '100%',
              objectFit: 'cover'
            }
          })
        ]) :
        h('div', { class: 'user-avatar-container' }, [
          h('div', { class: 'user-avatar-fallback' }, record.userName?.charAt(0) || '无')
        ])
    }
  },
  {
    title: '用户账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
    width: 150,
    ellipsis: true
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName',
    width: 120,
    ellipsis: true
  },
  {
    title: '用户简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
    ellipsis: true
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    key: 'userRole',
    width: 100,
    customRender: ({ text }: any) => {
      const roleMap: any = {
        'admin': { color: 'red', text: '管理员' },
        'user': { color: 'blue', text: '用户' }
      }
      const roleInfo = roleMap[text] || { color: 'default', text: text }
      return h('a-tag', {
        color: roleInfo.color
      }, roleInfo.text)
    }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    fixed: 'right',
    sorter: true,
    customRender: ({ text }: any) => {
      return text ? new Date(text).toLocaleString('zh-CN') : ''
    }
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right'
  }
]

// 表单数据
const userForm = reactive({
  id: null,
  userAccount: '',
  userName: '',
  userProfile: '',
  userAvatar: '',
  userPassword: '',
  checkPassword: '',
  userRole: 'user'
})

// 表单验证规则
const formRules = {
  userAccount: [
    { required: true, message: '请输入用户账号', trigger: 'blur' },
    { min: 4, max: 20, message: '账号长度在 4 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '账号只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入用户密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/,
      message: '密码必须包含大小写字母和数字', trigger: 'blur'
    }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string) => {
        if (value && value !== userForm.userPassword) {
          return Promise.reject('两次输入密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  userAvatar: [
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' }
  ]
}

// 编辑表单验证规则（移除密码必填规则）
const editFormRules = {
  userName: formRules.userName,
  userPassword: [
    {
      validator: (rule: any, value: string) => {
        if (value && value.length < 6) {
          return Promise.reject('密码长度至少 6 个字符')
        }
        if (value && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/.test(value)) {
          return Promise.reject('密码必须包含大小写字母和数字')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  userAvatar: formRules.userAvatar
}

// 方法定义
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

const buildSearchParams = () => {
  const params: any = {
    current: pagination.current,
    pageSize: pagination.pageSize
  }

  if (searchForm.userAccount) {
    params.userAccount = searchForm.userAccount
  }
  if (searchForm.userName) {
    params.userName = searchForm.userName
  }
  if (searchForm.userRole) {
    params.userRole = searchForm.userRole
  }
  if (searchForm.createTimeRange && searchForm.createTimeRange.length === 2) {
    params.createTimeStart = searchForm.createTimeRange[0].format('YYYY-MM-DD HH:mm:ss')
    params.createTimeEnd = searchForm.createTimeRange[1].format('YYYY-MM-DD HH:mm:ss')
  }

  return params
}

const fetchUsers = async () => {
  loading.value = true
  try {
    console.log('开始获取用户列表...')
    const params = buildSearchParams()
    console.log('请求参数:', params)

    // 首先尝试调用真实API
    try {
      const response = await listUserVoByPage(params)
      console.log('API响应:', response)

      // 修复API响应数据处理
      let records = []
      let total = 0

      if (response.data) {
        const responseData = response.data
        if (responseData.code === 0 && responseData.data) {
          // 后端返回的数据结构：{ code: 0, data: { records: [], total: number } }
          records = responseData.data.records || []
          total = responseData.data.total || 0
          console.log('用户数据:', records)
          console.log('总数:', total)
        } else {
          // 如果response.data是直接的数组
          if (Array.isArray(responseData)) {
            records = responseData
            total = records.length
          } else if (responseData.records) {
            records = responseData.records
            total = responseData.total || records.length
          }
        }
      } else if (Array.isArray(response)) {
        // 如果response本身就是数组
        records = response
        total = records.length
      }

      console.log('处理后的数据:', records)
      console.log('分页总数:', total)

      // 确保数据是数组
      userList.value = Array.isArray(records) ? records : []
      pagination.total = total
      pagination.current = params.current || 1
      pagination.pageSize = params.pageSize || 10

      console.log('设置后的用户数据:', userList.value)
      console.log('分页信息:', pagination)

      // 如果没有数据，显示提示
      if (!userList.value.length) {
        message.info('暂无用户数据')
      }
    } catch (apiError) {
      console.warn('API调用失败，使用模拟数据:', apiError)
      // API调用失败，使用模拟数据
      userList.value = getMockData()
      pagination.total = userList.value.length
      pagination.current = 1
      pagination.pageSize = 10
      message.info('使用模拟数据展示')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    message.error('获取用户列表失败: ' + (error as any)?.message)
    // 如果所有都失败，使用模拟数据
    userList.value = getMockData()
    pagination.total = userList.value.length
    message.info('使用模拟数据展示')
  } finally {
    loading.value = false
  }
}

// 获取模拟数据 - 更新为使用userRole字段
const getMockData = () => {
  return [
    {
      id: 1,
      userAccount: 'admin',
      userName: '系统管理员',
      userProfile: '系统管理员账户',
      userAvatar: 'https://via.placeholder.com/32x32?text=A',
      userRole: 'admin',
      createTime: '2024-01-15 10:30:00'
    },
    {
      id: 2,
      userAccount: 'user001',
      userName: '张三',
      userProfile: '前端开发工程师',
      userAvatar: 'https://via.placeholder.com/32x32?text=张',
      userRole: 'user',
      createTime: '2024-01-16 14:20:00'
    },
    {
      id: 3,
      userAccount: 'user002',
      userName: '李四',
      userProfile: '后端开发工程师',
      userAvatar: 'https://via.placeholder.com/32x32?text=李',
      userRole: 'user',
      createTime: '2024-01-17 09:15:00'
    },
    {
      id: 4,
      userAccount: 'user003',
      userName: '王五',
      userProfile: 'UI设计师',
      userAvatar: 'https://via.placeholder.com/32x32?text=王',
      userRole: 'user',
      createTime: '2024-01-18 16:45:00'
    },
    {
      id: 5,
      userAccount: 'user004',
      userName: '赵六',
      userProfile: '产品经理',
      userAvatar: 'https://via.placeholder.com/32x32?text=赵',
      userRole: 'user',
      createTime: '2024-01-19 11:30:00'
    },
    {
      id: 6,
      userAccount: 'user005',
      userName: '钱七',
      userProfile: '测试工程师',
      userAvatar: 'https://via.placeholder.com/32x32?text=钱',
      userRole: 'user',
      createTime: '2024-01-20 13:20:00'
    }
  ]
}

const handleSearch = () => {
  pagination.current = 1
  fetchUsers()
}

const handleReset = () => {
  Object.assign(searchForm, {
    userAccount: '',
    userName: '',
    userRole: undefined,
    createTimeRange: null
  })
  pagination.current = 1
  fetchUsers()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchUsers()
}

const handleView = async (record: any) => {
  try {
    const response = await getUserVoById({ id: record.id })
    if (response.data.code === 0) {
      currentUser.value = response.data.data
      showDetailModal.value = true
    } else {
      message.error(response.data.message || '获取用户详情失败')
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    message.error('获取用户详情失败')
  }
}

const handleEdit = (record: any) => {
  Object.assign(userForm, {
    id: record.id,
    userAccount: record.userAccount,
    userName: record.userName,
    userProfile: record.userProfile || '',
    userAvatar: record.userAvatar || '',
    userPassword: '',
    checkPassword: '',
    userRole: record.userRole || 'user'
  })
  showEditModal.value = true
}

const handleDelete = async (record: any) => {
  try {
    const response = await deleteUser({ id: record.id })
    if (response.data.code === 0) {
      message.success('用户删除成功')
      fetchUsers()
    } else {
      message.error(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除用户失败:', error)
    message.error('删除用户失败')
  }
}

const checkAccountExists = async () => {
  if (!userForm.userAccount || userForm.userAccount.length < 4) return

  try {
    // 这里可以调用检查账号是否存在的API
    // const response = await checkAccountExists(userForm.userAccount)
    // if (response.data.data) {
    //   message.warning('账号已存在')
    // }
  } catch (error) {
    console.error('检查账号是否存在失败:', error)
  }
}

const handleSubmit = async () => {
  const formRef = showEditModal.value ? editFormRef.value : addFormRef.value
  if (!formRef) return

  try {
    await formRef.validateFields()
    submitting.value = true

    if (showEditModal.value) {
      // 编辑用户
      const updateData: any = {
        id: userForm.id,
        userAccount: userForm.userAccount,
        userName: userForm.userName,
        userProfile: userForm.userProfile,
        userAvatar: userForm.userAvatar,
        userRole: userForm.userRole
      }

      // 只有当密码不为空时才更新密码
      if (userForm.userPassword) {
        updateData.userPassword = userForm.userPassword
      }

      const response = await updateUser(updateData)
      if (response.data.code === 0) {
        message.success('用户更新成功')
        showEditModal.value = false
        fetchUsers()
      } else {
        message.error(response.data.message || '更新失败')
      }
    } else {
      // 添加用户
      const addData = {
        userAccount: userForm.userAccount,
        userName: userForm.userName,
        userProfile: userForm.userProfile,
        userAvatar: userForm.userAvatar,
        userPassword: userForm.userPassword,
        userRole: userForm.userRole
      }

      const response = await addUser(addData)
      if (response.data.code === 0) {
        message.success('用户添加成功')
        showAddModal.value = false
        fetchUsers()
      } else {
        message.error(response.data.message || '添加失败')
      }
    }
  } catch (error: any) {
    console.error('表单提交失败:', error)
    if (error.errorFields) {
      message.error('请检查表单信息')
    }
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  const formRef = showEditModal.value ? editFormRef.value : addFormRef.value
  formRef?.resetFields()

  Object.assign(userForm, {
    id: null,
    userAccount: '',
    userName: '',
    userProfile: '',
    userAvatar: '',
    userPassword: '',
    checkPassword: '',
    userRole: 'user'
  })
  showAddModal.value = false
  showEditModal.value = false
}

// 批量操作方法
const batchDelete = async () => {
  batchLoading.value = true
  try {
    // 批量删除逻辑
    await Promise.all(
      selectedRowKeys.value.map(id => deleteUser({ id }))
    )
    message.success('批量删除成功')
    selectedRowKeys.value = []
    fetchUsers()
  } catch (error) {
    message.error('批量删除失败')
  } finally {
    batchLoading.value = false
  }
}

const exportUsers = () => {
  // 导出逻辑
  message.info('导出功能开发中...')
}

// 生命周期
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  color: #262626;
}

.search-filters {
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.search-item.compact {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.search-label {
  font-size: 13px;
  color: #595959;
  white-space: nowrap;
  min-width: 40px;
}

.search-actions-inline {
  display: flex;
  gap: 8px;
  margin-left: auto;
  flex-shrink: 0;
}

.batch-actions {
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
}

.user-avatar-container {
  display: inline-block;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  background-color: #f0f0f0;
}

.user-avatar-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-row {
    flex-direction: column;
    align-items: stretch;
  }

  .search-actions-inline {
    margin-left: 0;
    justify-content: center;
  }
}

/* 表格样式优化 */
:deep(.ant-table-thead > tr > th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background-color: #f5f5f5;
}
</style>
