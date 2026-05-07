<template>
  <div class="main-bar-view">
    <div class="header-container">
      <div class="logo" @click="$router.push({name:'portal'})">
        <img src="@/assets/images/shop-logo.png" class="logo-image">
        <span class="logo-text">乐器商城</span>
      </div>
      <div class="search-entry">
        <input placeholder="请输入商品名称" ref="keywordRef" @keyup.enter="search" />
        <button class="search-btn" @click="search">搜索</button>
      </div>
      <div class="right-view">
        <div class="nav-menu">
          <router-link to="/index/portal" class="nav-menu-item" :class="{ active: route.name === 'portal' }">首页</router-link>
          <router-link to="/index/cart" class="nav-menu-item" :class="{ active: route.name === 'cartView' }">购物车</router-link>
          <router-link to="/index/couponCenter" class="nav-menu-item" :class="{ active: route.name === 'couponCenter' }">优惠券</router-link>
          <router-link to="/index/music" class="nav-menu-item" :class="{ active: route.name === 'musicView' }">乐谱</router-link>
          <div class="nav-menu-item" @click="msgVisible=true">消息</div>
        </div>

        <template v-if="userStore.user_token">
          <a-dropdown>
            <a class="ant-dropdown-link user-dropdown" @click="e => e.preventDefault()">
              <img v-if="userAvatar" :src="userAvatar" class="self-img">
              <img v-else :src="AvatarIcon" class="self-img">
              <span class="username">{{ nickName || '暂无' }}</span>
              <i class="down-icon"></i>
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item>
                  <a @click="goUserCenter('orderView')">订单中心</a>
                </a-menu-item>
                <a-menu-item>
                  <a @click="goUserCenter('userInfoEditView')">个人设置</a>
                </a-menu-item>
                <a-menu-item>
                  <a href="/#/admin" class="foot-link" target="_blank">后台管理</a>
                </a-menu-item>
                <a-menu-item>
                  <a @click="quit()">退出</a>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <template v-else>


          <a-dropdown>
            <a class="ant-dropdown-link user-dropdown" @click="e => e.preventDefault()">
              <img :src="AvatarIcon" class="self-img">
              <i class="down-icon"></i>
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item>
                  <a class="foot-link" @click="goLogin()">登录</a>
                </a-menu-item>
                <a-menu-item>
                  <a href="/#/admin" class="foot-link" target="_blank">后台管理</a>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>

        </template>
      </div>
    </div>

    <a-drawer
      title="我的消息"
      placement="right"
      :closable="true"
      :maskClosable="true"
      :visible="msgVisible"
      @close="onClose"
    >
      <a-spin :spinning="loading" style="min-height: 200px;">
        <div class="list-content">
          <div class="notification-view">
            <div class="list">
              <div class="notification-item flex-view" v-for="item in msgData">
                <div class="content-box">
                  <div class="header">
                    <span class="title-txt">{{item.title}}</span>
                    <br/>
                    <span class="time">{{ item.create_time }}</span>
                  </div>
                  <div class="head-text">
                  </div>
                  <div class="content">
                    <p>{{ item.content }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </a-spin>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import {listApi} from '/@/api/notice'
import {detailApi} from '/@/api/user'
import {useUserStore} from "/@/store";
import {BASE_URL} from "/@/store/constants";
import AvatarIcon from '/@/assets/images/avatar.jpg';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const keywordRef = ref()

let loading = ref(false)
let msgVisible = ref(false)
let msgData = ref([] as any)
let userAvatar = ref('')
let nickName = ref('')

onMounted(()=>{
  getMessageList()
  getUserInfo()
})

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

const getMessageList = ()=> {
  loading.value = true
  listApi({}).then(res => {
    msgData.value = res.data
    loading.value = false
  }).catch(err => {
    console.log(err)
    loading.value = false
  })
}
const search = () => {
  const keyword = keywordRef.value.value
  if (!keyword) return

  if (route.name === 'search') {
    router.push({name: 'search', query: {keyword: keyword}})
  } else {
    let text = router.resolve({name: 'search', query: {keyword: keyword}})
    window.open(text.href, '_blank')
  }
}
const goLogin = () => {
  router.push({name: 'login'})
}

const goUserCenter = (menuName) => {
  router.push({name: menuName})
}
const quit= () => {
  userStore.logout().then(res => {
    router.push({name: 'portal'})
  })
}
const onClose = () => {
  msgVisible.value = false;
}

</script>

<style scoped lang="less">
.main-bar-view {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-container {
  width: 1300px;
  height: 60px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 20px;
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

.search-entry {
  position: relative;
  width: 500px;
  height: 40px;
  display: flex;
  margin-right: 40px;

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
    color: #fff;
    border: none;
    border-radius: 0 20px 20px 0;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #ff3a3d;
    }
  }
}

.right-view {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  align-items: center;

  .nav-menu {
    display: inline-flex; // 改为 inline-flex 让子元素在一行
  white-space: nowrap; // 禁止换行
  margin-right: 20px;

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
  white-space: nowrap; // 子项也禁止换行

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

  .user-dropdown {
    display: flex;
    align-items: center;
    cursor: pointer;

    .self-img {
      width: 30px;
      height: 30px;
      border-radius: 50%;
      margin-right: 8px;
    }

    .username {
      font-size: 14px;
      color: #333;
      width: 50px;
      margin-right: 5px;
    }

    .down-icon {
      width: 0;
      height: 0;
      border-left: 5px solid transparent;
      border-right: 5px solid transparent;
      border-top: 5px solid #999;
    }
  }

  .login-btn {
    height: 32px;
    padding: 0 15px;
    background: #ff4d4f;
    color: #fff;
    border: none;
    border-radius: 16px;
    font-size: 14px;
    cursor: pointer;
    margin-right: 30px;
    transition: all 0.3s;

    &:hover {
      background: #ff3a3d;
      transform: translateY(-2px);
    }
  }
}

.notification-item {
  padding-top: 16px;

  .content-box {
    flex: 1;
    border-bottom: 1px solid rgba(233, 233, 233, 0.6);
    padding: 4px 0 16px;
  }

  .header {
    margin-bottom: 12px;
  }

  .title-txt {
    color: #ff4d4f;
    font-weight: 500;
    font-size: 14px;
  }

  .time {
    color: #999;
    font-size: 12px;
  }

  .content {
    margin-top: 4px;
    color: #666;
    font-size: 14px;
    line-height: 22px;
  }
}

.flex-view {
  display: flex;
}
</style>
