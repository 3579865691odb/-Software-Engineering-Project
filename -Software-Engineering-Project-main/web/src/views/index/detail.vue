<template>
  <div class="detail">
    <Header/>

    <div class="detail-content">
      <div class="detail-content-top">
        <div class="thing-infos-view">
          <div class="thing-infos">
            <div class="thing-img-box">
              <img :src="detailData.cover" alt="商品图片"/>
            </div>
            <div class="thing-info-box">
              <div class="thing-state">
                <span class="state hidden-sm">商品状态</span>
                <span>上市销售</span>
              </div>
              <h1 class="thing-name">{{ detailData.title }}</h1>
              <div class="price-container">
                <span class="a-price-symbol">¥</span>
                <span class="a-price">{{detailData.price}}</span>
              </div>
              <div class="translators flex-view">
                <span>分类：</span>
                <span v-if="detailData.classification_title" class="name">{{ detailData.classification_title }}</span>
                <span v-else-if="detailData.classificationId && classificationLoading" class="name loading">加载中...</span>
                <span v-else class="name">暂无分类</span>
              </div>
              <div class="translators flex-view">
                <span>库存：</span>
                <span class="name">{{ detailData.repertory }}</span>
                <span class="stock-status" v-if="detailData.repertory > 10">有货</span>
                <span class="stock-status low" v-else-if="detailData.repertory > 0">库存紧张</span>
                <span class="stock-status none" v-else>无货</span>
              </div>
              <button class="buy-btn" @click="handleOrder(detailData)">
                <img :src="AddIcon" />
                <span>立即购买</span>
              </button>
            </div>
          </div>
          <div class="thing-counts hidden-sm">
            <div class="count-item flex-view pointer" @click="addToCart()">
              <div class="count-img">
                <img :src="WantIcon">
              </div>
              <div class="count-box flex-view">
                <div class="count-text-box">
                  <span class="count-title">加入购物车</span>
                </div>
                <div class="count-num-box">
                  <span class="num-text">{{ cartItemCount }}</span>
                </div>
              </div>
            </div>
            <div class="count-item flex-view pointer" @click="collect()">
              <div class="count-img">
                <img :src="RecommendIcon">
              </div>
              <div class="count-box flex-view">
                <div class="count-text-box">
                  <span class="count-title">收藏</span>
                </div>
                <div class="count-num-box">
                  <span class="num-text">{{ detailData.collectCount }}</span>
                </div>
              </div>
            </div>
            <div class="count-item flex-view" @click="share()">
              <div class="count-img">
                <img :src="ShareIcon">
              </div>
              <div class="count-box flex-view">
                <div class="count-text-box">
                  <span class="count-title">分享</span>
                </div>
                <div class="count-num-box">
                  <span class="num-text"></span>
                  <img :src="WeiboShareIcon" class="mg-l">
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="detail-content-bottom">
        <div class="thing-content-view flex-view">
          <div class="main-content">
            <div class="order-view main-tab">
              <div class="tab-container">
                <span class="tab"
                      :class="selectTabIndex===index? 'tab-select':''"
                      v-for="(item,index) in tabData"
                      :key="index"
                      @click="selectTab(index)">
                  {{ item }}
                  <span v-if="selectTabIndex===index" class="tab-underline"></span>
                </span>
              </div>
            </div>

            <!--简介-->
            <div class="thing-intro" :class="selectTabIndex <= 0? '':'hide'">
              <p class="text">{{ detailData.description }}</p>
            </div>

            <!--评论-->
            <div class="thing-comment" :class="selectTabIndex > 0? '':'hide'">
              <div class="title">发表新的评论</div>
              <div class="publish flex-view">
                <img :src="AvatarIcon" class="mine-img">
                <input placeholder="说点什么..." class="content-input" ref="commentRef">
                <button class="send-btn" @click="sendComment()">发送</button>
              </div>
              <div class="tab-view flex-view">
                <div class="count-text">共有{{ commentData.length }}条评论</div>
                <div class="tab-box flex-view" v-if="commentData.length > 0">
                  <span :class="sortIndex === 0? 'tab-select': ''" @click="sortCommentList('recent')">最新</span>
                  <div class="line"></div>
                  <span :class="sortIndex === 1? 'tab-select': ''" @click="sortCommentList('hot')">热门</span>
                </div>
              </div>
              <div class="comments-list">
                <div class="comment-item" v-for="item in commentData" :key="item.id">
                  <div class="flex-item flex-view">
                    <img :src="AvatarIcon" class="avator">
                    <div class="person">
                      <div class="name">{{ item.username }}</div>
                      <div class="time">{{ item.commentTime }}</div>
                    </div>
                    <div class="float-right">
                      <span @click="like(item.id)">推荐</span>
                      <span class="num">{{ item.likeCount }}</span>
                    </div>
                  </div>
                  <p class="comment-content">{{ item.content }}</p>
                </div>
                <div class="infinite-loading-container" v-if="commentData.length === 0">
                  <div class="infinite-status-prompt">
                    <div class="no-results">
                      <p>暂无评论，快来发表第一条评论吧！</p>
                    </div>
                  </div>
                </div>
                <div class="infinite-loading-container" v-else>
                  <div class="infinite-status-prompt">
                    <div class="no-results">
                      <p>没有更多了</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

          </div>
          <div class="recommend">
            <div class="title">热门推荐</div>
            <div class="things">
              <div class="thing-item" v-for="item in recommendData" :key="item.id" @click="handleDetail(item)">
                <div class="img-view">
                  <img :src="item.cover" :alt="item.title">
                </div>
                <div class="info-view">
                  <h3 class="thing-name">{{ item.title.substring(0, 12)}}</h3>
                  <span class="price">
                    <span class="a-price-symbol">¥</span>
                    <span class="a-price">{{item.price}}</span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {message} from "ant-design-vue";
