<template>
  <!-- 模板部分完全不变 -->
  <div class="music-page">
    <!-- 顶部导航 -->
    <div class="header-container">
      <div class="header-content">
        <div class="logo" @click="$router.push({name:'portal'})">
          <img src="@/assets/images/shop-logo.png" class="logo-image">
          <span class="logo-text">乐器商城</span>
        </div>
        
        <div class="search-collect-container">
          <div class="search-entry">
            <input placeholder="搜索音乐/艺术家" v-model="searchKeyword" @keyup.enter="handleSearch" />
            <button class="search-btn" @click="handleSearch">
              <img src="@/assets/icons/search.png" class="icon" alt="搜索">
            </button>
          </div>
          
          <!-- 我的收藏按钮 -->
          <button class="my-collect-btn" @click="showCollectList = !showCollectList">
            <img src="@/assets/icons/collect-btn.png" class="icon" alt="我的收藏">
            <span>我的收藏</span>
          </button>
        </div>
        
        <div class="right-view">
          <div class="nav-menu">
            <router-link to="/index/portal" class="nav-menu-item" :class="{ active: route.name === 'portal' }">首页</router-link>
            <router-link to="/index/cart" class="nav-menu-item" :class="{ active: route.name === 'cartView' }">购物车</router-link>
            <router-link to="/index/couponCenter" class="nav-menu-item" :class="{ active: route.name === 'couponCenter' }">优惠券</router-link>
            <router-link to="/index/music" class="nav-menu-item" :class="{ active: route.name === 'musicView' }">乐谱</router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 音乐页面主体 -->
    <div class="main-content">
      <div class="page-header">
        <h1 class="page-title">音乐资源中心</h1>
        <p class="page-desc">精选乐器演奏曲库、教学音频及伴奏资源</p>
      </div>

      <!-- 分类筛选栏 -->
      <div class="filter-bar">
        <div class="filter-tag" 
             v-for="(item, index) in categoryList" 
             :key="index"
             :class="{ active: activeCategory === index }"
             @click="activeCategory = index">
          {{ item }}
        </div>
      </div>

      <!-- 音乐列表区域 -->
      <div class="music-list">
        <div class="music-item" v-for="(music, idx) in filteredAndSearchedMusicList" :key="idx">
          <div class="music-info">
            <img :src="music.cover" class="music-cover" alt="专辑封面">
            <div class="music-detail">
              <h3 class="music-title">{{ music.title }}</h3>
              <p class="music-artist">{{ music.artist }} · {{ music.instrument }}</p>
              <p class="music-duration">{{ formatDuration(music.duration) }}</p>
            </div>
          </div>
          <div class="music-actions">
            <!-- 收藏按钮（纯CSS实现） -->
            <button class="action-btn collect" @click="toggleCollect(music.id)">
              <span class="icon" :class="isCollected(music.id) ? 'collected' : 'uncollected'"></span>
            </button>
            <!-- 下载按钮 -->
            <button class="action-btn download" @click="downloadMusic(music)">
              <img src="@/assets/icons/download.png" class="icon" alt="下载">
            </button>
            <!-- 播放按钮（纯CSS实现） -->
            <button class="play-btn" @click="playMusic(music, idx)">
              <span class="icon" :class="isMusicPlaying(idx) ? 'pause' : 'play'"></span>
            </button>
          </div>
        </div>
        <p class="empty-tip" v-if="filteredAndSearchedMusicList.length === 0">暂无音乐资源</p>
      </div>

      <!-- 我的收藏弹窗 -->
      <div class="collect-popup" v-if="showCollectList">
        <div class="popup-header">
          <h3>我的收藏</h3>
          <button class="close-btn" @click="showCollectList = false">
            <img src="@/assets/icons/close.png" class="icon" alt="关闭">
          </button>
        </div>
        <div class="popup-content">
          <div class="collect-item" v-for="(item, idx) in collectedMusicList" :key="item.id">
            <div class="collect-info">
              <img :src="item.cover" class="collect-cover">
              <div class="collect-detail">
                <p class="collect-title">{{ item.title }}</p>
                <p class="collect-artist">{{ item.artist }} · {{ item.instrument }}</p>
              </div>
            </div>
            <div class="collect-actions">
              <!-- 收藏弹窗中的播放按钮也改为纯CSS实现 -->
              <button class="action-btn play" @click="playMusic(item, getMusicIndex(item.id))">
                <span class="icon play"></span>
              </button>
              <button class="action-btn cancel" @click="toggleCollect(item.id)">
                <img src="@/assets/icons/delete.png" class="icon" alt="取消收藏">
              </button>
            </div>
          </div>
          <p class="empty-tip" v-if="collectedMusicList.length === 0">暂无收藏的音乐</p>
        </div>
      </div>

      <!-- 底部播放器 -->
      <div class="music-player" v-if="currentMusic">
        <div class="player-info">
          <img :src="currentMusic.cover" class="player-cover" alt="当前播放">
          <div class="player-detail">
            <p class="player-title">{{ currentMusic.title }}</p>
            <p class="player-artist">{{ currentMusic.artist }}</p>
          </div>
        </div>
        <div class="player-controls">
          <button class="control-btn prev-btn" @click="playPrev">
            <!-- 上一首按钮纯CSS实现 -->
            <span class="icon prev"></span>
          </button>
          <button class="control-btn play-pause-btn" @click="togglePlay">
            <!-- 播放/暂停按钮纯CSS实现 -->
            <span class="icon" :class="isPlaying ? 'pause' : 'play'"></span>
          </button>
          <button class="control-btn next-btn" @click="playNext">
            <!-- 下一首按钮纯CSS实现 -->
            <span class="icon next"></span>
          </button>
        </div>
        <div class="player-progress">
          <span class="current-time">{{ formatDuration(currentTime) }}</span>
          <div class="progress-bar" @click="handleProgressClick">
            <div class="progress-played" :style="{ width: progress + '%' }"></div>
            <div class="progress-handle" :style="{ left: progress + '%' }" @mousedown="startDrag"></div>
          </div>
          <span class="total-time">{{ formatDuration(currentMusic.duration) }}</span>
        </div>
      </div>

      <!-- 音频元素 (隐藏) -->
      <audio ref="audioRef" @timeupdate="updateProgress" @ended="playNext" @error="handleAudioError"></audio>
    </div>
  </div>
