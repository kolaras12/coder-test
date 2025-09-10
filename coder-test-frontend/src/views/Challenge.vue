<template>
  <div class="challenge-container">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <div class="main-content">
      <!-- 生成关卡区域 -->
      <div v-if="!currentLevel" class="generate-section">
        <el-card class="generate-card">
          <div class="generate-content">
            <h2>准备开始新的挑战</h2>
            <p>系统将根据你当前的薪资水平生成适合的技术关卡</p>
            <el-button
              type="primary"
              size="large"
              :disabled="generating"
              @click="generateLevel"
            >
              <el-icon><MagicStick /></el-icon>
              生成关卡
            </el-button>
            
            <!-- 自定义加载区域 -->
            <div v-if="generating" class="custom-loading-area">
              <img 
                :src="loadingIcon" 
                alt="生成中..." 
                class="custom-loading-icon"
                :style="{ left: loadingPosition.x + 'px', top: loadingPosition.y + 'px' }"
              />
              <div class="loading-text">正在生成关卡中...</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 关卡内容区域 -->
      <div v-if="currentLevel" class="level-section">
        <!-- 关卡信息 -->
        <el-card class="level-info-card">
          <div class="level-header">
            <h2 class="level-name">{{ currentLevel.levelName }}</h2>
            <div class="level-meta">
              <el-tag :type="getDifficultyType(currentLevel.difficulty)">
                {{ currentLevel.difficulty }}
              </el-tag>
              <span class="target-salary">
                目标薪资：¥{{ currentLevel.targetSalary?.toLocaleString() || 0 }}/月
              </span>
            </div>
          </div>
          
          <div class="level-desc">
            <h3>需求描述：</h3>
            <div class="desc-content" v-html="formatDescription(currentLevel.levelDesc)"></div>
          </div>
        </el-card>

        <!-- 答题区域 -->
        <div class="answer-section">
          <div class="options-area">
            <h3>可选项（拖拽到右侧答题区）</h3>
            <div class="options-grid">
              <div
                v-for="option in availableOptions"
                :key="option.optionName"
                class="option-item"
                :class="{ 'selected': selectedOptions.includes(option.optionName) }"
                draggable="true"
                @dragstart="handleDragStart($event, option)"
                @click="toggleOption(option)"
              >
                {{ option.optionName }}
              </div>
            </div>
          </div>

          <div class="answer-area">
            <h3>你的答案</h3>
            <div
              class="answer-box"
              @drop="handleDrop"
              @dragover="handleDragOver"
              @dragenter="handleDragEnter"
              @dragleave="handleDragLeave"
            >
              <div v-if="selectedOptions.length === 0" class="answer-placeholder">
                将选项拖拽到这里，或者点击左侧选项添加到答案中
              </div>
              
              <div
                v-for="(optionName, index) in selectedOptions"
                :key="index"
                class="selected-option"
              >
                {{ optionName }}
                <el-button
                  type="danger"
                  size="small"
                  :icon="Close"
                  circle
                  @click="removeOption(optionName)"
                />
              </div>
            </div>

            <div class="submit-area">
              <div class="button-row">
                <el-button
                  type="primary"
                  size="large"
                  :disabled="submitting || selectedOptions.length === 0"
                  @click="submitAnswer"
                >
                  <el-icon><Check /></el-icon>
                  提交答案
                </el-button>
                
                <el-button
                  size="large"
                  :disabled="submitting"
                  @click="resetLevel"
                >
                  <el-icon><Refresh /></el-icon>
                  重新生成
                </el-button>
              </div>
              
              <!-- 提交答案加载区域 -->
              <div v-if="submitting" class="custom-loading-area submit-loading">
                <img 
                  :src="loadingIcon" 
                  alt="提交中..." 
                  class="custom-loading-icon"
                  :style="{ left: submitLoadingPosition.x + 'px', top: submitLoadingPosition.y + 'px' }"
                />
                <div class="loading-text">正在提交答案...</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { generateLevel as generateLevelAPI } from '../api/level'
import { submitAnswer as submitAnswerAPI } from '../api/userLevel'
import { ElMessage } from 'element-plus'
import {
  MagicStick,
  Check,
  Refresh,
  Close
} from '@element-plus/icons-vue'
import GlobalNavbar from '../components/GlobalNavbar.vue'
import loadingIcon from '../assets/loading.png'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const generating = ref(false)
const submitting = ref(false)
const currentLevel = ref(null)
const selectedOptions = ref([])
const draggedOption = ref(null)

// 加载图标随机移动相关
const loadingPosition = ref({ x: 0, y: 0 })
const submitLoadingPosition = ref({ x: 0, y: 0 })
let loadingInterval = null
let submitLoadingInterval = null

