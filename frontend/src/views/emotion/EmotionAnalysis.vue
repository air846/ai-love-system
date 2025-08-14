<template>
  <div class="emotion-analysis-container">
    <div class="page-header">
      <h1>情感洞察中心</h1>
      <p>深入分析您的对话，全面掌握情感动态与核心议题</p>
    </div>

    <!-- 数据概览面板 -->
    <a-card class="data-overview-panel" :bordered="false">
      <a-row :gutter="[24, 24]">
        <!-- 情感趋势折线图 -->
        <a-col :xs="24" :sm="24" :md="24" :lg="12">
          <div class="chart-container">
            <div class="chart-header">
              <h3>情感趋势</h3>
              <a-radio-group v-model:value="trendDays" @change="loadEmotionTrend" size="small">
                <a-radio-button :value="7">近7天</a-radio-button>
                <a-radio-button :value="30">近30天</a-radio-button>
                <a-radio-button :value="90">近90天</a-radio-button>
              </a-radio-group>
            </div>
            <div class="chart-body" ref="trendChartRef" style="height: 300px;"></div>
          </div>
        </a-col>

        <!-- 情感分布饼图 -->
        <a-col :xs="24" :sm="24" :md="12" :lg="6">
          <div class="chart-container">
            <div class="chart-header">
              <h3>情感分布</h3>
            </div>
            <div class="chart-body" ref="distributionChartRef" style="height: 300px;"></div>
          </div>
        </a-col>

        <!-- 关键词词云 -->
        <a-col :xs="24" :sm="24" :md="12" :lg="6">
          <div class="chart-container">
            <div class="chart-header">
              <h3>核心关键词</h3>
            </div>
            <div class="chart-body" ref="wordCloudChartRef" style="height: 300px;"></div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 情感详情 -->
    <a-card title="情感日志" class="detail-card" :bordered="false">
      <template #extra>
        <a-select
          v-model:value="selectedConversationId"
          placeholder="选择一个对话进行回溯"
          style="width: 240px"
          @change="loadConversationEmotions"
          allow-clear
        >
          <a-select-option
            v-for="conversation in conversations"
            :key="conversation.id"
            :value="conversation.id"
          >
            {{ conversation.title }}
          </a-select-option>
        </a-select>
      </template>
      
      <div v-if="conversationEmotions.length > 0" class="emotion-timeline">
        <a-timeline>
          <a-timeline-item 
            v-for="emotion in conversationEmotions" 
            :key="emotion.id"
            :color="getEmotionColor(emotion)"
          >
            <div class="timeline-item-content">
              <div class="timeline-header">
                <span class="emotion-type">
                  <component :is="getEmotionIcon(emotion.emotionType)" class="emotion-icon" />
                  {{ emotion.emotionDescription }}
                </span>
                <span class="emotion-time">{{ formatTime(emotion.createdAt) }}</span>
              </div>
              <div class="emotion-metrics">
                <span>效价: {{ emotion.valence?.toFixed(2) }}</span>
                <span>强度: {{ emotion.intensityLevel }}</span>
                <span>置信度: {{ (emotion.confidence * 100).toFixed(1) }}%</span>
              </div>
              <div v-if="emotion.keywords" class="emotion-keywords">
                <a-tag
                  v-for="keyword in emotion.keywords.split(',')"
                  :key="keyword"
                  :color="getEmotionColor(emotion)"
                >
                  {{ keyword }}
                </a-tag>
              </div>
            </div>
          </a-timeline-item>
        </a-timeline>
      </div>
      
      <a-empty v-else description="选择一个对话，开始您的情感回溯之旅" class="empty-emotions" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import {
  SmileOutlined, FrownOutlined, MehOutlined, HeartOutlined,
  ThunderboltOutlined, FireOutlined, CloudOutlined
} from '@ant-design/icons-vue';
import * as echarts from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart, LineChart } from 'echarts/charts';
import {
  TitleComponent, TooltipComponent, LegendComponent, GridComponent,
  DataZoomComponent
} from 'echarts/components';
import 'echarts-wordcloud';
import { emotionApi } from '@/api/emotion';
import { chatApi } from '@/api/chat';
import type { EmotionStatsResponse, EmotionTrendResponse, EmotionAnalysisResponse } from '@/types/emotion';
import type { ConversationListResponse } from '@/types/chat';

echarts.use([
  CanvasRenderer, PieChart, LineChart, TitleComponent, TooltipComponent,
  LegendComponent, GridComponent, DataZoomComponent
]);

// -- 响应式数据 --
const emotionStats = ref<EmotionStatsResponse | null>(null);
const emotionTrend = ref<EmotionTrendResponse[]>([]);
const conversationEmotions = ref<EmotionAnalysisResponse[]>([]);
const conversations = ref<ConversationListResponse[]>([]);
const selectedConversationId = ref<number | null>(null);
const trendDays = ref(30);

// -- 图表实例引用 --
const trendChartRef = ref<HTMLElement>();
const distributionChartRef = ref<HTMLElement>();
const wordCloudChartRef = ref<HTMLElement>();

// -- 色彩方案 --
const colors = {
  positive: '#6dd400',
  negative: '#ff4d4f',
  neutral: '#ffc400',
  primary: '#1677ff',
  text: '#333',
  background: '#f0f2f5'
};

// -- API 调用 --
const loadEmotionStats = async () => {
  try {
    const response = await emotionApi.getEmotionStats();
    emotionStats.value = response.data;
    await nextTick();
    renderDistributionChart();
    renderWordCloudChart();
  } catch (error) {
    console.error('加载情感统计失败:', error);
  }
};