</template>

<script setup lang="ts">
// 脚本部分完全不变
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from "/@/store";
import { BASE_URL } from "/@/store/constants";
import AvatarIcon from '/@/assets/images/avatar.jpg';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 搜索与收藏相关
const searchKeyword = ref('');
const showCollectList = ref(false);
const collectedMusicList = ref<any[]>([]);

// 音乐分类与数据 - 添加了真实的音频URL
const categoryList = ref([
  '全部音乐',
  '钢琴',
  '吉他',
  '小提琴',
  '古筝',
  '架子鼓'
]);
const activeCategory = ref(0);

// 音乐列表 - 使用真实可播放的音频链接
// 音乐列表 - 扩展至50首，涵盖多种乐器和风格
const musicList = ref([
  // 钢琴
  {
    id: 1,
    title: '月光奏鸣曲',
    artist: '贝多芬',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano1/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3',
    duration: 245,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3'
  },
  {
    id: 2,
    title: '致爱丽丝',
    artist: '贝多芬',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano2/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3',
    duration: 170,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3'
  },
  {
    id: 3,
    title: '梦中的婚礼',
    artist: '理查德·克莱德曼',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano3/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3',
    duration: 195,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3'
  },
  {
    id: 4,
    title: '卡农变奏曲',
    artist: '乔治·温斯顿',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano4/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3',
    duration: 220,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3'
  },
  {
    id: 5,
    title: '蓝色多瑙河',
    artist: '约翰·施特劳斯',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano5/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3',
    duration: 280,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3'
  },
  {
    id: 6,
    title: '秋日私语',
    artist: '理查德·克莱德曼',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano6/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3',
    duration: 210,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3'
  },
  {
    id: 7,
    title: '土耳其进行曲',
    artist: '莫扎特',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano7/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3',
    duration: 185,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3'
  },
  {
    id: 8,
    title: '夜的钢琴曲五',
    artist: '石进',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano8/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3',
    duration: 150,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3'
  },
  {
    id: 9,
    title: '少女的祈祷',
    artist: '巴达捷芙斯卡',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano9/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3',
    duration: 190,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3'
  },
  {
    id: 10,
    title: '星空',
    artist: '理查德·克莱德曼',
    instrument: '钢琴',
    cover: 'https://picsum.photos/seed/piano10/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3',
    duration: 230,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3'
  },

  // 吉他
  {
    id: 11,
    title: '卡农',
    artist: '帕赫贝尔',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar1/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3',
    duration: 180,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3'
  },
  {
    id: 12,
    title: '爱的罗曼史',
    artist: '纳西索·耶佩斯',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar2/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3',
    duration: 200,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3'
  },
  {
    id: 13,
    title: '阿尔罕布拉宫的回忆',
    artist: '塔雷加',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar3/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3',
    duration: 240,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3'
  },
  {
    id: 14,
    title: '加州旅馆',
    artist: '老鹰乐队',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar4/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-17.mp3',
    duration: 300,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-17.mp3'
  },
  {
    id: 15,
    title: '天空之城',
    artist: '久石让',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar5/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-18.mp3',
    duration: 175,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-18.mp3'
  },
  {
    id: 16,
    title: '泪',
    artist: '塔雷加',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar6/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-19.mp3',
    duration: 160,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-19.mp3'
  },
  {
    id: 17,
    title: 'Wonderful Tonight',
    artist: '埃里克·克莱普顿',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar7/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-20.mp3',
    duration: 225,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-20.mp3'
  },
  {
    id: 18,
    title: '安妮的仙境',
    artist: '班得瑞',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar8/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-21.mp3',
    duration: 190,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-21.mp3'
  },
  {
    id: 19,
    title: '寂静之声',
    artist: '保罗·西蒙',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar9/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-22.mp3',
    duration: 210,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-22.mp3'
  },
  {
    id: 20,
    title: 'Yesterday',
    artist: '披头士',
    instrument: '吉他',
    cover: 'https://picsum.photos/seed/guitar10/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-23.mp3',
    duration: 180,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-23.mp3'
  },

  // 小提琴
  {
    id: 21,
    title: '梁祝',
    artist: '经典民乐',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin1/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3',
    duration: 320,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3'
  },
  {
    id: 22,
    title: '流浪者之歌',
    artist: '萨拉萨蒂',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin2/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-24.mp3',
    duration: 290,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-24.mp3'
  },
  {
    id: 23,
    title: '卡农',
    artist: '帕赫贝尔',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin3/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-25.mp3',
    duration: 200,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-25.mp3'
  },
  {
    id: 24,
    title: '沉思',
    artist: '马斯涅',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin4/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-26.mp3',
    duration: 150,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-26.mp3'
  },
  {
    id: 25,
    title: '天鹅',
    artist: '圣桑',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin5/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-27.mp3',
    duration: 180,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-27.mp3'
  },
  {
    id: 26,
    title: '查尔达什舞曲',
    artist: '蒙蒂',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin6/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-28.mp3',
    duration: 240,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-28.mp3'
  },
  {
    id: 27,
    title: '爱的礼赞',
    artist: '埃尔加',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin7/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-29.mp3',
    duration: 160,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-29.mp3'
  },
  {
    id: 28,
    title: '西西里舞曲',
    artist: '福雷',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin8/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-30.mp3',
    duration: 170,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-30.mp3'
  },
  {
    id: 29,
    title: '四季·春',
    artist: '维瓦尔第',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin9/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-31.mp3',
    duration: 230,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-31.mp3'
  },
  {
    id: 30,
    title: '小夜曲',
    artist: '舒伯特',
    instrument: '小提琴',
    cover: 'https://picsum.photos/seed/violin10/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-32.mp3',
    duration: 190,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-32.mp3'
  },

  // 古筝
  {
    id: 31,
    title: '渔舟唱晚',
    artist: '传统曲目',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng1/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3',
    duration: 210,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3'
  },
  {
    id: 32,
    title: '高山流水',
    artist: '传统曲目',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng2/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-33.mp3',
    duration: 280,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-33.mp3'
  },
  {
    id: 33,
    title: '春江花月夜',
    artist: '传统曲目',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng3/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-34.mp3',
    duration: 300,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-34.mp3'
  },
  {
    id: 34,
    title: '汉宫秋月',
    artist: '传统曲目',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng4/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-35.mp3',
    duration: 220,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-35.mp3'
  },
  {
    id: 35,
    title: '梅花三弄',
    artist: '传统曲目',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng5/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-36.mp3',
    duration: 240,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-36.mp3'
  },
  {
    id: 36,
    title: '广陵散',
    artist: '传统曲目',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng6/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-37.mp3',
    duration: 260,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-37.mp3'
  },
  {
    id: 37,
    title: '战台风',
    artist: '王昌元',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng7/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-38.mp3',
    duration: 230,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-38.mp3'
  },
  {
    id: 38,
    title: '浏阳河',
    artist: '传统改编',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng8/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-39.mp3',
    duration: 180,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-39.mp3'
  },
  {
    id: 39,
    title: '洞庭新歌',
    artist: '传统改编',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng9/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-40.mp3',
    duration: 195,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-40.mp3'
  },
  {
    id: 40,
    title: '雪山春晓',
    artist: '范上娥',
    instrument: '古筝',
    cover: 'https://picsum.photos/seed/guzheng10/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-41.mp3',
    duration: 225,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-41.mp3'
  },

  // 架子鼓
  {
    id: 41,
    title: 'Rock Beat',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums1/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3',
    duration: 150,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3'
  },
  {
    id: 42,
    title: 'Jazz Fusion',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums2/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-42.mp3',
    duration: 180,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-42.mp3'
  },
  {
    id: 43,
    title: 'Blues Shuffle',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums3/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-43.mp3',
    duration: 200,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-43.mp3'
  },
  {
    id: 44,
    title: 'Funk Groove',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums4/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-44.mp3',
    duration: 170,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-44.mp3'
  },
  {
    id: 45,
    title: 'Metal Blast',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums5/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-45.mp3',
    duration: 160,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-45.mp3'
  },
  {
    id: 46,
    title: 'Latin Rhythm',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums6/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-46.mp3',
    duration: 190,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-46.mp3'
  },
  {
    id: 47,
    title: 'Reggae Beat',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums7/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-47.mp3',
    duration: 210,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-47.mp3'
  },
  {
    id: 48,
    title: 'Pop Drums',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums8/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-48.mp3',
    duration: 185,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-48.mp3'
  },
  {
    id: 49,
    title: 'Punk Rock',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums9/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-49.mp3',
    duration: 155,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-49.mp3'
  },
  {
    id: 50,
    title: 'Drum Solo',
    artist: 'Unknown',
    instrument: '架子鼓',
    cover: 'https://picsum.photos/seed/drums10/200/200',
    url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-50.mp3',
    duration: 230,
    downloadUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-50.mp3'
  }
]);

