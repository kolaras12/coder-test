<template>
  <div class="result-container">
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
          <el-button @click="$router.push('/history')">
            <el-icon><Clock /></el-icon>
            历史记录
          </el-button>
        </div>
      </div>
    </div>

    <div class="main-content" v-loading="loading">
      <div v-if="resultData" class="result-content">
        <!-- 分数展示 -->
        <div class="score-section">
          <el-card class="score-card">
            <div class="score-display">
              <div class="score-circle" :class="getScoreClass(resultData.score)">
                <div class="score-number">{{ resultData.score }}</div>
                <div class="score-label">分</div>
              </div>
              
              <div class="score-info">
                <h2 class="score-title">挑战完成！</h2>
                <p class="score-comment">{{ resultData.comment }}</p>
                
                <div class="salary-change">
                  <div class="salary-item">
                    <span class="salary-label">薪资变化：</span>
                    <span class="salary-value" :class="getSalaryChangeClass(resultData.salaryChange)">
                      {{ resultData.salaryChange > 0 ? '+' : '' }}{{ resultData.salaryChange }}
                    </span>
                  </div>
                  
                  <div class="salary-item">
                    <span class="salary-label">当前薪资：</span>
                    <span class="current-salary">¥{{ user?.salary?.toLocaleString() || 0 }}/月</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 公司建议 -->
        <div class="suggestion-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><OfficeBuilding /></el-icon>
                投递建议
              </div>
            </template>
            
            <div class="suggestion-content">
              <p>{{ resultData.suggest }}</p>
            </div>
          </el-card>
        </div>

        <!-- 评分原因 -->
        <div class="reason-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><Document /></el-icon>
                评分原因
              </div>
            </template>
            
            <div class="reason-content">
              <p>{{ resultData.reason }}</p>
            </div>
          </el-card>
        </div>

        <!-- 正确答案 -->
        <div class="answers-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><Check /></el-icon>
                正确答案
              </div>
            </template>
            
            <div class="answers-content">
              <div class="true-options">
                <h4>标准选项：</h4>
                <div class="options-list">
                  <el-tag
                    v-for="option in trueOptionsList"
                    :key="option"
                    type="success"
                    class="option-tag"
                  >
                    {{ option }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 详细解析 -->
        <div class="analysis-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><Reading /></el-icon>
                详细解析
              </div>
            </template>
            
            <div class="analysis-content">
              <div class="markdown-content" v-html="formattedStandardAnswer"></div>
            </div>
          </el-card>
        </div>

        <!-- 操作按钮 -->
        <div class="actions-section">
          <el-button type="primary" size="large" @click="$router.push('/challenge')">
            <el-icon><Operation /></el-icon>
            继续挑战
          </el-button>
          
          <el-button size="large" @click="$router.push('/history')">
            <el-icon><Clock /></el-icon>
            查看历史
          </el-button>
          
          <el-button size="large" @click="$router.push('/')">
            <el-icon><House /></el-icon>
            返回首页
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUserLevelDetail } from '../api/userLevel'
import { ElMessage } from 'element-plus'
import {
  Trophy,
  Operation,
  Clock,
  OfficeBuilding,
  Document,
  Check,
  Reading,
  House
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const loading = ref(false)
const resultData = ref(null)

// 解析正确选项列表
const trueOptionsList = computed(() => {
  if (!resultData.value?.trueOptions) return []
  
  try {
    const options = typeof resultData.value.trueOptions === 'string' 
      ? JSON.parse(resultData.value.trueOptions) 
      : resultData.value.trueOptions
    return Array.isArray(options) ? options : []
  } catch (error) {
    console.error('解析正确选项失败:', error)
    return []
  }
})

// 格式化标准答案（简单的 Markdown 转 HTML）
const formattedStandardAnswer = computed(() => {
  if (!resultData.value?.standardAnswer) return ''
  
  let content = resultData.value.standardAnswer
  
  // 简单的 Markdown 转换
  content = content
    .replace(/\n/g, '<br>') // 换行
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 粗体
    .replace(/\*(.*?)\*/g, '<em>$1</em>') // 斜体
    .replace(/`(.*?)`/g, '<code>$1</code>') // 行内代码
  
  return content
})

// 获取分数样式类
const getScoreClass = (score) => {
  if (score >= 80) return 'excellent'
  if (score >= 60) return 'good'
  return 'poor'
}

// 获取薪资变化样式类
const getSalaryChangeClass = (change) => {
  if (change > 0) return 'salary-increase'
  if (change < 0) return 'salary-decrease'
  return 'salary-neutral'
}

// 获取结果详情
const fetchResultDetail = async () => {
  const id = route.params.id
  console.log('结果页面 - 路由参数 ID:', id)
  
  if (!id) {
    ElMessage.error('无效的结果ID')
    router.push('/history')
    return
  }

  loading.value = true
  try {
    console.log('开始获取结果详情:', id)
    const data = await getUserLevelDetail(id)
    console.log('获取到的结果数据:', data)
    resultData.value = data
  } catch (error) {
    console.error('获取结果详情失败:', error)
    ElMessage.error('获取结果详情失败')
    router.push('/history')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!user.value) {
    router.push('/login')
    return
  }
  fetchResultDetail()
})
</script>

<style scoped>
.result-container {
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
  gap: 15px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.score-section {
  display: flex;
  justify-content: center;
}

.score-card {
  min-width: 600px;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 40px;
  padding: 20px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  background: conic-gradient(from 0deg, var(--score-color) 0%, var(--score-color) var(--score-percent), #e4e7ed var(--score-percent), #e4e7ed 100%);
}

.score-circle.excellent {
  --score-color: #67c23a;
  --score-percent: 80%;
}

.score-circle.good {
  --score-color: #e6a23c;
  --score-percent: 60%;
}

.score-circle.poor {
  --score-color: #f56c6c;
  --score-percent: 40%;
}

.score-circle::before {
  content: '';
  position: absolute;
  width: 90px;
  height: 90px;
  background: white;
  border-radius: 50%;
}

.score-number {
  font-size: 36px;
  font-weight: bold;
  color: #333;
  z-index: 1;
}

.score-label {
  font-size: 14px;
  color: #666;
  z-index: 1;
}

.score-info {
  flex: 1;
}

.score-title {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
}

.score-comment {
  color: #666;
  font-size: 16px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.salary-change {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.salary-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.salary-label {
  color: #666;
  font-size: 14px;
}

.salary-value {
  font-weight: bold;
  font-size: 16px;
}

.salary-increase {
  color: #67c23a;
}

.salary-decrease {
  color: #f56c6c;
}

.salary-neutral {
  color: #909399;
}

.current-salary {
  color: #409eff;
  font-weight: bold;
  font-size: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.suggestion-content,
.reason-content {
  color: #666;
  line-height: 1.8;
  font-size: 15px;
}

.answers-content {
  color: #666;
}

.true-options h4 {
  color: #333;
  margin-bottom: 15px;
}

.options-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-tag {
  font-size: 14px;
  padding: 8px 12px;
}

.analysis-content {
  color: #666;
  line-height: 1.8;
}

.markdown-content {
  font-size: 15px;
}

.markdown-content strong {
  color: #333;
  font-weight: bold;
}

.markdown-content em {
  font-style: italic;
}

.markdown-content code {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: #e96900;
}

.actions-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .nav-menu {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .score-card {
    min-width: auto;
  }
  
  .score-display {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .actions-section {
    flex-direction: column;
    align-items: center;
  }
  
  .actions-section .el-button {
    width: 200px;
  }
}
</style>
