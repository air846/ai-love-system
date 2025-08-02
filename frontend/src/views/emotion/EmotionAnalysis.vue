<template>
  <div class="emotion-analysis">
    <div class="page-header">
      <h1>情感分析</h1>
      <p>分析您的对话情感变化，了解情感健康状态</p>
    </div>

    <!-- 情感统计卡片 -->
    <a-row :gutter="24" class="stats-row">
      <a-col :span="6">
        <a-card class="stat-card positive">
          <div class="stat-content">
            <div class="stat-icon">
              <SmileOutlined />
            </div>
            <div class="stat-info">
               <div class="stat-value">{{ ((emotionStats?.positiveRatio || 0) * 100).toFixed(1) }}%</div>
              <div class="stat-label">积极情感</div>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :span="6">
        <a-card class="stat-card negative">
          <div class="stat-content">
            <div class="stat-icon">
              <FrownOutlined />
            </div>
            <div class="stat-info">
               <div class="stat-value">{{ ((emotionStats?.negativeRatio || 0) * 100).toFixed(1) }}%</div>
              <div class="stat-label">消极情感</div>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :span="6">
        <a-card class="stat-card neutral">
          <div class="stat-content">
            <div class="stat-icon">
              <MehOutlined />
            </div>
            <div class="stat-info">
               <div class="stat-value">{{ ((emotionStats?.neutralRatio || 0) * 100).toFixed(1) }}%</div>
              <div class="stat-label">中性情感</div>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :span="6">
        <a-card class="stat-card health">
          <div class="stat-content">
            <div class="stat-icon">
              <HeartOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ emotionStats?.emotionalHealthScore?.toFixed(0) || 0 }}</div>
              <div class="stat-label">情感健康度</div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="24" class="charts-row">
      <!-- 情感趋势图 -->
      <a-col :span="16">
        <a-card title="情感趋势" class="chart-card">
          <div class="chart-controls">
            <a-radio-group v-model:value="trendDays" @change="loadEmotionTrend">
              <a-radio-button :value="7">7天</a-radio-button>
              <a-radio-button :value="30">30天</a-radio-button>
              <a-radio-button :value="90">90天</a-radio-button>
            </a-radio-group>
          </div>
          
          <div class="emotion-chart" ref="trendChartRef"></div>
        </a-card>
      </a-col>
      
      <!-- 情感分布图 -->
      <a-col :span="8">
        <a-card title="情感分布" class="chart-card">
          <div class="emotion-chart" ref="distributionChartRef"></div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 情感详情 -->
    <a-card title="情感详情" class="detail-card">
      <div class="detail-controls">
        <a-select
          v-model:value="selectedConversationId"
          placeholder="选择对话"
          style="width: 200px"
          @change="loadConversationEmotions"
        >
          <a-select-option
            v-for="conversation in conversations"
            :key="conversation.id"
            :value="conversation.id"
          >
            {{ conversation.title }}
          </a-select-option>
        </a-select>
      </div>
      
      <div v-if="conversationEmotions.length > 0" class="emotion-timeline">
        <div
          v-for="emotion in conversationEmotions"
          :key="emotion.id"
          class="emotion-item"
          :class="getEmotionClass(emotion)"
        >
          <div class="emotion-icon">
            <component :is="getEmotionIcon(emotion.emotionType)" />
          </div>
          
          <div class="emotion-content">
            <div class="emotion-header">
              <span class="emotion-type">{{ emotion.emotionDescription }}</span>
              <span class="emotion-time">{{ formatTime(emotion.createdAt) }}</span>
            </div>
            
            <div class="emotion-metrics">
              <div class="metric">
                <span class="metric-label">置信度:</span>
                <span class="metric-value">{{ (emotion.confidence * 100).toFixed(1) }}%</span>
              </div>
              <div class="metric">
                <span class="metric-label">强度:</span>
                <span class="metric-value">{{ emotion.intensityLevel }}</span>
              </div>
              <div class="metric">
                <span class="metric-label">效价:</span>
                <span class="metric-value">{{ emotion.valence?.toFixed(2) }}</span>
              </div>
            </div>
            
            <div v-if="emotion.keywords" class="emotion-keywords">
              <span class="keywords-label">关键词:</span>
              <a-tag
                v-for="keyword in emotion.keywords.split(',')"
                :key="keyword"
                size="small"
              >
                {{ keyword }}
              </a-tag>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-emotions">
        <HeartOutlined class="empty-icon" />
        <p>选择一个对话查看情感分析结果</p>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import {
  SmileOutlined,
  FrownOutlined,
  MehOutlined,
  HeartOutlined,
  ThunderboltOutlined,
  FireOutlined,
  CloudOutlined
} from '@ant-design/icons-vue'
import * as echarts from 'echarts'
import { emotionApi } from '@/api/emotion'
import { chatApi } from '@/api/chat'
import type { EmotionStatsResponse, EmotionTrendResponse, EmotionAnalysisResponse } from '@/types/emotion'
import type { ConversationListResponse } from '@/types/chat'

// 响应式数据
const emotionStats = ref<EmotionStatsResponse | null>(null)
const emotionTrend = ref<EmotionTrendResponse[]>([])
const conversationEmotions = ref<EmotionAnalysisResponse[]>([])
const conversations = ref<ConversationListResponse[]>([])
const selectedConversationId = ref<number | null>(null)
const trendDays = ref(30)