// 筛选+搜索后的音乐列表
const filteredAndSearchedMusicList = computed(() => {
  let result = activeCategory.value === 0 
    ? musicList.value 
    : musicList.value.filter(item => item.instrument === categoryList.value[activeCategory.value]);
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(item => 
      item.title.toLowerCase().includes(keyword) || 
      item.artist.toLowerCase().includes(keyword)
    );
  }
  return result;
});

// 播放相关状态
const currentMusic = ref<any>(null);
const currentIndex = ref(-1);
const isPlaying = ref(false);
const currentTime = ref(0);
const progress = ref(0);
const audioRef = ref<HTMLAudioElement | null>(null);

// 音频错误处理
const handleAudioError = () => {
  alert('音频播放失败，请尝试其他音乐');
  isPlaying.value = false;
};

// 更新播放进度
const updateProgress = () => {
  if (!audioRef.value || !currentMusic.value) return;
  
  currentTime.value = audioRef.value.currentTime;
  const duration = audioRef.value.duration || currentMusic.value.duration;
  progress.value = (currentTime.value / duration) * 100;
};

// 用户信息
const userAvatar = ref('');
const nickName = ref('');

const getUserInfo = () => {
  let userId = userStore.user_id
  if (!userId) return
  // 实际项目中替换为用户信息接口
};

