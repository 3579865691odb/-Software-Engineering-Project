<template>
  <a-spin :spinning="showSpin">
    <div class="main">
      <a-row :gutter="[24, 24]">
        <a-col :sm="24" :md="12" :lg="6">
          <div class="data-card data-card-blue">
            <div class="data-card-icon">
              <shopping-outlined />
            </div>
            <div class="data-card-content">
              <div class="data-card-title">商品总数</div>
              <div class="data-card-value">{{ tdata.data.spzs }}<span class="data-card-unit">种</span></div>
              <div class="data-card-info">
                <rise-outlined v-if="tdata.data.qrxz > 0" />
                <fall-outlined v-else />
                <span>本周新增 {{ tdata.data.qrxz }} 种</span>
              </div>
            </div>
          </div>
        </a-col>

        <a-col :sm="24" :md="12" :lg="6">
          <div class="data-card data-card-green">
            <div class="data-card-icon">
              <clock-circle-outlined />
            </div>
            <div class="data-card-content">
              <div class="data-card-title">未付订单</div>
              <div class="data-card-value">{{ tdata.data.wfdd }}<span class="data-card-unit">单</span></div>
              <div class="data-card-info">
                <team-outlined />
                <span>共 {{ tdata.data.wfddrs }} 人</span>
              </div>
            </div>
          </div>
        </a-col>

        <a-col :sm="24" :md="12" :lg="6">
          <div class="data-card data-card-purple">
            <div class="data-card-icon">
              <credit-card-outlined />
            </div>
            <div class="data-card-content">
              <div class="data-card-title">已付订单</div>
              <div class="data-card-value">{{ tdata.data.yfdd }}<span class="data-card-unit">单</span></div>
              <div class="data-card-info">
                <team-outlined />
                <span>共 {{ tdata.data.yfddrs }} 人</span>
              </div>
            </div>
          </div>
        </a-col>

        <a-col :sm="24" :md="12" :lg="6">
          <div class="data-card data-card-orange">
            <div class="data-card-icon">
              <close-circle-outlined />
            </div>
            <div class="data-card-content">
              <div class="data-card-title">取消订单</div>
              <div class="data-card-value">{{ tdata.data.qxdd }}<span class="data-card-unit">单</span></div>
              <div class="data-card-info">
                <team-outlined />
                <span>共 {{ tdata.data.qxddrs }} 人</span>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>

      <a-card class="chart-card" title="最近一周访问量" :bordered="false">
        <div class="chart-container" ref="visitChartDiv"></div>
      </a-card>

      <a-row :gutter="[24, 24]">
        <a-col :sm="24" :md="24" :lg="12">
          <a-card class="chart-card" title="热门商品排名" :bordered="false">
            <div class="chart-container" ref="barChartDiv"></div>
          </a-card>
        </a-col>
        <a-col :sm="24" :md="24" :lg="12">
          <a-card class="chart-card" title="热门分类比例" :bordered="false">
            <div class="chart-container" ref="pieChartDiv"></div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </a-spin>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted} from 'vue';

import {
  InteractionOutlined,
  StarFilled,
  StarTwoTone,
  RiseOutlined,
  FallOutlined,
  ShoppingOutlined,
  ClockCircleOutlined,
  CreditCardOutlined,
  CloseCircleOutlined,
  TeamOutlined
} from '@ant-design/icons-vue';
import {listApi} from '/@/api/overview'

let showSpin = ref(true)
let chartView = ref('week')

const visitChartDiv = ref()
const barChartDiv = ref()
const pieChartDiv = ref()

let visitChart, barChart, pieChart;

let tdata = reactive({
  data: {}
})

onMounted(() => {
  list()
  window.onresize = function () { // resize
    visitChart?.resize()
    barChart?.resize()
    pieChart?.resize()
  }
})

const list = () => {
  listApi({}).then(res => {
    console.log(res.data)
    tdata.data = res.data
    initCharts()
    initBarChart()
    initPieChart()

    showSpin.value = false
  }).catch(err => {
    showSpin.value = false
  })
}