import Header from '/@/views/index/components/header.vue'
import AddIcon from '/@/assets/images/add.svg';
import WantIcon from '/@/assets/images/want-read-hover.svg';
import RecommendIcon from '/@/assets/images/recommend-hover.svg';
import ShareIcon from '/@/assets/images/share-icon.svg';
import WeiboShareIcon from '/@/assets/images/wb-share.svg';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import {
  detailApi,
  listApi as listThingList,
} from '/@/api/thing'
import {listThingCommentsApi, createApi as createCommentApi, likeApi} from '/@/api/comment'
import {wishApi} from '/@/api/thingWish'
import {collectApi} from '/@/api/thingCollect'
import {BASE_URL} from "/@/store/constants";
import {useRoute, useRouter} from "vue-router/dist/vue-router";
import {useUserStore} from "/@/store";
import {getFormatTime} from "/@/utils";

const router = useRouter()
const route = useRoute()
const userStore = useUserStore();


let thingId = ref('')
let detailData = ref({})
let tabData = ref(['简介', '评论'])
let selectTabIndex = ref(0)
let cartItemCount = ref(0)
let classificationLoading = ref(false)

let commentData = ref([])
let recommendData = ref([])
let sortIndex = ref(0)
let order = ref('recent') // 默认排序最新

let commentRef = ref()

onMounted(()=>{
  thingId.value = route.query.id.trim()
  getThingDetail()
  getRecommendThing()
  getCommentList()
  updateCartItemCount()
})

const selectTab =(index)=> {
  selectTabIndex.value = index
}

const getThingDetail =()=> {
  detailApi({id: thingId.value}).then(res => {
    detailData.value = res.data
    detailData.value.cover = BASE_URL + '/api/staticfiles/image/' + detailData.value.cover

    // 如果分类标题不存在，获取分类信息
    if (!detailData.value.classification_title && detailData.value.classificationId) {
      getClassificationInfo(detailData.value.classificationId)
    }

    console.log('商品详情数据:', detailData.value)
  }).catch(err => {
    message.error('获取详情失败')
  })
}

// 获取分类信息
const getClassificationInfo = (classificationId) => {
  classificationLoading.value = true
  import('/@/api/classification').then(({ listApi }) => {
    listApi({}).then(res => {
      if (res.data && res.data.length > 0) {
        const classification = res.data.find(item => item.id === classificationId)
        if (classification) {
          detailData.value.classification_title = classification.title
        }
      }
      classificationLoading.value = false
    }).catch(err => {
      console.error('获取分类信息失败:', err)
      classificationLoading.value = false
    })
  }).catch(err => {
    console.error('导入分类API失败:', err)
    classificationLoading.value = false
  })
}