onMounted(() => {
  getUserInfo();
  // 从本地存储加载收藏列表
  const saved = localStorage.getItem('collectedMusic');
  if (saved) collectedMusicList.value = JSON.parse(saved);
  
  // 获取audio元素引用
  audioRef.value = document.querySelector('audio') as HTMLAudioElement;
});

// 格式化时长（分:秒）
const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

// 判断音乐是否正在播放
const isMusicPlaying = (idx: number) => {
  return currentIndex.value === idx && isPlaying.value;
};

// 判断音乐是否已收藏
const isCollected = (id: number) => {
  return collectedMusicList.value.some(item => item.id === id);
};

// 切换收藏状态
const toggleCollect = (id: number) => {
  const index = collectedMusicList.value.findIndex(item => item.id === id);
  if (index > -1) {
    // 取消收藏
    collectedMusicList.value.splice(index, 1);
  } else {
    // 添加收藏
    const music = musicList.value.find(item => item.id === id);
    if (music) collectedMusicList.value.push({...music});
  }
  // 保存到本地存储
  localStorage.setItem('collectedMusic', JSON.stringify(collectedMusicList.value));
};

// 下载音乐
const downloadMusic = (music: any) => {
  if (!music.downloadUrl) {
    alert('暂无下载资源');
    return;
  }
  const link = document.createElement('a');
  link.href = music.downloadUrl;
  link.download = `${music.title}-${music.artist}.mp3`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

// 搜索处理
const handleSearch = () => {
  // 搜索逻辑已在computed中实现
};

// 获取音乐在当前列表中的索引
const getMusicIndex = (id: number) => {
  return filteredAndSearchedMusicList.value.findIndex(item => item.id === id);
};

// 播放音乐
const playMusic = (music: any, idx: number) => {
  if (!audioRef.value) return;

  // 如果点击的是正在播放的音乐，则切换播放状态
  if (currentIndex.value === idx) {
    togglePlay();
    return;
  }

  // 播放新音乐
  currentMusic.value = music;
  currentIndex.value = idx;
  
  audioRef.value.src = music.url;
  audioRef.value.load(); // 加载音频
  audioRef.value.play()
    .then(() => {
      isPlaying.value = true;
    })
    .catch(err => {
      console.error('播放失败:', err);
      alert('播放失败: ' + err.message);
      isPlaying.value = false;
    });
};

// 暂停/继续播放
const togglePlay = () => {
  if (!audioRef.value) return;
  
  if (isPlaying.value) {
    audioRef.value.pause();
  } else {
    audioRef.value.play().catch(err => {
      console.error('播放失败:', err);
      alert('播放失败: ' + err.message);
    });
  }
  isPlaying.value = !isPlaying.value;
};

// 上一首（修复版）
const playPrev = () => {
  if (filteredAndSearchedMusicList.value.length === 0) return;
  
  // 计算上一首索引，循环播放
  const prevIndex = (currentIndex.value - 1 + filteredAndSearchedMusicList.value.length) % filteredAndSearchedMusicList.value.length;
  // 获取上一首音乐信息
  const prevMusic = filteredAndSearchedMusicList.value[prevIndex];
  // 播放上一首（强制播放，不检查是否是当前播放的音乐）
  currentMusic.value = prevMusic;
  currentIndex.value = prevIndex;
  
  if (audioRef.value) {
    audioRef.value.src = prevMusic.url;
    audioRef.value.load();
    audioRef.value.play()
      .then(() => {
        isPlaying.value = true;
      })
      .catch(err => {
        console.error('播放失败:', err);
        alert('播放失败: ' + err.message);
        isPlaying.value = false;
      });
  }
};

// 下一首（修复版）
const playNext = () => {
  if (filteredAndSearchedMusicList.value.length === 0) return;
  
  // 计算下一首索引，循环播放
  const nextIndex = (currentIndex.value + 1) % filteredAndSearchedMusicList.value.length;
  // 获取下一首音乐信息
  const nextMusic = filteredAndSearchedMusicList.value[nextIndex];
  // 播放下一首
  playMusic(nextMusic, nextIndex);
};

// 点击进度条调整播放位置
const handleProgressClick = (e: MouseEvent) => {
  if (!audioRef.value || !currentMusic.value) return;
  
  const bar = e.currentTarget as HTMLElement;
  const barRect = bar.getBoundingClientRect();
  const percent = (e.clientX - barRect.left) / barRect.width * 100;
  setProgress(percent);
};

// 进度条拖拽
const startDrag = (e: MouseEvent) => {
  e.stopPropagation();
  if (!audioRef.value || !currentMusic.value) return;
  
  const barRect = (e.currentTarget as HTMLElement).parentElement?.getBoundingClientRect();
  if (!barRect) return;
  
  const updateProgress = (clientX: number) => {
    const percent = Math.max(0, Math.min(100, (clientX - barRect.left) / barRect.width * 100));
    setProgress(percent);
  };
  
  const onMouseMove = (e: MouseEvent) => updateProgress(e.clientX);
  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove);
    document.removeEventListener('mouseup', onMouseUp);
  };
  
  document.addEventListener('mousemove', onMouseMove);
  document.addEventListener('mouseup', onMouseUp);
};