// 随机移动函数
const getRandomPosition = (containerWidth = 300, containerHeight = 80, iconSize = 40) => {
  return {
    x: Math.random() * (containerWidth - iconSize),
    y: Math.random() * (containerHeight - iconSize)
  }
}

// 开始随机移动
const startRandomMovement = (positionRef, intervalRef) => {
  if (intervalRef) {
    clearInterval(intervalRef)
  }
  
  // 根据屏幕大小调整容器尺寸
  const isMobile = window.innerWidth <= 768
  const containerWidth = isMobile ? 210 : 260
  const containerHeight = isMobile ? 30 : 40
  const iconSize = isMobile ? 35 : 40
  
  // 初始位置
  positionRef.value = getRandomPosition(containerWidth, containerHeight, iconSize)
  
  // 每800ms移动一次（稍微慢一点，更优雅）
  return setInterval(() => {
    positionRef.value = getRandomPosition(containerWidth, containerHeight, iconSize)
  }, 800)
}

// 停止随机移动
const stopRandomMovement = (intervalRef) => {
  if (intervalRef) {
    clearInterval(intervalRef)
  }
}

// 计算可用选项（排除已选择的）
const availableOptions = computed(() => {
  if (!currentLevel.value?.options) return []
  
  try {
    const options = typeof currentLevel.value.options === 'string' 
      ? JSON.parse(currentLevel.value.options) 
      : currentLevel.value.options
    return options || []
  } catch (error) {
    console.error('解析选项失败:', error)
    return []
  }
})


const getDifficultyType = (difficulty) => {
  switch (difficulty) {
    case '简单': return 'success'
    case '中等': return 'warning'
    case '困难': return 'danger'
    default: return 'info'
  }
}

const formatDescription = (desc) => {
  if (!desc) return ''
  // 将换行符转换为 <br> 标签，保持原有格式
  return desc.replace(/\n/g, '<br>')
}

// 生成关卡
const generateLevel = async () => {
  console.log('用户信息:', user.value)
  console.log('用户薪资:', user.value?.salary)
  
  if (!user.value) {
    ElMessage.error('用户未登录，请先登录')
    router.push('/login')
    return
  }
  
  // 如果用户没有薪资信息，设置默认薪资为 5000
  const userSalary = user.value.salary || 5000
  console.log('使用的薪资:', userSalary)

  generating.value = true
  try {
    const levelData = await generateLevelAPI({ salary: userSalary })
    currentLevel.value = levelData
    selectedOptions.value = []
    ElMessage.success('关卡生成成功！')
  } catch (error) {
    console.error('生成关卡失败:', error)
    ElMessage.error('生成关卡失败，请重试')
  } finally {
    generating.value = false
  }
}

// 拖拽开始
const handleDragStart = (event, option) => {
  draggedOption.value = option
  event.dataTransfer.effectAllowed = 'move'
  event.dataTransfer.setData('text/plain', option.optionName)
}

// 拖拽悬停
const handleDragOver = (event) => {
  event.preventDefault()
  event.dataTransfer.dropEffect = 'move'
}

const handleDragEnter = (event) => {
  event.preventDefault()
  event.target.closest('.answer-box')?.classList.add('drag-over')
}

const handleDragLeave = (event) => {
  event.preventDefault()
  if (!event.target.closest('.answer-box')?.contains(event.relatedTarget)) {
    event.target.closest('.answer-box')?.classList.remove('drag-over')
  }
}

// 拖拽放下
const handleDrop = (event) => {
  event.preventDefault()
  event.target.closest('.answer-box')?.classList.remove('drag-over')
  
  if (draggedOption.value) {
    toggleOption(draggedOption.value)
    draggedOption.value = null
  }
}

// 切换选项（添加或移除）
const toggleOption = (option) => {
  const optionName = option.optionName
  const index = selectedOptions.value.indexOf(optionName)
  
  if (index > -1) {
    selectedOptions.value.splice(index, 1)
  } else {
    selectedOptions.value.push(optionName)
  }
}

// 移除选项
const removeOption = (optionName) => {
  const index = selectedOptions.value.indexOf(optionName)
  if (index > -1) {
    selectedOptions.value.splice(index, 1)
  }
}