const initCharts = () => {
  let xData = []
  let uvData = []
  let pvData = []
  tdata.data.visitList.forEach((item, index) => {
    xData.push(item.day)
    uvData.push(item.uv)
    pvData.push(item.pv)
  })
  if (visitChart) {
    visitChart.dispose()
  }
  visitChart = echarts.init(visitChartDiv.value)
  let option = {
    title: {
      text: ''
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e6e6e6',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(0, 0, 0, 0.03)'
        }
      }
    },
    legend: {
      data: ['IP', 'visit'],
      top: '90%',
      left: 'center',
      textStyle: {
        color: '#666'
      }
    },
    grid: {
      top: '30px',
      left: '20px',
      right: '20px',
      bottom: '40px',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      axisLabel: {
        textStyle: {
          color: '#666'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#e6e6e6'
        }
      },
      data: xData
    },
    yAxis: {
      type: 'value',
      axisLine: {show: false},
      axisTick: {show: false},
      splitLine: {
        show: true,
        lineStyle: {
          color: 'rgba(0, 0, 0, 0.05)',
          width: 1,
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: 'IP',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        showSymbol: false,
        lineStyle: {
          width: 3,
          color: '#1890ff'
        },
        itemStyle: {
          color: '#1890ff',
          borderWidth: 2,
          borderColor: '#fff'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(24, 144, 255, 0.3)'
            }, {
              offset: 1, color: 'rgba(24, 144, 255, 0.1)'
            }]
          }
        },
        data: uvData
      },
      {
        name: 'visit',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        showSymbol: false,
        lineStyle: {
          width: 3,
          color: '#52c41a'
        },
        itemStyle: {
          color: '#52c41a',
          borderWidth: 2,
          borderColor: '#fff'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(82, 196, 26, 0.3)'
            }, {
              offset: 1, color: 'rgba(82, 196, 26, 0.1)'
            }]
          }
        },
        data: pvData
      }
    ]
  }
  visitChart.setOption(option)
}

const initBarChart = () => {
  let xData = []
  let yData = []
  tdata.data.popularThings.forEach((item, index) => {
    xData.push(item.title)
    yData.push(item.count)
  })
  if (barChart) {
    barChart.dispose()
  }
  barChart = echarts.init(barChartDiv.value)
  let option = {
    grid: {
      top: '40px',
      left: '40px',
      right: '40px',
      bottom: '40px'
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e6e6e6',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(0, 0, 0, 0.03)'
        }
      }
    },
    xAxis: {
      data: xData,
      type: 'category',
      axisLabel: {
        rotate: 30,
        textStyle: {
          color: '#666'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#e6e6e6'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {show: false},
      axisTick: {show: false},
      splitLine: {
        show: true,
        lineStyle: {
          color: 'rgba(0, 0, 0, 0.05)',
          width: 1,
          type: 'dashed'
        }
      }
    },
    series: [
      {
        data: yData,
        type: 'bar',
        barWidth: '60%',
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: '#1890ff'
            }, {
              offset: 1, color: '#36cfc9'
            }]
          },
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }
  barChart.setOption(option)
}

const initPieChart = () => {
  let pieData = []
  tdata.data.popularClassification.forEach((item, index) => {
    pieData.push({name: item.title, value: item.count})
  })
  if (pieChart) {
    pieChart.dispose()
  }
  pieChart = echarts.init(pieChartDiv.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e6e6e6',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      }
    },
    legend: {
      orient: 'horizontal',
      bottom: '0%',
      left: 'center',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        color: '#666'
      }
    },
    series: [
      {
        name: '分类',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.2)'
          }
        },
        labelLine: {
          show: false
        },
        data: pieData,
        color: ['#1890ff', '#13c2c2', '#52c41a', '#faad14', '#722ed1', '#eb2f96', '#f5222d']
      }
    ]
  }
  pieChart.setOption(option)
}
</script>

<style lang="less" scoped>
.main {
  height: 100%;
  display: flex;
  gap: 24px;
  flex-direction: column;
}

.data-card {
  display: flex;
  align-items: center;
  height: 120px;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    right: 0;
    top: 0;
    width: 100px;
    height: 100%;
    background: linear-gradient(90deg, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.1));
  }

  &.data-card-blue {
    background: linear-gradient(135deg, #1890ff, #096dd9);
  }

  &.data-card-green {
    background: linear-gradient(135deg, #52c41a, #389e0d);
  }

  &.data-card-purple {
    background: linear-gradient(135deg, #722ed1, #531dab);
  }

  &.data-card-orange {
    background: linear-gradient(135deg, #fa8c16, #d46b08);
  }

  .data-card-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    margin-right: 16px;

    :deep(svg) {
      font-size: 24px;
      color: white;
    }
  }

  .data-card-content {
    flex: 1;
    color: white;

    .data-card-title {
      font-size: 14px;
      opacity: 0.8;
      margin-bottom: 8px;
    }

    .data-card-value {
      font-size: 28px;
      font-weight: bold;

      .data-card-unit {
        font-size: 14px;
        font-weight: normal;
        margin-left: 4px;
      }
    }

    .data-card-info {
      display: flex;
      align-items: center;
      margin-top: 8px;
      font-size: 12px;
      opacity: 0.8;

      :deep(svg) {
        margin-right: 4px;
      }
    }
  }
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

  :deep(.ant-card-head) {
    border-bottom: 1px solid #f0f0f0;
    padding: 0 24px;

    .ant-card-head-title {
      font-size: 16px;
      font-weight: 600;
      color: #000000d9;
    }
  }

  .chart-container {
    height: 320px;
    width: 100%;
  }
}
</style>