// 设置播放进度
const setProgress = (percent: number) => {
  if (!audioRef.value || !currentMusic.value) return;
  
  progress.value = percent;
  const duration = audioRef.value.duration || currentMusic.value.duration;
  const newTime = (percent / 100) * duration;
  currentTime.value = newTime;
  audioRef.value.currentTime = newTime;
};

// 组件销毁时清理
onUnmounted(() => {
  if (audioRef.value) {
    audioRef.value.pause();
    audioRef.value.removeEventListener('timeupdate', updateProgress);
    audioRef.value.removeEventListener('ended', playNext);
    audioRef.value.removeEventListener('error', handleAudioError);
  }
});

// 导航栏相关方法
const goLogin = () => {
  router.push({name: 'login'})
}

const goUserCenter = (menuName: string) => {
  router.push({name: menuName})
}

const quit= () => {
  userStore.logout().then(res => {
    router.push({name: 'portal'})
  })
}
</script>

<style scoped lang="less">
/* 只修改未收藏状态为五角星样式，其他样式不变 */
// 顶部导航样式（居中显示）
.header-container {
  width: 100%;
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  z-index: 1000;
  display: flex;
  justify-content: center; // 容器居中
  align-items: center;
  padding: 0 20px;

  .header-content {
    width: 100%;
    max-width: 1200px; // 限制最大宽度
    display: flex;
    align-items: center;
  }

  .logo {
    display: flex;
    align-items: center;
    cursor: pointer;
    margin-right: 40px;

    .logo-image {
      width: 40px;
      height: 40px;
      margin-right: 10px;
    }

    .logo-text {
      font-size: 20px;
      font-weight: bold;
      color: #ff4d4f;
    }
  }

  // 搜索框和收藏按钮容器
  .search-collect-container {
    display: flex;
    align-items: center;
    gap: 15px;
    flex: 1; // 占据中间空间
  }

  .search-entry {
    position: relative;
    width: 500px;
    height: 40px;
    display: flex;

    input {
      width: 100%;
      height: 100%;
      border: 2px solid #ff4d4f;
      border-radius: 20px 0 0 20px;
      padding: 0 15px;
      font-size: 14px;
      outline: none;

      &::placeholder {
        color: #999;
      }
    }

    .search-btn {
      width: 80px;
      height: 40px;
      background: #ff4d4f;
      border: none;
      border-radius: 0 20px 20px 0;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s;

      &:hover {
        background: #ff3a3d;
      }

      .icon {
        width: 20px;
        height: 20px;
        object-fit: contain;
      }
    }
  }

  // 我的收藏按钮
  .my-collect-btn {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 6px 12px;
    background: #f5f5f5;
    border: 1px solid #eee;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #eee;
    }

    .icon {
      width: 18px;
      height: 18px;
      object-fit: contain;
    }

    span {
      font-size: 14px;
      color: #333;
    }
  }

  .right-view {
    display: flex;
    align-items: center;
    gap: 20px;

    .nav-menu {
      display: flex;
      gap: 5px;

      .nav-menu-item {
        padding: 0 15px;
        font-size: 16px;
        color: #333;
        text-decoration: none;
        position: relative;
        height: 60px;
        line-height: 60px;
        transition: all 0.3s;
        cursor: pointer;

        &:hover, &.active {
          color: #ff4d4f;
        }

        &.active:after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 20px;
          height: 3px;
          background-color: #ff4d4f;
        }
      }
    }
  }
}

