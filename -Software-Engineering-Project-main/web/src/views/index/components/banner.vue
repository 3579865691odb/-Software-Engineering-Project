<template>
  <div class="banner-container">
    <!-- 左侧分类导航 -->
    <div class="category-nav">
      <h3 class="category-title">主题市场</h3>
      <ul class="category-list">
        <li v-for="item in categoryData" :key="item.id" class="category-item">
          <a href="javascript:;" @click="selectCategory(item)">
            <span>{{ item.title }}</span>
          </a>
        </li>
      </ul>
    </div>

    <!-- 轮播图 -->
    <div class="carousel-container">
      <a-carousel autoplay class="custom-carousel">
        <div v-for="(banner, index) in bannerList" :key="index" class="carousel-item" @click="handleBannerClick(banner)">
          <img :src="banner.image" :alt="banner.title">
        </div>
      </a-carousel>
    </div>

    <!-- 右侧用户信息和快捷入口 -->
    <div class="user-panel">
      <div class="user-info">
        <img v-if="userAvatar"  :src="userAvatar" class="user-avatar" alt="用户头像">
        <img v-else :src="AvatarIcon" class="self-img">
        <div class="user-greeting">
          <p v-if="userStore.user_token">Hi, {{ nickName }}</p>
          <p v-else>Hi, 游客</p>
        </div>
      </div>
      <div class="quick-links">
        <div class="link-row">
          <a href="javascript:;" @click="goToUserCenter('购物车')" class="quick-link">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-cart"></use>
            </svg>
            <span>购物车</span>
          </a>
          <a href="javascript:;" @click="goToUserCenter('地址')" class="quick-link">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-location"></use>
            </svg>
            <span>我的地址</span>
          </a>
        </div>
        <div class="link-row">
          <a href="javascript:;" @click="goToUserCenter('订单')" class="quick-link">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-order"></use>
            </svg>
            <span>我的订单</span>
          </a>
          <a href="javascript:;" @click="goToUserCenter('个人设置')" class="quick-link">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-user"></use>
            </svg>
            <span>个人设置</span>
          </a>
        </div>
      </div>
      <div class="announcement">
        <svg class="icon-notice" aria-hidden="true">
          <use xlink:href="#icon-notice"></use>
        </svg>
        <span>公告：</span>
        <marquee>{{ announcement }}</marquee>
      </div>
    </div>
  </div>

  <!-- 热门标签 -->
  <div class="hot-tags">
    <div class="section-title">
      <h2>热门标签</h2>
    </div>
    <div class="tag-container">
      <span class="tag"
            :class="{'tag-select': selectedTagId === item.id}"
            v-for="item in tagData"
            :key="item.id"
            @click="clickTag(item.id)">
        {{ item.title }}
      </span>
    </div>
  </div>

  <!-- 热卖商品区域 -->
  <div class="hot-products">
    <div class="section-title">
      <h2>热卖商品</h2>
      <a href="javascript:;" class="refresh-btn" @click="refreshProducts('hot')">
        <svg class="icon-refresh" aria-hidden="true" viewBox="0 0 1024 1024">
          <path d="M512 96c-229.76 0-416 186.24-416 416s186.24 416 416 416 416-186.24 416-416c0-31.36-3.84-61.44-10.24-90.88l-84.48 84.48c1.28 18.56 2.56 37.76 2.56 56.96 0 179.84-145.92 325.76-325.76 325.76S184.32 742.4 184.32 562.56 330.24 236.8 510.08 236.8c88.32 0 168.96 35.2 227.84 92.16L642.56 424.32h210.56V213.76L778.24 288c-74.88-75.52-178.56-121.6-294.4-121.6l28.16-70.4z" fill="currentColor"></path>
        </svg>
        <span>刷新</span>
      </a>
    </div>
    <div class="product-list">
      <div v-if="hotProducts.length === 0" class="loading-container">
        <svg class="loading-icon" viewBox="0 0 1024 1024">
          <path d="M512 64c-247.4 0-448 200.6-448 448s200.6 448 448 448 448-200.6 448-448-200.6-448-448-448z m0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="#f0f0f0"></path>
          <path d="M512 140c-205.4 0-372 166.6-372 372h76c0-163.2 132.8-296 296-296V140z" fill="#ff4d4f"></path>
        </svg>
        <p>正在加载数据...</p>
      </div>
      <div v-else v-for="(product, index) in hotProducts" :key="index" class="product-card" @click="handleDetail(product)">
        <div class="product-tag" v-if="index < 3">热销{{ index + 1 }}号</div>
        <img :src="product.cover" :alt="product.title" class="product-image">
        <div class="product-info">
          <h3 class="product-title">{{ product.title }}</h3>
          <div class="product-price">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ product.price }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 猜你喜欢区域 -->
  <div class="recommended-products">
    <div class="section-title">
      <h2>猜你喜欢</h2>
      <a href="javascript:;" class="refresh-btn" @click="refreshProducts('recommended')">
        <svg class="icon-refresh" aria-hidden="true" viewBox="0 0 1024 1024">
          <path d="M512 96c-229.76 0-416 186.24-416 416s186.24 416 416 416 416-186.24 416-416c0-31.36-3.84-61.44-10.24-90.88l-84.48 84.48c1.28 18.56 2.56 37.76 2.56 56.96 0 179.84-145.92 325.76-325.76 325.76S184.32 742.4 184.32 562.56 330.24 236.8 510.08 236.8c88.32 0 168.96 35.2 227.84 92.16L642.56 424.32h210.56V213.76L778.24 288c-74.88-75.52-178.56-121.6-294.4-121.6l28.16-70.4z" fill="currentColor"></path>
        </svg>
        <span>刷新</span>
      </a>
    </div>
    <div class="product-list">
      <div v-if="recommendedProducts.length === 0" class="loading-container">
        <svg class="loading-icon" viewBox="0 0 1024 1024">
          <path d="M512 64c-247.4 0-448 200.6-448 448s200.6 448 448 448 448-200.6 448-448-200.6-448-448-448z m0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="#f0f0f0"></path>
          <path d="M512 140c-205.4 0-372 166.6-372 372h76c0-163.2 132.8-296 296-296V140z" fill="#ff4d4f"></path>
        </svg>
        <p>正在加载数据...</p>
      </div>
      <div v-else v-for="(product, index) in recommendedProducts" :key="index" class="product-card" @click="handleDetail(product)">
        <img :src="product.cover" :alt="product.title" class="product-image">
        <div class="product-info">
          <h3 class="product-title">{{ product.title }}</h3>
          <div class="product-price">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ product.price }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from "/@/store"