// 图表引用
const trendChartRef = ref<HTMLElement>()
const distributionChartRef = ref<HTMLElement>()

// 方法
const loadEmotionStats = async () => {
  try {
    const response = await emotionApi.getEmotionStats()
    emotionStats.value = response.data
    await nextTick()
    renderDistributionChart()
  } catch (error) {
    console.error('加载情感统计失败:', error)
  }
}

const loadEmotionTrend = async () => {
  try {
    const response = await emotionApi.getEmotionTrend(trendDays.value)
    emotionTrend.value = response.data || []
    await nextTick()
    renderTrendChart()
  } catch (error) {
    console.error('加载情感趋势失败:', error)
  }
}

const loadConversations = async () => {
  try {
    const response = await chatApi.getConversations()
    conversations.value = response.data.content || []
  } catch (error) {
    console.error('加载对话列表失败:', error)
  }
}

const loadConversationEmotions = async () => {
  if (!selectedConversationId.value) return
  
  try {
    const response = await emotionApi.getConversationEmotions(selectedConversationId.value)
    conversationEmotions.value = response.data || []
  } catch (error) {
    console.error('加载对话情感失败:', error)
  }
}

const renderTrendChart = () => {
  if (!trendChartRef.value || !emotionTrend.value.length) return
  
  const chart = echarts.init(trendChartRef.value)
  
  const dates = emotionTrend.value.map(item => item.date)
  const valenceData = emotionTrend.value.map(item => item.averageValence)
  const intensityData = emotionTrend.value.map(item => item.averageIntensity)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['情感效价', '情感强度']
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      min: -1,
      max: 1
    },
    series: [
      {
        name: '情感效价',
        type: 'line',
        data: valenceData,
        smooth: true,
        itemStyle: {
          color: '#1890ff'
        }
      },
      {
        name: '情感强度',
        type: 'line',
        data: intensityData,
        smooth: true,
        itemStyle: {
          color: '#52c41a'
        }
      }
    ]
  }
  
  chart.setOption(option)
}

const renderDistributionChart = () => {
  if (!distributionChartRef.value || !emotionStats.value) return
  
  const chart = echarts.init(distributionChartRef.value)
  
  const data = Object.entries(emotionStats.value.emotionDistribution || {}).map(([name, value]) => ({
    name,
    value
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [
      {
        name: '情感分布',
        type: 'pie',
        radius: '70%',
        data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  chart.setOption(option)
}

const getEmotionClass = (emotion: EmotionAnalysisResponse) => {
  if (emotion.isPositive) return 'positive'
  if (emotion.isNegative) return 'negative'
  return 'neutral'
}

const getEmotionIcon = (emotionType: string) => {
  const iconMap: Record<string, any> = {
    JOY: SmileOutlined,
    SADNESS: FrownOutlined,
    ANGER: FireOutlined,
    FEAR: ThunderboltOutlined,
    SURPRISE: ThunderboltOutlined,
    LOVE: HeartOutlined,
    EXCITEMENT: FireOutlined,
    CALM: CloudOutlined
  }
  return iconMap[emotionType] || MehOutlined
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString()
}

// 生命周期
onMounted(() => {
  loadEmotionStats()
  loadEmotionTrend()
  loadConversations()
})
</script>

<style scoped>
.emotion-analysis {
  padding: 24px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
}

.stat-card.positive {
  border-left: 4px solid #52c41a;
}

.stat-card.negative {
  border-left: 4px solid #ff4d4f;
}

.stat-card.neutral {
  border-left: 4px solid #faad14;
}

.stat-card.health {
  border-left: 4px solid #1890ff;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 32px;
  color: #666;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  height: 400px;
}

.chart-controls {
  margin-bottom: 16px;
  text-align: right;
}

.emotion-chart {
  height: 300px;
}

.detail-card {
  min-height: 400px;
}

.detail-controls {
  margin-bottom: 24px;
}

.emotion-timeline {
  max-height: 500px;
  overflow-y: auto;
}

.emotion-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border-left: 4px solid #d9d9d9;
}

.emotion-item.positive {
  background: #f6ffed;
  border-left-color: #52c41a;
}

.emotion-item.negative {
  background: #fff2f0;
  border-left-color: #ff4d4f;
}

.emotion-item.neutral {
  background: #fffbe6;
  border-left-color: #faad14;
}

.emotion-icon {
  font-size: 20px;
  color: #666;
  margin-top: 4px;
}

.emotion-content {
  flex: 1;
}

.emotion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.emotion-type {
  font-weight: 600;
  color: #333;
}

.emotion-time {
  color: #999;
  font-size: 12px;
}

.emotion-metrics {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.metric {
  font-size: 12px;
}

.metric-label {
  color: #666;
}

.metric-value {
  color: #333;
  font-weight: 500;
  margin-left: 4px;
}

.emotion-keywords {
  margin-top: 8px;
}

.keywords-label {
  color: #666;
  font-size: 12px;
  margin-right: 8px;
}

.empty-emotions {
  text-align: center;
  padding: 80px 20px;
  color: #666;
}

.empty-icon {
  font-size: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .stats-row .ant-col {
    margin-bottom: 16px;
  }
  
  .charts-row .ant-col {
    margin-bottom: 24px;
  }
  
  .emotion-metrics {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