// 页面主体样式
.main-content {
  width: 1200px;
  margin: 20px auto 0;
  padding: 20px;
  padding-bottom: 100px; // 预留播放器空间
}

// 页面标题栏
.page-header {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 30px;

  .page-title {
    font-size: 24px;
    color: #333;
    margin-bottom: 10px;
  }

  .page-desc {
    font-size: 14px;
    color: #666;
  }
}

// 分类筛选栏
.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
  flex-wrap: wrap;

  .filter-tag {
    padding: 6px 16px;
    background: #f5f5f5;
    border-radius: 20px;
    font-size: 14px;
    color: #666;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #eee;
    }

    &.active {
      background: #ff4d4f;
      color: #fff;
    }
  }
}

// 音乐列表样式
.music-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.music-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }

  .music-info {
    display: flex;
    align-items: center;
    gap: 15px;
  }

  .music-cover {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    object-fit: cover;
  }

  .music-detail {
    min-width: 0;

    .music-title {
      font-size: 16px;
      color: #333;
      margin-bottom: 5px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .music-artist {
      font-size: 13px;
      color: #666;
      margin-bottom: 3px;
    }

    .music-duration {
      font-size: 12px;
      color: #999;
    }
  }

  .music-actions {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .action-btn {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    border: 1px solid #eee;
    background: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      background: #f5f5f5;
    }

    .icon {
      width: 20px;
      height: 20px;
    }
  }

  .play-btn {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: #ff4d4f;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background 0.3s;

    &:hover {
      background: #ff3a3d;
    }

    .icon {
      width: 20px;
      height: 20px;
    }
  }
}