import { listApi as listThingList } from '/@/api/thing'
import { listApi as listClassificationList } from '/@/api/classification'
import { listApi as listTagList } from '/@/api/tag'
import { BASE_URL } from "/@/store/constants"
import { getRecommendedProducts } from '/@/utils/recommendation'
import {detailApi} from "/@/api/user";
import {listApi} from '/@/api/notice'
import {listApi as listBannerApi} from '/@/api/banner'
import AvatarIcon from "/@/assets/images/avatar.jpg";

const router = useRouter()
const userStore = useUserStore()

// 分类数据和标签数据
const categoryData = ref([])
const tagData = ref([])
const selectedTagId = ref(-1)

let userAvatar = ref('')
let nickName = ref('')
// 公告内容
const announcement = ref('加载中...')

// 轮播图数据
const bannerList = ref([])

// 热卖商品和推荐商品
const hotProducts = ref([])
const recommendedProducts = ref([])
const allProducts = ref([])

onMounted(() => {
  getCategories()
  getTags()
  getProducts()
  getUserInfo()
  getNotices()
  getBanners()
})

// 获取轮播图数据
const getBanners = () => {
  listBannerApi().then(res => {
    if (res.data && res.data.length > 0) {
      bannerList.value = res.data.map(item => {
        return {
          title: item.thingId ? `商品ID: ${item.thingId}` : '轮播图',
          image: BASE_URL + '/api/staticfiles/banner/' + item.image,
          thingId: item.thingId
        }
      })
    } else {
      // 如果没有数据，使用默认图片
      bannerList.value = [
        {
          title: '品质好货',
          image: 'https://img.alicdn.com/imgextra/i4/O1CN01Fj5Jvs1LwCUGQgGZx_!!6000000001367-0-tps-1600-600.jpg'
        },
        {
          title: '限时特惠',
          image: 'https://img.alicdn.com/imgextra/i1/O1CN01Tnc3Gt1mx4KDgEVP3_!!6000000005024-0-tps-1600-600.jpg'
        },
        {
          title: '新品上市',
          image: 'https://img.alicdn.com/imgextra/i2/O1CN01gwUMcu1aAhZqDL3Zg_!!6000000003290-0-tps-1600-600.jpg'
        }
      ]
    }
  }).catch(err => {
    console.log('获取轮播图失败:', err)
    // 加载失败时使用默认图片
    bannerList.value = [
      {
        title: '品质好货',
        image: 'https://img.alicdn.com/imgextra/i4/O1CN01Fj5Jvs1LwCUGQgGZx_!!6000000001367-0-tps-1600-600.jpg'
      },
      {
        title: '限时特惠',
        image: 'https://img.alicdn.com/imgextra/i1/O1CN01Tnc3Gt1mx4KDgEVP3_!!6000000005024-0-tps-1600-600.jpg'
      },
      {
        title: '新品上市',
        image: 'https://img.alicdn.com/imgextra/i2/O1CN01gwUMcu1aAhZqDL3Zg_!!6000000003290-0-tps-1600-600.jpg'
      }
    ]
  })
}

