<template>
  <div class="mine-infos-view">
    <div class="info-box flex-view">
      <img v-if="userAvatar" :src="userAvatar" class="avatar-img">
      <img v-else :src="AvatarImg" class="avatar-img">
      <div class="name-box">
        <h2 class="nick">{{ nickName }}</h2>
        <div class="age">
          <span>活跃1天</span>
          <span class="give-point"></span>
        </div>
      </div>
    </div>
    <div class="counts-view">
      <div class="counts flex-view">
        <div class="fans-box flex-item" @click="clickMenu('collectThingView')">
          <div class="text">收藏</div>
          <div class="num">{{collectCount}}</div>
        </div>
        <div class="split-line">
        </div>
        <div class="follow-box flex-item" @click="clickMenu('wishThingView')">
          <div class="text">购物车</div>
          <div class="num">{{cartCount}}</div>
        </div>
      </div>
    </div>


    <a-tabs default-active-key="1" class="order-box">
      <a-tab-pane  key="1" tab="订单中心">
        <div class="order-box">
          <div class="list">
            <div class="mine-item flex-view" @click="clickMenu('orderView')">
              <img :src="MyOrderImg">
              <span>我的订单</span>
            </div>
            <div class="mine-item flex-view" @click="clickMenu('userCouponsView')">
              <img :src="CouponIconImg">
              <span>我的优惠券</span>
            </div>
            <div class="mine-item flex-view" @click="clickMenu('commentView')">
              <img :src="CommentIconImg">
              <span>我的评论</span>
            </div>
            <div class="mine-item flex-view" @click="clickMenu('addressView')">
              <img :src="AddressIconImage">
              <span>地址管理</span>
            </div>
          </div>
        </div>
      </a-tab-pane>
      <a-tab-pane key="2" tab="个人设置">
        <div class="setting-box">
          <div class="list">
            <div class="mine-item flex-view" @click="clickMenu('userInfoEditView')">
              <img :src="SettingIconImage" alt="编辑资料">
              <span>编辑资料</span>
            </div>
            <div class="mine-item flex-view" @click="clickMenu('securityView')">
              <img :src="SafeIconImage" alt="账号安全">
              <span>账号安全</span>
            </div>
            <div class="mine-item flex-view" @click="clickMenu('pushView')">
              <img :src="PushIconImage" alt="推送设置">
              <span>推送设置</span>
            </div>
            <div class="mine-item flex-view" @click="clickMenu('messageView')">
              <img :src="MessageIconImage" alt="消息管理">
              <span>消息管理</span>
            </div>
          </div>
        </div>
      </a-tab-pane>
    </a-tabs>

  </div>
</template>

<script setup lang="ts">
import AvatarImg from '/@/assets/images/avatar.jpg'
import MyOrderImg from '/@/assets/images/order-icon.svg'
import CommentIconImg from '/@/assets/images/order-thing-icon.svg'
import AddressIconImage from '/@/assets/images/order-address-icon.svg'
import PointIconImage from '/@/assets/images/order-point-icon.svg'
import SettingIconImage from '/@/assets/images/setting-icon.svg'
import SafeIconImage from '/@/assets/images/setting-safe-icon.svg'
import PushIconImage from '/@/assets/images/setting-push-icon.svg'
import MessageIconImage from '/@/assets/images/setting-msg-icon.svg'
import CouponIconImg from '/@/assets/images/coupon-icon.svg'

import {userCollectListApi} from '/@/api/thingCollect'
import {detailApi} from '/@/api/user'
import {BASE_URL} from "/@/store/constants"
import {useUserStore} from '/@/store';
const userStore = useUserStore();
const router = useRouter();


let collectCount = ref(0)
let cartCount = ref(0)
let userAvatar = ref('')
let nickName = ref('')

onMounted(()=>{
  getCollectThingList()
  getCartItemCount()
  getUserInfo()
})

const clickMenu =(name)=> {
  router.push({name: name})
}

const getUserInfo = () => {
  let userId = userStore.user_id
  if (!userId) return

  detailApi({userId: userId}).then(res => {
    if (res.data) {
      console.log("数据", res.data)
      userAvatar.value = BASE_URL + '/api/staticfiles/avatar/' + res.data.avatar
      nickName.value = res.data.nickname
    }
  }).catch(err => {
    console.log(err)
  })
}