// 收藏按钮样式（纯CSS）
.action-btn.collect .icon {
  position: relative;
}

// 未收藏状态（五角星样式，调整大小和位置）
.action-btn.collect .uncollected {
  position: relative;
  width: 18px;  /* 缩小宽度 */
  height: 18px; /* 缩小高度 */
  color: #999;
  display: inline-block;
  left: -1px;   /* 向左微调位置 */
}

.action-btn.collect .uncollected::before {
  content: "★";
  position: absolute;
  font-size: 18px;  /* 调整字体大小以匹配容器 */
  line-height: 1;
  transition: color 0.3s;
  top: 0;
  left: 0;
}

.action-btn.collect:hover .uncollected::before {
  color: #ff4d4f;
}


// 已收藏状态（实心心型）
.action-btn.collect .collected::before,
.action-btn.collect .collected::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 16px;
  background: #ff4d4f;
  border-radius: 10px 10px 0 0;
}

.action-btn.collect .collected::before {
  left: 10px;
  transform: rotate(-45deg);
  transform-origin: 0 100%;
}

.action-btn.collect .collected::after {
  left: 0;
  transform: rotate(45deg);
  transform-origin: 100% 100%;
}

// 播放按钮样式（纯CSS）
.play-btn .play {
  position: relative;
}

.play-btn .play::before {
  content: '';
  position: absolute;
  top: 0;
  left: 2px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 10px 0 10px 16px;
  border-color: transparent transparent transparent #fff;
}

.play-btn .pause {
  position: relative;
}

.play-btn .pause::before,
.play-btn .pause::after {
  content: '';
  position: absolute;
  top: 3px;
  width: 5px;
  height: 14px;
  background: #fff;
}

.play-btn .pause::before {
  left: 3px;
}

.play-btn .pause::after {
  right: 3px;
}

// 收藏弹窗中的播放按钮样式
.collect-actions .action-btn.play .icon.play {
  position: relative;
  width: 20px;
  height: 20px;
}

.collect-actions .action-btn.play .icon.play::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 10px 0 10px 16px;
  border-color: transparent transparent transparent #666;
}

// 底部播放器控制按钮样式
.player-controls .control-btn {
  position: relative;
}

// 上一首按钮样式（已修正方向）
.player-controls .prev-btn .icon.prev {
  position: relative;
  width: 24px;
  height: 24px;
}

.player-controls .prev-btn .icon.prev::before {
  content: '';
  position: absolute;
  top: 5px;
  left: 0;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 7px 12px 7px 0;
  border-color: transparent #333 transparent transparent;
}

.player-controls .prev-btn .icon.prev::after {
  content: '';
  position: absolute;
  top: 5px;
  left: 10px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 7px 12px 7px 0;
  border-color: transparent #333 transparent transparent;
}