// 获取公告信息
const getNotices = () => {
  listApi().then(res => {
    if (res.data && res.data.length > 0) {
      // 按创建时间排序，获取最新的一条公告
      const sortedNotices = [...res.data].sort((a, b) => {
        return new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
      })
      const latestNotice = sortedNotices[0]
      announcement.value = latestNotice.title ? `${latestNotice.title}: ${latestNotice.content}` : latestNotice.content
    } else {
      announcement.value = '暂无公告'
    }
  }).catch(err => {
    console.log('获取公告失败:', err)
    announcement.value = '暂无公告'
  })
}

const getUserInfo = () => {
  let userId = userStore.user_id
  if (!userId) return

  detailApi({userId: userId}).then(res => {
    if (res.data) {
      userAvatar.value = BASE_URL + '/api/staticfiles/avatar/' + res.data.avatar
      nickName.value = res.data.nickname
    }
  }).catch(err => {
    console.log(err)
  })
}

// 获取分类数据
const getCategories = () => {
  listClassificationList().then(res => {
    categoryData.value = res.data
  }).catch(err => {
    console.log(err)
  })
}

// 获取标签数据
const getTags = () => {
  listTagList().then(res => {
    tagData.value = res.data
  }).catch(err => {
    console.log(err)
  })
}

// 获取商品数据
const getProducts = () => {
  listThingList({}).then(res => {
    allProducts.value = res.data.map(item => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
      return item
    })

    // 获取热门商品（按销量或评分排序）
    hotProducts.value = [...allProducts.value].sort(() => Math.random() - 0.5).slice(0, 4)

    // 获取推荐商品（使用协同过滤算法）
    if (userStore.user_token) {
      // 如果用户已登录，使用协同过滤算法获取推荐
      recommendedProducts.value = getRecommendedProducts(userStore.user_info?.id, allProducts.value)
    } else {
      // 如果用户未登录，随机推荐一些商品
      recommendedProducts.value = [...allProducts.value].sort(() => Math.random() - 0.5).slice(0, 4)
    }
  }).catch(err => {
    console.log(err)
  })
}

// 根据分类名称获取对应的图标
const getCategoryIcon = (title) => {
  const iconMap = {
    '图书音像': 'icon-book',
    '厨具': 'icon-kitchen',
    '收纳': 'icon-kitchen',
    '宠物': 'icon-kitchen',
    '医药': 'icon-medicine',
    '保健品': 'icon-medicine',
    '家纺': 'icon-home',
    '家饰': 'icon-home',
    '鲜花': 'icon-home',
    '汽车': 'icon-car',
    '配件': 'icon-car',
    '用品': 'icon-car',
    '家具': 'icon-furniture',
    '灯具': 'icon-furniture',
    '卫浴': 'icon-furniture',
    '电器': 'icon-appliance',
    '生活用品': 'icon-appliance',
    '水果': 'icon-fruit',
    '生鲜': 'icon-fruit',
    '零食': 'icon-food',
    '茶酒': 'icon-food',
    '食品': 'icon-food',
    '母婴': 'icon-baby',
    '玩具': 'icon-baby',
    '手机': 'icon-phone',
    '数码': 'icon-phone',
    '电脑': 'icon-computer',
    '办公': 'icon-computer',
    '珠宝': 'icon-jewelry',
    '护肤': 'icon-jewelry',
    '饰品': 'icon-jewelry',
    '女鞋': 'icon-shoes',
    '男鞋': 'icon-shoes',
    '箱包': 'icon-shoes',
    '女装': 'icon-clothing',
    '内衣': 'icon-clothing',
    '男装': 'icon-menswear',
    '运动': 'icon-menswear',
    '户外': 'icon-menswear',
    '教材': 'icon-book',
    '教辅': 'icon-book',
    '计算机': 'icon-computer',
    '研究生考试': 'icon-book',
    '外语学习': 'icon-book',
    '人文社科': 'icon-book',
    '历史政治': 'icon-book',
    '文学小说': 'icon-book'
  }

  // 尝试精确匹配
  if (iconMap[title]) {
    return iconMap[title]
  }

  // 尝试模糊匹配
  for (const key in iconMap) {
    if (title.includes(key)) {
      return iconMap[key]
    }
  }

  // 默认图标
  return 'icon-book'
}