const addToCart =()=> {
  let userId = userStore.user_id
  if (userId) {
    // 从本地存储获取购物车数据
    let cartItems = []
    const storedCart = localStorage.getItem('cart')
    if (storedCart) {
      cartItems = JSON.parse(storedCart)
    }

    // 检查商品是否已在购物车中
    const existingItem = cartItems.find(item => item.id === detailData.value.id)

    if (existingItem) {
      // 如果商品已存在，增加数量
      existingItem.quantity += 1
      message.success('商品数量已增加')
    } else {
      // 如果商品不存在，添加到购物车
      const newItem = {
        id: detailData.value.id,
        title: detailData.value.title,
        price: detailData.value.price,
        quantity: 1,
        spec: '默认规格',
        cover: detailData.value.cover,
        checked: true
      }
      cartItems.push(newItem)
      message.success('商品已添加到购物车')
    }

    // 保存到本地存储
    localStorage.setItem('cart', JSON.stringify(cartItems))
    updateCartItemCount()
  } else {
    message.warn('请先登录')
    router.push({name: 'login'})
  }
}
const collect =()=> {
  let userId = userStore.user_id
  if (userId) {
    // 创建FormData对象，以匹配后端接收方式
    const formData = new FormData()
    formData.append('userId', userId)
    formData.append('thingId', thingId.value)

    collectApi(formData).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.error('收藏失败:', err)
      message.error(err.msg || '收藏失败，请稍后再试')
    })
  } else {
    message.warn('请先登录')
    router.push({name: 'login'})
  }
}
const share =()=> {
  let content = '分享一个非常好玩的网站 ' + window.location.href
  let shareHref = 'http://service.weibo.com/share/share.php?title=' + content
  window.open(shareHref)
}
const handleOrder =(detailData)=> {
  console.log('立即购买商品信息:', detailData)
  const userId = userStore.user_id
  
  // 把商品信息存储到localStorage，便于确认订单页面获取
  const checkoutItems = [{
    id: detailData.id,
    title: detailData.title,
    cover: detailData.cover,
    price: detailData.price,
    quantity: 1
  }]
  localStorage.setItem('checkoutItems', JSON.stringify(checkoutItems))
  
  router.push({name: 'confirm',
    query:
        {
          id: detailData.id,
          title: detailData.title,
          cover: detailData.cover,
          price: detailData.price
        }})
}
const getRecommendThing =()=> {
  listThingList({sort: 'recommend'}).then(res => {
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
    })
    console.log(res)
    recommendData.value = res.data.slice(0, 6)
  }).catch(err => {
    console.log(err)
  })
}
const handleDetail =(item)=> {
  // 跳转新页面
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  window.open(text.href, '_blank')
}
const sendComment =()=> {
  console.log(commentRef.value)
  let text = commentRef.value.value.trim()
  console.log(text)
  if (text.length <= 0) {
    return
  }
  commentRef.value.value = ''
  let userId = userStore.user_id
  if (userId) {
    createCommentApi({content: text, thingId: thingId.value, userId: userId}).then(res => {
      getCommentList()
    }).catch(err => {
      console.log(err)
    })
  } else {
    message.warn('请先登录！')
    router.push({name: 'login'})
  }
}
const like =(commentId)=> {
  likeApi({id: commentId}).then(res => {
    getCommentList()
  }).catch(err => {
    console.log(err)
  })
}
const getCommentList =()=> {
  listThingCommentsApi({thingId: thingId.value, order: order.value}).then(res => {
    res.data.forEach(item => {
      item.commentTime = getFormatTime(item.commentTime, true)
    })
    commentData.value = res.data
  }).catch(err => {
    console.log(err)
  })
}
const sortCommentList =(sortType)=> {
  if (sortType === 'recent') {
    sortIndex.value = 0
  } else {
    sortIndex.value = 1
  }
  order.value = sortType
  getCommentList()
}