// 下一首按钮样式（已修正方向）
.player-controls .next-btn .icon.next {
  position: relative;
  width: 24px;
  height: 24px;
}

.player-controls .next-btn .icon.next::before {
  content: '';
  position: absolute;
  top: 5px;
  right: 10px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 7px 0 7px 12px;
  border-color: transparent transparent transparent #333;
}

.player-controls .next-btn .icon.next::after {
  content: '';
  position: absolute;
  top: 5px;
  right: 0;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 7px 0 7px 12px;
  border-color: transparent transparent transparent #333;
}

// 底部播放器播放/暂停按钮样式
.player-controls .play-pause-btn .icon.play {
  position: relative;
  width: 20px;
  height: 20px;
}

.player-controls .play-pause-btn .icon.play::before {
  content: '';
  position: absolute;
  top: 0;
  left: 2px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 10px 0 10px 16px;
  border-color: transparent transparent transparent #fff;
}

.player-controls .play-pause-btn .icon.pause {
  position: relative;
  width: 20px;
  height: 20px;
}

.player-controls .play-pause-btn .icon.pause::before,
.player-controls .play-pause-btn .icon.pause::after {
  content: '';
  position: absolute;
  top: 3px;
  width: 5px;
  height: 14px;
  background: #fff;
}

.player-controls .play-pause-btn .icon.pause::before {
  left: 3px;
}

.player-controls .play-pause-btn .icon.pause::after {
  right: 3px;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 20px;
  margin: 0;
}

// 我的收藏弹窗
.collect-popup {
  position: fixed;
  top: 80px;
  right: 20px;
  width: 350px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;

  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 15px;
    border-bottom: 1px solid #f0f0f0;

    h3 {
      font-size: 16px;
      color: #333;
      margin: 0;
    }

    .close-btn {
      background: transparent;
      border: none;
      cursor: pointer;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;

      .icon {
        width: 20px;
        height: 16px;
        object-fit: contain;
      }
    }
  }

  .popup-content {
    max-height: 400px;
    overflow-y: auto;

    .collect-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 15px;
      border-bottom: 1px solid #f9f9f9;

      &:hover {
        background: #f9f9f9;
      }
    }

    .collect-info {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .collect-cover {
      width: 40px;
      height: 40px;
      border-radius: 4px;
      object-fit: cover;
    }

    .collect-detail {
      min-width: 0;

      .collect-title {
        font-size: 14px;
        color: #333;
        margin: 0 0 3px 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .collect-artist {
        font-size: 12px;
        color: #999;
        margin: 0;
      }
    }

    .collect-actions {
      display: flex;
      gap: 5px;
    }
  }
}

// 底部播放器样式
.music-player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 900;

  .player-info {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 250px;

    .player-cover {
      width: 50px;
      height: 50px;
      border-radius: 4px;
      object-fit: cover;
    }

    .player-detail {
      min-width: 0;

      .player-title {
        font-size: 14px;
        color: #333;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .player-artist {
        font-size: 12px;
        color: #999;
      }
    }
  }

  .player-controls {
    display: flex;
    align-items: center;
    gap: 20px;

    .control-btn {
      background: transparent;
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;

      .icon {
        width: 24px;
        height: 24px;
      }

      &.play-pause-btn {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: #ff4d4f;

        .icon {
          width: 20px;
          height: 20px;
        }
      }
    }
  }

  .player-progress {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 10px;

    .current-time, .total-time {
      font-size: 12px;
      color: #999;
      width: 40px;
    }

    .progress-bar {
      flex: 1;
      height: 4px;
      background: #eee;
      border-radius: 2px;
      position: relative;
      cursor: pointer;

      .progress-played {
        position: absolute;
        left: 0;
        top: 0;
        height: 100%;
        background: #ff4d4f;
        border-radius: 2px;
      }

      .progress-handle {
        position: absolute;
        top: 50%;
        transform: translate(-50%, -50%);
        width: 12px;
        height: 12px;
        background: #ff4d4f;
        border-radius: 50%;
        box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
      }
    }
  }
}

// 通用图标样式
.icon {
  vertical-align: middle;
  display: inline-block;
}
</style>