// 选择分类
const selectCategory = (category) => {
  if (category.id === '-1') {
    // 全部分类
    router.push({
      name: 'portal'
    })
  } else {
    router.push({
      name: 'search',
      query: { c: category.id }
    })
  }
}

// 点击标签
const clickTag = (tagId) => {
  selectedTagId.value = tagId
  router.push({
    name: 'search',
    query: { tag: tagId }
  })
}

// 刷新商品
const refreshProducts = (type) => {
  // 显示加载动画
  if (type === 'hot') {
    hotProducts.value = []
  } else {
    recommendedProducts.value = []
  }

  // 获取商品数据
  listThingList({}).then(res => {
    const products = res.data.map(item => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
      return item
    })

    // 根据类型刷新不同的商品列表
    if (type === 'hot') {
      // 获取热门商品（按销量或评分排序）
      hotProducts.value = [...products].sort(() => Math.random() - 0.5).slice(0, 4)
    } else {
      // 获取推荐商品
      if (userStore.user_token) {
        // 如果用户已登录，使用协同过滤算法获取推荐
        recommendedProducts.value = getRecommendedProducts(userStore.user_info?.id, products)
      } else {
        // 如果用户未登录，随机推荐一些商品
        recommendedProducts.value = [...products].sort(() => Math.random() - 0.5).slice(0, 4)
      }
    }
  }).catch(err => {
    console.log(err)
  })
}

// 用户中心导航
const goToUserCenter = (type) => {
  if (!userStore.user_token) {
    router.push('/index/login')
    return
  }

  switch (type) {
    case '购物车':
      router.push('/index/usercenter/wishThingView')
      break
    case '地址':
      router.push('/index/usercenter/addressView')
      break
    case '订单':
      router.push('/index/usercenter/orderView')
      break
    case '个人设置':
      router.push('/index/usercenter/userInfoEditView')
      break
    default:
      router.push('/index/usercenter')
  }
}

// 商品详情
const handleDetail = (item) => {
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  window.open(text.href, '_blank')
}

// 处理轮播图点击事件
const handleBannerClick = (banner) => {
  if (banner.thingId) {
    router.push({
      name: 'detail',
      query: { id: banner.thingId }
    })
  }
}
</script>

<style scoped lang="less">
.banner-container {
  display: flex;
  margin-top: 50px;
  margin-bottom: 30px;
  gap: 20px;
  height: 360px;
  padding-top: 30px;
}

// 左侧分类导航
.category-nav {
  width: 240px;
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);

  .category-title {
    height: 50px;
    line-height: 50px;
    background-color: #ff4d4f;
    color: #fff;
    font-size: 18px;
    font-weight: 600;
    text-align: center;
    margin: 0;
  }

  .category-list {
    margin: 0;
    padding: 0;
    list-style: none;
    max-height: 310px;
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(0, 0, 0, 0.2);
      border-radius: 2px;
    }

    .category-item {
      height: 36px;
      line-height: 36px;
      padding: 0 15px;
      transition: all 0.3s;

      &:hover {
        background-color: #fff2f2;
      }

      a {
        display: flex;
        align-items: center;
        color: #333;
        text-decoration: none;
        font-size: 14px;

        i {
          margin-right: 8px;
          font-size: 16px;
          color: #666;
        }
      }
    }
  }
}

// 轮播图
.carousel-container {
  flex: 1;
  height: 330px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);

  .custom-carousel {
    height: 100%;

    .carousel-item {
      height: 360px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

// 用户面板
.user-panel {
  width: 240px;
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;

  .user-info {
    padding: 20px;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #f0f0f0;
    background-color: #fff;


    .self-img {
      width: 30px;
      height: 30px;
      border-radius: 50%;
      margin-right: 8px;
    }

    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
      margin-right: 10px;
      border: 2px solid #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .user-greeting {
      p {
        margin: 0;
        font-size: 16px;
        font-weight: 500;
        color: #333;
      }
    }
  }

  .quick-links {
    padding: 15px;
    flex: 1;

    .link-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;

      .quick-link {
        display: flex;
        flex-direction: column;
        align-items: center;
        width: 45%;
        height: 70px;
        background-color: #f9f9f9;
        border-radius: 8px;
        padding: 12px 0;
        color: #333;
        text-decoration: none;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-3px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          background-color: #fff2f2;
          color: #ff4d4f;
        }

        .icon {
          width: 24px;
          height: 24px;
          margin-bottom: 6px;
        }

        span {
          font-size: 12px;
        }
      }
    }
  }

  .announcement {
    height: 40px;
    line-height: 40px;
    background-color: #fff2f2;
    padding: 0 15px;
    display: flex;
    align-items: center;
    overflow: hidden;

    .icon-notice {
      width: 16px;
      height: 16px;
      color: #ff4d4f;
      margin-right: 5px;
      flex-shrink: 0;
    }

    span {
      color: #ff4d4f;
      font-weight: 500;
      flex-shrink: 0;
      margin-right: 5px;
    }

    marquee {
      flex: 1;
      color: #666;
      white-space: nowrap;
      overflow: hidden;
    }
  }
}

