<template>
  <div class="home">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <!-- 主横幅区域 -->
    <div class="hero-section">
      <div class="hero-banner">
        <img :src="bannerImage" alt="技术练兵场" class="banner-image" />
        <div class="hero-overlay">
          <div class="hero-content">
            <h1 class="hero-title">程序员技术练兵场</h1>
            <div class="hero-subtitle">沙场点兵 · 技艺精进</div>
            <p class="hero-desc">
              如古之名将练兵于沙场，今之程序员磨技于此地<br>
              通过AI生成的技术关卡，提升需求分析、方案设计之能<br>
              评估技术水平，助君在编程之路上策马扬鞭
            </p>
            
            <div class="hero-actions" :class="{ 'logged-in': isLoggedIn }">
              <div v-if="isLoggedIn" class="salary-display">
                <el-icon class="salary-icon"><Coin /></el-icon>
                <span class="salary-label">当前身价：</span>
                <span class="salary-value">¥{{ user.salary?.toLocaleString() || 0 }}/月</span>
              </div>
              <el-button type="primary" size="large" class="challenge-btn" @click="handleChallengeClick">
                <el-icon><KnifeFork /></el-icon>
                开始挑战
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 特色功能 -->
      <div class="features-section">
        <div class="section-header">
          <h2 class="section-title">练兵场特色</h2>
          <div class="section-subtitle">如古之兵法，今之技艺</div>
        </div>
        
        <div class="features-grid">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><MagicStick /></el-icon>
            </div>
            <h3>智能生成</h3>
            <p>如孙武练兵，AI生成真实企业场景<br>丰富技术选项，考验决策智慧</p>
            <div class="feature-decoration"></div>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><TrendCharts /></el-icon>
            </div>
            <h3>能力评估</h3>
            <p>根据答题表现动态调整身价<br>准确评估技术水平，知己知彼</p>
            <div class="feature-decoration"></div>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><Guide /></el-icon>
            </div>
            <h3>策略指导</h3>
            <p>提供投递建议和技术解析<br>助君在职场征途中运筹帷幄</p>
            <div class="feature-decoration"></div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { 
  KnifeFork, 
  Coin, 
  MagicStick, 
  TrendCharts, 
  Guide 
} from '@element-plus/icons-vue'
import bannerImage from '../assets/banner.png'
import GlobalNavbar from '../components/GlobalNavbar.vue'

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

const handleChallengeClick = () => {
  if (isLoggedIn.value) {
    // 已登录，直接开始挑战
    startChallenge()
  } else {
    // 未登录，跳转到登录页面
    router.push('/login')
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: var(--bg-primary);
}


/* 主横幅区域 */
.hero-section {
  position: relative;
  height: 600px;
  overflow: hidden;
}

.hero-banner {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.7) sepia(0.3) saturate(1.2);
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(60, 36, 21, 0.8) 0%,
    rgba(139, 115, 85, 0.6) 50%,
    rgba(210, 180, 140, 0.4) 100%
  );
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-content {
  text-align: center;
  color: var(--bg-card);
  max-width: 800px;
  padding: 0 30px;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.5);
  letter-spacing: 3px;
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--bg-card) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 24px;
  color: var(--accent-gold);
  letter-spacing: 2px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-desc {
  font-size: 18px;
  line-height: 1.8;
  margin-bottom: 40px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.4);
  opacity: 0.95;
}

.hero-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.hero-actions.logged-in {
  gap: 25px;
}

.salary-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: rgba(218, 165, 32, 0.2);
  border: 2px solid var(--accent-gold);
  border-radius: 50px;
  backdrop-filter: blur(10px);
  font-size: 18px;
  font-weight: 600;
}

.salary-icon {
  color: var(--accent-gold);
  font-size: 22px;
}

.salary-label {
  color: var(--bg-card);
}

.salary-value {
  color: var(--accent-gold);
  font-size: 20px;
  font-weight: 700;
}

.challenge-btn, .primary-btn, .secondary-btn {
  font-size: 18px !important;
  padding: 16px 32px !important;
  border-radius: 50px !important;
  font-weight: 600 !important;
  letter-spacing: 1px !important;
  min-width: 180px;
}

.challenge-btn {
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--accent-copper) 100%) !important;
  border: 3px solid var(--accent-gold) !important;
  color: var(--text-primary) !important;
  box-shadow: 0 6px 20px rgba(218, 165, 32, 0.4) !important;
}

.challenge-btn:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 8px 25px rgba(218, 165, 32, 0.6) !important;
}

/* 主要内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 80px 30px;
}

.features-section {
  margin-bottom: 80px;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--text-primary);
  letter-spacing: 2px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
  border-radius: 2px;
}

.section-subtitle {
  font-size: 18px;
  color: var(--text-secondary);
  font-style: italic;
  letter-spacing: 1px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 40px;
  margin-top: 60px;
}

.feature-card {
  text-align: center;
  padding: 40px 30px;
  position: relative;
  overflow: hidden;
  background: var(--bg-card) !important;
  border: 3px solid var(--border-light) !important;
  transition: all 0.4s ease !important;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(218, 165, 32, 0.1), transparent);
  transition: left 0.6s ease;
}

.feature-card:hover::before {
  left: 100%;
}

.feature-card:hover {
  transform: translateY(-8px) !important;
  border-color: var(--accent-gold) !important;
  box-shadow: 0 15px 40px var(--shadow-heavy) !important;
}

.feature-icon {
  margin-bottom: 24px;
  color: var(--primary-brown);
  position: relative;
  z-index: 2;
}

.feature-card h3 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--text-primary);
  letter-spacing: 1px;
  position: relative;
  z-index: 2;
}

.feature-card p {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 16px;
  position: relative;
  z-index: 2;
}

.feature-decoration {
  position: absolute;
  bottom: 15px;
  right: 15px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
  border-radius: 50%;
  opacity: 0.1;
  transform: scale(0);
  transition: all 0.4s ease;
}

.feature-card:hover .feature-decoration {
  transform: scale(1);
  opacity: 0.2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  
  .hero-title {
    font-size: 40px;
  }
  
  .hero-desc {
    font-size: 16px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .main-content {
    padding: 60px 20px;
  }
  
  .hero-content {
    padding: 0 20px;
  }
  
  .hero-actions {
    flex-direction: column;
    gap: 15px;
  }
  
  .challenge-btn, .primary-btn, .secondary-btn {
    width: 100%;
    max-width: 280px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    height: 500px;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .section-title {
    font-size: 32px;
  }
  
  .feature-card {
    padding: 30px 20px;
  }
}
</style>
