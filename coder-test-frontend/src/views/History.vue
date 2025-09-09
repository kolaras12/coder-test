<template>
  <div class="history-container">
    <!-- 导航栏 -->
    <div class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <el-icon class="logo-icon"><Trophy /></el-icon>
          <span class="logo-text">程序员技术练兵场</span>
        </div>
        
        <div class="nav-menu">
          <el-button @click="$router.push('/challenge')">
            <el-icon><Operation /></el-icon>
            继续挑战
          </el-button>
          
          <el-dropdown>
            <span class="user-name">
              {{ user?.nickname || user?.username }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="page-header">
        <h1>闯关历史</h1>
        <p>查看你的所有挑战记录和成长轨迹</p>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><Trophy /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ historyList.length }}</div>
                  <div class="stat-label">总挑战次数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><Star /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ averageScore }}</div>
                  <div class="stat-label">平均分数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><Money /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ user?.salary?.toLocaleString() || 0 }}</div>
                  <div class="stat-label">当前薪资</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><DataAnalysis /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number" :class="totalSalaryChangeClass">
                    {{ totalSalaryChange > 0 ? '+' : '' }}{{ totalSalaryChange }}
                  </div>
                  <div class="stat-label">薪资变化</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 历史记录列表 -->
      <div class="history-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>挑战记录</span>
              <el-button type="primary" @click="refreshHistory">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>

          <div v-loading="loading" class="history-list">
            <div v-if="historyList.length === 0" class="empty-state">
              <el-empty description="暂无挑战记录">
                <el-button type="primary" @click="$router.push('/challenge')">
                  开始第一次挑战
                </el-button>
              </el-empty>
            </div>

            <div v-else class="history-items">
              <div
                v-for="item in historyList"
                :key="item.id"
                class="history-item"
                @click="viewResult(item.id)"
              >
                <div class="item-header">
                  <div class="level-info">
                    <h3 class="level-name">{{ getLevelName(item) }}</h3>
                    <div class="level-meta">
                      <el-tag size="small" :type="getScoreType(item.score)">
                        {{ item.score }}分
                      </el-tag>
                      <span class="challenge-time">
                        {{ formatTime(item.createTime) }}
                      </span>
                    </div>
                  </div>
                  
                  <div class="item-actions">
                    <div class="salary-change" :class="getSalaryChangeClass(item.salaryChange)">
                      薪资{{ item.salaryChange > 0 ? '+' : '' }}{{ item.salaryChange }}
                    </div>
                    <el-icon class="arrow-icon"><ArrowRight /></el-icon>
                  </div>
                </div>
                
                <div class="item-content">
                  <p class="comment">{{ item.comment || '暂无评价' }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUserLevelHistory } from '../api/userLevel'
import { ElMessage } from 'element-plus'
import {
  Trophy,
  Operation,
  ArrowDown,
  SwitchButton,
  Star,
  Money,
  DataAnalysis,
  Refresh,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const loading = ref(false)
const historyList = ref([])

// 计算平均分数
const averageScore = computed(() => {
  if (historyList.value.length === 0) return 0
  const total = historyList.value.reduce((sum, item) => sum + (item.score || 0), 0)
  return Math.round(total / historyList.value.length)
})

// 计算总薪资变化
const totalSalaryChange = computed(() => {
  return historyList.value.reduce((sum, item) => sum + (item.salaryChange || 0), 0)
})

// 薪资变化样式类
const totalSalaryChangeClass = computed(() => {
  if (totalSalaryChange.value > 0) return 'positive'
  if (totalSalaryChange.value < 0) return 'negative'
  return 'neutral'
})

const handleLogout = async () => {
  await userStore.logoutUser()
  router.push('/')
}

// 获取关卡名称（从用户选择的选项中提取或使用默认名称）
const getLevelName = (item) => {
  // 这里可以根据实际需要从 levelId 获取关卡名称
  // 由于后端接口没有直接返回关卡名称，暂时使用默认格式
  return `技术挑战 #${item.id.toString().slice(-6)}`
}

// 获取分数类型
const getScoreType = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 获取薪资变化样式类
const getSalaryChangeClass = (change) => {
  if (change > 0) return 'salary-positive'
  if (change < 0) return 'salary-negative'
  return 'salary-neutral'
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 查看结果详情
const viewResult = (id) => {
  router.push(`/result/${id}`)
}

// 获取历史记录
const fetchHistory = async () => {
  loading.value = true
  try {
    const data = await getUserLevelHistory()
    historyList.value = data || []
  } catch (error) {
    console.error('获取历史记录失败:', error)
    ElMessage.error('获取历史记录失败')
  } finally {
    loading.value = false
  }
}

// 刷新历史记录
const refreshHistory = () => {
  fetchHistory()
}

onMounted(() => {
  if (!user.value) {
    router.push('/login')
    return
  }
  fetchHistory()
})
</script>

<style scoped>
.history-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}

.logo-icon {
  font-size: 24px;
  margin-right: 10px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: white;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 36px;
  margin-bottom: 10px;
  color: #333;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  height: 100px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  margin-right: 15px;
  color: #409eff;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-number.positive {
  color: #67c23a;
}

.stat-number.negative {
  color: #f56c6c;
}

.stat-number.neutral {
  color: #909399;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.history-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.level-name {
  color: #333;
  margin-bottom: 5px;
  font-size: 18px;
}

.level-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.challenge-time {
  color: #999;
  font-size: 13px;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.salary-change {
  font-weight: bold;
  font-size: 14px;
}

.salary-positive {
  color: #67c23a;
}

.salary-negative {
  color: #f56c6c;
}

.salary-neutral {
  color: #909399;
}

.arrow-icon {
  color: #c0c4cc;
}

.comment {
  color: #666;
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .stats-section .el-col {
    margin-bottom: 15px;
  }
  
  .item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .item-actions {
    align-self: flex-end;
  }
}
</style>