// 热门标签
.hot-tags {
  margin-bottom: 30px;
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);

  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
      color: #333;
      position: relative;
      padding-left: 15px;

      &:before {
        content: '';
        position: absolute;
        left: 0;
        top: 5px;
        width: 5px;
        height: 20px;
        background-color: #ff4d4f;
        border-radius: 3px;
      }
    }
  }

  .tag-container {
    display: flex;
    flex-wrap: wrap;

    .tag {
      background: #f5f8fc;
      border: 1px solid #e0e7f1;
      box-sizing: border-box;
      border-radius: 20px;
      height: 32px;
      line-height: 30px;
      padding: 0 16px;
      margin: 0 12px 12px 0;
      cursor: pointer;
      font-size: 14px;
      color: #333;
      transition: all 0.3s ease;

      &:hover {
        background: #4684e3;
        color: #fff;
        border: 1px solid #4684e3;
        transform: translateY(-2px);
        box-shadow: 0 2px 6px rgba(70, 132, 226, 0.2);
      }

      &.tag-select {
        background: #ff4d4f;
        color: #fff;
        border: 1px solid #ff4d4f;
        box-shadow: 0 2px 6px rgba(255, 77, 79, 0.2);
      }
    }
  }
}

// 热卖商品和推荐商品公共样式
.hot-products,
.recommended-products {
  margin-bottom: 30px;
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);

  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
      color: #333;
      position: relative;
      padding-left: 15px;

      &:before {
        content: '';
        position: absolute;
        left: 0;
        top: 5px;
        width: 5px;
        height: 20px;
        background-color: #ff4d4f;
        border-radius: 3px;
      }
    }

    .refresh-btn {
      display: flex;
      align-items: center;
      color: #666;
      font-size: 14px;
      text-decoration: none;
      transition: all 0.3s;

      .icon-refresh {
        width: 16px;
        height: 16px;
        margin-right: 4px;
        transition: all 0.3s;
      }

      span {
        line-height: 1;
      }

      &:hover {
        color: #ff4d4f;

        .icon-refresh {
          transform: rotate(180deg);
        }
      }
    }
  }

  .product-list {
    display: flex;
    gap: 20px;

    .product-card {
      width: calc(25% - 15px);
      background-color: #fff;
      border-radius: 10px;
      overflow: hidden;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      transition: all 0.3s;
      position: relative;
      cursor: pointer;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
      }

      .product-tag {
        position: absolute;
        top: 10px;
        left: 10px;
        background-color: #ff4d4f;
        color: #fff;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
        z-index: 1;
      }

      .product-image {
        width: 100%;
        height: 180px;
        object-fit: contain;
        background-color: #f9f9f9;
        transition: all 0.3s;

        &:hover {
          transform: scale(1.05);
        }
      }

      .product-info {
        padding: 12px;

        .product-title {
          margin: 0 0 8px;
          font-size: 14px;
          font-weight: 500;
          color: #333;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .product-price {
          color: #ff4d4f;

          .price-symbol {
            font-size: 12px;
            margin-right: 2px;
          }

          .price-value {
            font-size: 18px;
            font-weight: 600;
          }
        }
      }
    }
  }
}

// 图标样式
.iconfont {
  font-family: 'iconfont';
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-cart:before { content: '\e698'; }
.icon-location:before { content: '\e651'; }
.icon-order:before { content: '\e645'; }
.icon-user:before { content: '\e607'; }
.icon-notice:before { content: '\e614'; }

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 180px;
  width: 100%;

  .loading-icon {
    width: 64px;
    height: 64px;
    margin-bottom: 10px;
    animation: rotate 1.5s linear infinite;
  }

  p {
    margin: 0;
    font-size: 14px;
    color: #666;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