// 更新购物车商品数量
const updateCartItemCount = () => {
  const storedCart = localStorage.getItem('cart')
  if (storedCart) {
    const cartItems = JSON.parse(storedCart)
    // 找到当前商品在购物车中的数量
    const currentItem = cartItems.find(item => item.id === detailData.value.id)
    cartItemCount.value = currentItem ? currentItem.quantity : 0
  } else {
    cartItemCount.value = 0
  }
}

</script>
<style scoped lang="less">

.hide {
  display: none;
}

.detail-content {
  display: flex;
  flex-direction: column;
  width: 1100px;
  margin: 40px auto 40px;
}

.flex-view {
  display: flex;
}

.hidden-lg {
  display: none !important;
}

.thing-infos-view {
  display: flex;
  margin: 40px 0;
  overflow: hidden;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  padding: 30px;

  .thing-infos {
    flex: 1;
    display: flex;
  }

  .mobile-share-box {
    height: 38px;
    background: transparent;
    padding: 0 16px;
    margin: 12px 0;
    font-size: 0;
    align-items: center;
    justify-content: space-between;

    .state {
      width: 64px;
      height: 24px;
      line-height: 24px;
      background: rgba(70, 132, 226, .1);
      border-radius: 4px;
      font-weight: 500;
      font-size: 12px;
      color: #4684e2;
      text-align: center;
    }

    .share-img {
      background: #fff;
      width: 38px;
      height: 38px;
      border-radius: 50%;
      text-align: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      img {
        position: relative;
        top: 4px;
        width: 24px;
      }
    }
  }

  .thing-img-box {
    flex: 0 0 300px;
    margin: 0 40px 0 0;
    position: relative;
    overflow: hidden;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    background: #f8f9fa;
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      max-width: 100%;
      max-height: 100%;
      display: block;
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.05);
      }
    }
  }

  .thing-info-box {
    text-align: left;
    padding: 0;
    margin: 0;
  }

  .thing-state {
    height: 26px;
    line-height: 26px;

    .state {
      font-weight: 500;
      color: #4684e2;
      background: rgba(70, 132, 226, .1);
      border-radius: 4px;
      padding: 5px 12px;
      margin-right: 16px;
      transition: all 0.3s ease;

      &:hover {
        background: rgba(70, 132, 226, .2);
      }
    }

    span {
      font-size: 14px;
      color: #152844;
    }
  }

  .thing-name {
    line-height: 32px;
    margin: 16px 0;
    color: #0F1111;
    font-size: 24px !important;
    font-weight: 600 !important;
    letter-spacing: -0.5px;
  }

  .translators, .authors {
    line-height: 18px;
    font-size: 14px;
    margin: 16px 0;
    align-items: flex-start;
    justify-content: flex-start;
    padding: 10px 0;
    border-top: 1px solid #f0f0f0;
    border-bottom: 1px solid #f0f0f0;

    .name {
      color: #315c9e;
      white-space: normal;
      font-weight: 500;

      &.loading {
        color: #999;
        font-style: italic;
        position: relative;

        &::after {
          content: '...';
          position: absolute;
          animation: ellipsis 1.5s infinite;
        }
      }
    }
  }

  @keyframes ellipsis {
    0% {
      content: '.';
    }
    33% {
      content: '..';
    }
    66% {
      content: '...';
    }
  }

  .tags {
    position: absolute;
    bottom: 20px;
    margin-top: 16px;

    .category-box {
      color: #152844;
      font-size: 14px;

      .title {
        color: #787878;
      }
    }
  }

  .thing-counts {
    flex: 0 0 235px;
    margin-left: 20px;
    border-left: 1px solid #f0f0f0;
    padding-left: 20px;
  }

  .pointer {
    cursor: pointer;
  }

  .count-item {
    height: 64px;
    align-items: center;
    cursor: pointer;
    transition: all 0.2s ease;
    border-radius: 8px;
    padding: 0 10px;

    &:hover {
      background-color: #f5f7fa;
    }
  }

  .count-img {
    flex: 0 0 32px;
    margin-right: 24px;
    font-size: 0;

    img {
      width: 100%;
      display: block;
    }
  }

  .count-box {
    position: relative;
    border-bottom: 1px solid #f0f0f0;
    align-items: center;
    justify-content: space-between;
    flex: 1;
    height: 100%;
  }

  .count-text-box {
    font-size: 0;

    .count-title {
      color: #152844;
      font-weight: 600;
      font-size: 16px;
      line-height: 18px;
      display: block;
      height: 18px;
    }
  }

  .count-num-box {
    font-weight: 600;
    font-size: 20px;
    line-height: 24px;
    color: #152844;
  }
}