const loadEmotionTrend = async () => {
  try {
    const response = await emotionApi.getEmotionTrend(trendDays.value);
    emotionTrend.value = response.data || [];
    await nextTick();
    renderTrendChart();
  } catch (error) {
    console.error('加载情感趋势失败:', error);
  }
};

const loadConversations = async () => {
  try {
    const response = await chatApi.getConversations();
    conversations.value = response.data.content || [];
  } catch (error) {
    console.error('加载对话列表失败:', error);
  }
};

const loadConversationEmotions = async () => {
  if (!selectedConversationId.value) {
    conversationEmotions.value = [];
    return;
  }
  try {
    const response = await emotionApi.getConversationEmotions(selectedConversationId.value);
    conversationEmotions.value = response.data || [];
  } catch (error) {
    console.error('加载对话情感失败:', error);
  }
};

// -- 图表渲染 --
const renderTrendChart = () => {
  if (!trendChartRef.value || !emotionTrend.value.length) return;
  const chart = echarts.init(trendChartRef.value);
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['情感效价', '情感强度'], bottom: 0 },
    grid: { top: '10%', left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: { type: 'category', data: emotionTrend.value.map(item => item.date) },
    yAxis: { type: 'value', min: -1, max: 1 },
    dataZoom: [{ type: 'inside', start: 0, end: 100 }, { type: 'slider', start: 0, end: 100, height: 20, bottom: '5%' }],
    series: [
      { name: '情感效价', type: 'line', smooth: true, data: emotionTrend.value.map(item => item.averageValence), itemStyle: { color: colors.primary } },
      { name: '情感强度', type: 'line', smooth: true, data: emotionTrend.value.map(item => item.averageIntensity), itemStyle: { color: colors.positive } }
    ]
  };
  chart.setOption(option);
};

const renderDistributionChart = () => {
  if (!distributionChartRef.value || !emotionStats.value) return;
  const chart = echarts.init(distributionChartRef.value);
  const data = [
    { value: emotionStats.value.positiveRatio, name: '积极', itemStyle: { color: colors.positive } },
    { value: emotionStats.value.negativeRatio, name: '消极', itemStyle: { color: colors.negative } },
    { value: emotionStats.value.neutralRatio, name: '中性', itemStyle: { color: colors.neutral } },
  ];
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {d}%' },
    legend: { orient: 'vertical', left: 'left', top: 'center' },
    series: [{
      name: '情感分布', type: 'pie', radius: ['50%', '70%'],
      avoidLabelOverlap: false,
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
      labelLine: { show: false },
      data
    }]
  };
  chart.setOption(option);
};

const renderWordCloudChart = () => {
  if (!wordCloudChartRef.value || !emotionStats.value?.keywords) return;
  const chart = echarts.init(wordCloudChartRef.value);
  const option = {
    tooltip: { show: true },
    series: [{
      type: 'wordCloud',
      shape: 'circle',
      sizeRange: [12, 50],
      rotationRange: [-90, 90],
      textStyle: {
        fontFamily: 'sans-serif',
        fontWeight: 'bold',
        color: () => `rgb(${[Math.round(Math.random() * 160), Math.round(Math.random() * 160), Math.round(Math.random() * 160)].join(',')})`
      },
      emphasis: { focus: 'self', textStyle: { textShadowBlur: 10, textShadowColor: '#333' } },
      data: emotionStats.value.keywords
    }]
  };
  chart.setOption(option);
};

// -- 辅助函数 --
const getEmotionColor = (emotion: EmotionAnalysisResponse) => {
  if (emotion.isPositive) return colors.positive;
  if (emotion.isNegative) return colors.negative;
  return colors.neutral;
};

const getEmotionIcon = (emotionType: string) => {
  const iconMap: Record<string, any> = {
    JOY: SmileOutlined, SADNESS: FrownOutlined, ANGER: FireOutlined,
    FEAR: ThunderboltOutlined, SURPRISE: ThunderboltOutlined, LOVE: HeartOutlined,
    EXCITEMENT: FireOutlined, CALM: CloudOutlined
  };
  return iconMap[emotionType] || MehOutlined;
};

const formatTime = (time: string) => new Date(time).toLocaleString();

// -- 生命周期钩子 --
onMounted(() => {
  loadEmotionStats();
  loadEmotionTrend();
  loadConversations();
  
  window.addEventListener('resize', () => {
    echarts.getInstanceByDom(trendChartRef.value!)?.resize();
    echarts.getInstanceByDom(distributionChartRef.value!)?.resize();
    echarts.getInstanceByDom(wordCloudChartRef.value!)?.resize();
  });
});
</script>

<style scoped>
.emotion-analysis-container {
  padding: 24px;
  background-color: var(--color-bg-1);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-text-1);
}

.page-header p {
  font-size: 14px;
  color: var(--color-text-2);
}

.data-overview-panel {
  margin-bottom: 24px;
  background: var(--color-bg-2);
  border-radius: 8px;
}

.chart-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 8px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-1);
}

.chart-body {
  flex-grow: 1;
}

.detail-card {
  background: var(--color-bg-2);
  border-radius: 8px;
}

.emotion-timeline {
  max-height: 600px;
  overflow-y: auto;
  padding: 0 12px;
}

.timeline-item-content {
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.timeline-item-content:hover {
  background-color: var(--color-fill-2);
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.emotion-type {
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.emotion-icon {
  font-size: 16px;
}

.emotion-time {
  font-size: 12px;
  color: var(--color-text-3);
}

.emotion-metrics {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--color-text-2);
  margin-bottom: 8px;
}

.emotion-keywords .ant-tag {
  margin-top: 4px;
}

.empty-emotions {
  padding: 40px 0;
}
</style>
