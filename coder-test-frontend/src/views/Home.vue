<template>
  <div class="home">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon class="logo-icon"><Trophy /></el-icon>
          <span class="logo-text">程序员技术练兵场</span>
        </div>
        
        <div class="nav-menu">
          <el-button v-if="!isLoggedIn" type="primary" @click="$router.push('/login')">
            登录
          </el-button>
          <el-button v-if="!isLoggedIn" @click="$router.push('/register')">
            注册
          </el-button>
          
          <div v-if="isLoggedIn" class="user-info">
            <el-dropdown>
              <span class="user-name">
                {{ user.nickname || user.username }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/challenge')">
                    <el-icon><Operation /></el-icon>
                    开始挑战
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/history')">
                    <el-icon><Clock /></el-icon>
                    闯关历史
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 欢迎区域 -->
      <div class="welcome-section">
        <h1 class="welcome-title">欢迎来到程序员技术练兵场</h1>
        <p class="welcome-desc">
          通过AI生成的技术关卡，提升你的需求分析、方案设计能力，评估当前薪资水平
        </p>
        
        <div v-if="isLoggedIn" class="user-status">
          <el-card class="status-card">
            <div class="status-info">
              <div class="salary-info">
                <el-icon class="salary-icon"><Money /></el-icon>
                <span class="salary-label">当前薪资：</span>
                <span class="salary-value">¥{{ user.salary?.toLocaleString() || 0 }}/月</span>
              </div>
              <el-button type="primary" size="large" @click="startChallenge">
                <el-icon><Operation /></el-icon>
                开始挑战
              </el-button>
            </div>
          </el-card>
        </div>

        <div v-else class="login-prompt">
          <el-card class="prompt-card">
            <p>请先登录或注册账号，开始你的技术挑战之旅！</p>
            <div class="prompt-buttons">
              <el-button type="primary" size="large" @click="$router.push('/login')">
                立即登录
              </el-button>
              <el-button size="large" @click="$router.push('/register')">
                注册账号
              </el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 特性介绍 -->
      <div class="features-section">
        <h2 class="section-title">平台特色</h2>
        <div class="features-grid">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="40"><MagicStick /></el-icon>
            </div>
            <h3>AI智能生成</h3>
            <p>基于AI技术生成真实的企业需求场景，提供丰富的技术选项和干扰项</p>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="40"><DataAnalysis /></el-icon>
            </div>
            <h3>薪资评估</h3>
            <p>根据答题表现动态调整薪资，帮助你准确评估自己的技术水平</p>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="40"><Star /></el-icon>
            </div>
            <h3>个性化建议</h3>
            <p>提供投递公司建议和详细的技术解析，助你在职场中更进一步</p>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <el-footer class="footer">
      <p>&copy; 2024 程序员技术练兵场. All rights reserved.</p>
    </el-footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { 
  Trophy, 
  ArrowDown, 
  Operation, 
  Clock, 
  SwitchButton, 
  Money, 
  MagicStick, 
  DataAnalysis, 
  Star 
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const user = computed(() => userStore.user)

const handleLogout = async () => {
  await userStore.logoutUser()
  router.push('/')
}

const startChallenge = () => {
  router.push('/challenge')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  height: 60px;
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
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
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 80px;
}

.welcome-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-desc {
  font-size: 18px;
  color: #666;
  margin-bottom: 40px;
  line-height: 1.6;
}

.user-status, .login-prompt {
  display: flex;
  justify-content: center;
}

.status-card, .prompt-card {
  min-width: 400px;
}

.status-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.salary-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
}

.salary-icon {
  color: #f56c6c;
  font-size: 20px;
}

.salary-value {
  font-weight: bold;
  color: #409eff;
  font-size: 20px;
}

.prompt-buttons {
  margin-top: 20px;
  display: flex;
  gap: 15px;
  justify-content: center;
}

.features-section {
  margin-bottom: 60px;
}

.section-title {
  text-align: center;
  font-size: 32px;
  margin-bottom: 40px;
  color: #333;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.feature-card {
  text-align: center;
  padding: 30px 20px;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  margin-bottom: 20px;
  color: #409eff;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

.footer {
  background-color: #f8f9fa;
  text-align: center;
  color: #666;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 15px;
  }
  
  .welcome-title {
    font-size: 36px;
  }
  
  .welcome-desc {
    font-size: 16px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .status-card, .prompt-card {
    min-width: auto;
  }
}
</style>