.buy-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4684e2, #5a6acd);
  border-radius: 6px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  height: 44px;
  width: 160px;
  outline: none;
  border: none;
  margin-top: 24px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(70, 132, 226, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(70, 132, 226, 0.4);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(70, 132, 226, 0.3);
  }

  img {
    width: 16px;
    margin-right: 8px;
    vertical-align: middle;
  }

  span {
    vertical-align: middle;
  }
}

.buy-way {
  overflow: hidden;

  .title {
    font-weight: 600;
    font-size: 18px;
    height: 26px;
    line-height: 26px;
    color: #152844;
    margin-bottom: 12px;
  }
}

.thing-content-view {
  margin-top: 20px;
  padding-bottom: 50px;
  display: flex;
  gap: 30px;
}

.main-content {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  padding: 30px;

  .text {
    color: #484848;
    font-size: 16px;
    line-height: 1.8;
    padding: 20px;
    margin: 0;
    white-space: pre-wrap;
    background: #f9f9f9;
    border-radius: 8px;
  }
}

.main-tab {
  border-bottom: 1px solid #eaeaea;
  margin-bottom: 20px;
}

.order-view {
  position: relative;
  color: #6c6c6c;
  font-size: 14px;
  line-height: 40px;

  .title {
    margin-right: 8px;
  }

  .tab-container {
    display: flex;
    position: relative;
  }

  .tab {
    margin-right: 30px;
    cursor: pointer;
    color: #5f77a6;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    padding: 0 10px;
    position: relative;

    &:hover {
      color: #4684e2;
    }
  }

  .tab-select {
    color: #152844;
    font-weight: 600;
  }

  .tab-underline {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 24px;
    height: 4px;
    background: #4684e2;
    border-radius: 2px;
  }
}