const getCollectThingList =()=> {
  let userId = userStore.user_id
  userCollectListApi({userId: userId}).then(res => {
    collectCount.value = res.data.length
  }).catch(err => {
    console.log(err.msg)
  })
}

const getCartItemCount = () => {
  // 从本地存储获取购物车数据
  const storedCart = localStorage.getItem('cart')
  if (storedCart) {
    const cartItems = JSON.parse(storedCart)
    cartCount.value = cartItems.length
  } else {
    cartCount.value = 0
  }
}

</script>

<style scoped lang="less">
.flex-view {
  display: flex;
}

.mine-infos-view {
  width: 235px;
  margin-right: 0;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  height: fit-content;

  .info-box {
    align-items: center;
    padding: 20px;
    background: linear-gradient(135deg, #4684e2, #6a5acd);
    color: white;
    position: relative;
    overflow: hidden;

    &::after {
      content: '';
      position: absolute;
      bottom: -10px;
      right: -10px;
      width: 80px;
      height: 80px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 50%;
    }
  }

  .avatar-img {
    width: 60px;
    height: 60px;
    margin-right: 16px;
    border-radius: 50%;
    border: 2px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    object-fit: cover;
  }

  .name-box {
    flex: 1;
    overflow: hidden;

    .nick {
      color: white;
      font-weight: 600;
      font-size: 18px;
      line-height: 24px;
      height: 24px;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin: 0;
      overflow: hidden;
    }

    .age {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.8);
      height: 16px;
      line-height: 16px;
      margin-top: 8px;
    }

    .give-point {
      color: #fff;
      cursor: pointer;
      float: right;
      text-decoration: underline;
    }
  }

  .counts-view {
    border: none;
    padding: 16px;
  }

  .counts {
    margin-top: 12px;
    text-align: center;
    align-items: center;
    background-color: #f8f9fa;
    border-radius: 6px;
    padding: 12px 8px;

    .flex-item {
      flex: 1;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        transform: translateY(-2px);
      }
    }

    .text {
      height: 16px;
      line-height: 16px;
      color: #6f6f6f;
    }

    .num {
      height: 18px;
      line-height: 18px;
      color: #4684e2;
      font-weight: 600;
      font-size: 16px;
      margin-top: 4px;
    }

    .split-line {
      width: 1px;
      height: 30px;
      background: #dae6f9;
    }
  }

  .intro-box {
    border-top: 1px solid #f0f0f0;
    padding: 16px;

    .title {
      color: #6f6f6f;
      font-size: 12px;
      line-height: 16px;
    }

    .intro-content {
      color: #152844;
      font-size: 14px;
      line-height: 20px;
      overflow: hidden;
      text-overflow: ellipsis;
      margin: 8px 0;
    }
  }

  .create-box {
    cursor: pointer;
    border-top: 1px solid #f0f0f0;
    padding: 16px;

    .title {
      position: relative;
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      line-height: 18px;
      height: 18px;
    }

    .counts {
      margin-top: 12px;
      text-align: center;
      align-items: center;

      .flex-item {
        flex: 1;
        cursor: pointer;
      }

      .split-line {
        width: 1px;
        height: 24px;
        background: #dae6f9;
      }
    }
  }

  .order-box, .setting-box {
    border-top: 1px solid #f0f0f0;
    padding: 16px;

    .title {
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      line-height: 18px;
      height: 18px;
      margin-bottom: 12px;
      padding-left: 10px;
      border-left: 3px solid #4684e2;
    }

    .list {
      padding-left: 8px;

      .mine-item {
        border-top: none;
        cursor: pointer;
        height: 48px;
        align-items: center;
        border-radius: 6px;
        padding: 0 8px;
        margin-bottom: 4px;
        transition: all 0.2s;

        &:hover {
          background-color: #f5f7fa;
          color: #4684e2;

          span {
            color: #4684e2;
          }
        }

        img {
          width: 20px;
          margin-right: 12px;
          height: 20px;
        }

        span {
          color: #333;
          font-size: 14px;
          transition: all 0.2s;
        }
      }
    }
  }
}
</style>