// 提交答案
const submitAnswer = async () => {
  if (selectedOptions.value.length === 0) {
    ElMessage.warning('请至少选择一个选项')
    return
  }

  submitting.value = true
  try {
    const submitData = {
      levelId: currentLevel.value.id,
      userOptions: selectedOptions.value
    }
    
    const result = await submitAnswerAPI(submitData)
    console.log('提交答案结果:', result)
    
    // 更新用户薪资
    if (result.salaryChange) {
      const newSalary = user.value.salary + result.salaryChange
      userStore.updateUserSalary(newSalary)
    }
    
    // 跳转到结果页面
    console.log('准备跳转到结果页面:', `/result/${result.id}`)
    router.push(`/result/${result.id}`)
    
  } catch (error) {
    console.error('提交答案失败:', error)
    ElMessage.error('提交答案失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 重置关卡
const resetLevel = () => {
  currentLevel.value = null
  selectedOptions.value = []
}

// 监听生成状态变化
watch(generating, (newVal) => {
  if (newVal) {
    loadingInterval = startRandomMovement(loadingPosition, loadingInterval)
  } else {
    stopRandomMovement(loadingInterval)
    loadingInterval = null
  }
})

// 监听提交状态变化
watch(submitting, (newVal) => {
  if (newVal) {
    submitLoadingInterval = startRandomMovement(submitLoadingPosition, submitLoadingInterval)
  } else {
    stopRandomMovement(submitLoadingInterval)
    submitLoadingInterval = null
  }
})

onMounted(() => {
  // 页面加载时检查用户登录状态
  if (!user.value) {
    router.push('/login')
  }
})

onUnmounted(() => {
  // 组件销毁时清理定时器
  stopRandomMovement(loadingInterval)
  stopRandomMovement(submitLoadingInterval)
})
</script>

<style scoped>
.challenge-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(210, 180, 140, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 115, 85, 0.1) 0%, transparent 50%);
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 30px;
}

.generate-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.generate-card {
  max-width: 500px;
  width: 100%;
}

.generate-content {
  text-align: center;
  padding: 40px 20px;
}

.generate-content h2 {
  margin-bottom: 20px;
  color: var(--text-primary);
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.generate-content p {
  margin-bottom: 35px;
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 16px;
}

.level-section {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.level-info-card {
  width: 100%;
}

.level-header {
  margin-bottom: 20px;
}

.level-name {
  color: var(--text-primary);
  margin-bottom: 12px;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.level-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.target-salary {
  color: var(--text-secondary);
  font-size: 15px;
  font-weight: 600;
}

.level-desc h3 {
  color: var(--text-primary);
  margin-bottom: 15px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.desc-content {
  color: var(--text-secondary);
  line-height: 1.8;
  background: var(--bg-secondary);
  padding: 25px;
  border-radius: 12px;
  border-left: 4px solid var(--accent-gold);
  border: 2px solid var(--border-light);
  font-size: 15px;
}

.answer-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.options-area h3,
.answer-area h3 {
  color: var(--text-primary);
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.option-item {
  background: var(--bg-card);
  border: 2px solid var(--border-light);
  border-radius: 12px;
  padding: 18px 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
  color: var(--text-primary);
  font-weight: 500;
}

.option-item:hover {
  border-color: var(--primary-brown);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px var(--shadow-medium);
}

.option-item.selected {
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
  border-color: var(--accent-gold);
  color: var(--text-primary);
  font-weight: 600;
}

.answer-box {
  min-height: 220px;
  border: 2px dashed var(--border-medium);
  border-radius: 12px;
  padding: 25px;
  background: var(--bg-card);
  transition: all 0.3s ease;
}

.answer-box.drag-over {
  border-color: var(--accent-gold);
  background: var(--secondary-sand);
  border-style: solid;
}

.answer-placeholder {
  text-align: center;
  color: var(--text-muted);
  font-style: italic;
  padding: 70px 20px;
  font-size: 15px;
}

.selected-option {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%);
  color: var(--bg-card);
  padding: 10px 18px;
  border-radius: 25px;
  margin: 6px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 12px var(--shadow-medium);
}

.submit-area {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.submit-area .button-row {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 10px;
}

/* 自定义加载区域 */
.custom-loading-area {
  position: relative;
  width: 300px;
  height: 80px;
  margin: 20px auto;
  border: 2px dashed var(--border-medium);
  border-radius: 12px;
  background: var(--bg-secondary);
  overflow: hidden;
}

.custom-loading-icon {
  position: absolute;
  width: 40px;
  height: 40px;
  transition: all 0.3s ease-in-out;
  z-index: 2;
}

.loading-text {
  position: absolute;
  bottom: 5px;
  left: 50%;
  transform: translateX(-50%);
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  z-index: 1;
}

.submit-loading {
  margin-top: 15px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 30px 20px;
  }
  
  .answer-section {
    grid-template-columns: 1fr;
  }
  
  .options-grid {
    grid-template-columns: 1fr;
  }
  
  .submit-area .button-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .custom-loading-area {
    width: 250px;
    height: 70px;
  }
  
  .custom-loading-icon {
    width: 35px;
    height: 35px;
  }
}
</style>