.recommend {
  flex: 0 0 280px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  padding: 20px;
  height: fit-content;

  .title {
    font-weight: 600;
    font-size: 18px;
    line-height: 26px;
    color: #152844;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eaeaea;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -1px;
      left: 0;
      width: 40px;
      height: 3px;
      background: #4684e2;
      border-radius: 2px;
    }
  }

  .things {
    max-height: 500px;
    overflow-y: auto;
    padding-right: 5px;

    /* 自定义滚动条样式 */
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb:hover {
      background: #a8a8a8;
    }

    .thing-item {
      position: relative;
      width: 100%;
      height: fit-content;
      overflow: hidden;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-5px);

        img {
          transform: scale(1.05);
        }
      }

      &:last-child {
        border-bottom: none;
        margin-bottom: 0;
      }

      .img-view {
        height: 160px;
        width: 100%;
        border-radius: 8px;
        overflow: hidden;
        background: #f8f9fa;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          max-height: 160px;
          max-width: 100%;
          overflow: hidden;
          margin: 0 auto;
          object-fit: contain;
          transition: transform 0.3s ease;
        }
      }

      .info-view {
        overflow: hidden;
        padding: 12px 0;

        .thing-name {
          line-height: 1.4;
          margin-top: 8px;
          color: #152844;
          font-size: 15px;
          font-weight: 500;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .price {
          color: #ff7b31;
          font-size: 18px;
          line-height: 20px;
          margin-top: 8px;
          font-weight: 600;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .translators {
          color: #6f6f6f;
          font-size: 12px;
          line-height: 14px;
          margin-top: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }
}

.thing-comment {
  .title {
    font-weight: 600;
    font-size: 18px;
    line-height: 22px;
    color: #152844;
    margin: 30px 0 20px;
    position: relative;
    padding-left: 12px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 2px;
      width: 4px;
      height: 18px;
      background: #4684e2;
      border-radius: 2px;
    }
  }

  .publish {
    align-items: center;
    background: #f8f9fb;
    padding: 15px;
    border-radius: 8px;

    .mine-img {
      flex: 0 0 40px;
      margin-right: 12px;
      border-radius: 50%;
      width: 40px;
      height: 40px;
    }

    .content-input {
      flex: 1;
      background: #fff;
      border-radius: 4px;
      height: 40px;
      line-height: 40px;
      color: #484848;
      padding: 5px 15px;
      white-space: nowrap;
      outline: none;
      border: 1px solid #eaeaea;
      transition: all 0.3s ease;

      &:focus {
        border-color: #4684e2;
        box-shadow: 0 0 0 2px rgba(70, 132, 226, 0.2);
      }
    }

    .send-btn {
      margin-left: 10px;
      background: #4684e2;
      border-radius: 4px;
      flex: 0 0 80px;
      color: #fff;
      font-size: 14px;
      text-align: center;
      height: 40px;
      line-height: 40px;
      outline: none;
      border: 0px;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        background: #3a75d0;
      }
    }
  }

  .tab-view {
    justify-content: space-between;
    font-size: 14px;
    align-items: center;
    margin: 24px 0;
    padding: 10px 0;
    border-bottom: 1px solid #eaeaea;

    .count-text {
      color: #484848;
      float: left;
      font-weight: 500;
    }

    .tab-box {
      color: #5f77a6;
      align-items: center;

      .tab-select {
        color: #4684e2;
        font-weight: 500;
      }

      span {
        cursor: pointer;
        padding: 4px 12px;
        border-radius: 4px;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(70, 132, 226, 0.1);
        }
      }
    }

    .line {
      width: 1px;
      height: 12px;
      margin: 0 12px;
      background: #eaeaea;
    }
  }
}

.comments-list {
  .comment-item {
    .flex-item {
      align-items: center;
      padding-top: 16px;

      .avator {
        flex: 0 0 40px;
        width: 40px;
        height: 40px;
        margin-right: 12px;
        border-radius: 50%;
        cursor: pointer;
      }

      .person {
        flex: 1;
      }

      .name {
        color: #152844;
        font-weight: 600;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
        cursor: pointer;
      }

      .time {
        color: #5f77a6;
        font-size: 12px;
        line-height: 16px;
        height: 16px;
        margin-top: 2px;
      }

      .float-right {
        color: #4684e2;
        font-size: 14px;
        float: right;

        span {
          margin-left: 19px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            color: #3a75d0;
            text-decoration: underline;
          }
        }

        .num {
          color: #152844;
          margin-left: 6px;
          cursor: auto;
          text-decoration: none;
        }
      }
    }
  }
}

.comment-content {
  margin-top: 8px;
  color: #484848;
  font-size: 14px;
  line-height: 1.6;
  padding: 10px 15px 16px;
  border-bottom: 1px solid #eaeaea;
  margin-left: 52px;
  overflow: hidden;
  word-break: break-word;
  background: #f9f9fb;
  border-radius: 0 8px 8px 8px;
}

.infinite-loading-container {
  clear: both;
  text-align: center;
  padding: 20px 0;
}

.a-price-symbol {
  top: -0.5em;
  font-size: 14px;
  color: #ff7b31;
}

.a-price {
  color: #ff7b31;
  font-size: 28px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .detail-content {
    width: 100%;
    padding: 0 15px;
  }

  .thing-infos-view {
    flex-direction: column;
    padding: 20px;

    .thing-infos {
      flex-direction: column;
    }

    .thing-img-box {
      margin: 0 0 20px 0;
      width: 100%;
      flex: none;
    }

    .thing-counts {
      margin-left: 0;
      margin-top: 20px;
      border-left: none;
      border-top: 1px solid #f0f0f0;
      padding-left: 0;
      padding-top: 20px;
    }
  }

  .thing-content-view {
    flex-direction: column;

    .recommend {
      margin-top: 20px;
      width: 100%;
    }
  }
}
</style>
