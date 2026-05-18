<template>
  <a-layout id="components-layout-demo-custom-trigger">
    <a-layout-header class="admin-header">
      <div class="header">
        <div class="logo-section">
          <div class="sider-toggle" @click="collapsed = !collapsed">
            <menu-unfold-outlined v-if="collapsed" />
            <menu-fold-outlined v-else />
          </div>
        <img class="header-logo" :src="logo">
        <span class="header-title">乐器后台管理系统</span>
        </div>
        <div class="empty"></div>
        <div class="header-actions">
          <a-button type="primary" ghost @click="handlePreview">
            <eye-outlined /> 前台预览
          </a-button>
          <a-dropdown>
            <div class="user-info">
              <img v-if="userAvatar" :src="userAvatar" class="user-avatar-img" />
              <a-avatar v-else :size="32" class="user-avatar">
                <template #icon><user-outlined /></template>
              </a-avatar>
              <span class="user-name">{{ userStore.admin_user_name }}</span>
              <down-outlined />
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="handleProfileClick">
                  <user-outlined /> 个人信息
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <logout-outlined /> 退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
    </a-layout-header>
    <a-layout>
      <a-layout-sider
        v-model:collapsed="collapsed"
        :trigger="null"
        collapsible
        class="admin-sider"
        :width="220"
        :collapsedWidth="80"
      >
        <a-menu
          class="admin-menu"
          v-model:selectedKeys="selectedKeys"
          theme="dark"
          mode="inline"
          @click="handleClick"
          :inline-collapsed="collapsed"
        >
          <a-menu-item key="overview">
            <template #icon><dashboard-outlined /></template>
            <span>仪表盘</span>
          </a-menu-item>
          <a-menu-item key="user">
            <template #icon><user-outlined /></template>
            <span>用户管理</span>
          </a-menu-item>
          <a-menu-item key="classification">
            <template #icon><partition-outlined /></template>
            <span>分类管理</span>
          </a-menu-item>
          <a-menu-item key="tag">
            <template #icon><tag-outlined /></template>
            <span>标签管理</span>
          </a-menu-item>
          <a-menu-item key="coupon">
            <template #icon><gift-outlined /></template>
            <span>优惠券管理</span>
          </a-menu-item>
          <a-menu-item key="order">
            <template #icon><shopping-outlined /></template>
            <span>订单管理</span>
          </a-menu-item>
          <a-menu-item key="thing">
            <template #icon><shopping-cart-outlined /></template>
            <span>商品管理</span>
          </a-menu-item>
          <a-menu-item key="comment">
            <template #icon><comment-outlined /></template>
            <span>评论管理</span>
          </a-menu-item>
          <a-sub-menu key="sub1">
            <template #icon><fund-outlined /></template>
            <template #title>运营管理</template>
            <a-menu-item key="notice">
              <template #icon><notification-outlined /></template>
              <span>通知公告</span>
            </a-menu-item>
            <a-menu-item key="banner">
              <template #icon><picture-outlined /></template>
              <span>轮播图管理</span>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="sub2">
            <template #icon><file-text-outlined /></template>
            <template #title>日志管理</template>
            <a-menu-item key="loginLog">
              <template #icon><login-outlined /></template>
              <span>登录日志</span>
            </a-menu-item>
            <a-menu-item key="opLog">
              <template #icon><interaction-outlined /></template>
              <span>操作日志</span>
            </a-menu-item>
            <a-menu-item key="errorLog">
              <template #icon><warning-outlined /></template>
              <span>错误日志</span>
            </a-menu-item>
          </a-sub-menu>
          <a-menu-item key="sysInfo">
            <template #icon><setting-outlined /></template>
            <span>系统信息</span>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      <a-layout-content :style="{ margin: '24px', minHeight: '200px' }">
        <a-card class="content-card">
        <router-view/>
        </a-card>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import {useRouter, useRoute} from 'vue-router'
import logo from '/@/assets/images/shop-logo.png'
import { onMounted, ref } from 'vue';
import { detailApi } from '/@/api/user';
import { BASE_URL } from "/@/store/constants";

import {
  HomeOutlined,
  AppstoreOutlined,
  FolderOutlined,
  UserOutlined,
  CommentOutlined,
  InfoCircleOutlined,
  TagOutlined,
  PieChartOutlined,
  DollarOutlined,
  LayoutOutlined,
  DatabaseOutlined,
  GiftOutlined,
  EyeOutlined,
  SettingOutlined,
  LogoutOutlined,
  DownOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  ShoppingOutlined,
  ShoppingCartOutlined,
  PartitionOutlined,
  FileTextOutlined,
  NotificationOutlined,
  PictureOutlined,
  LoginOutlined,
  InteractionOutlined,
  WarningOutlined,
  FundOutlined
} from '@ant-design/icons-vue';

import {useUserStore} from "/@/store";

const userStore = useUserStore();
const userAvatar = ref('');

const selectedKeys = ref<any[]>([])
const collapsed = ref<boolean>(false)

const router = useRouter()
const route = useRoute()

// 获取用户头像
const getUserAvatar = () => {
  const userId = userStore.admin_user_id;
  if (!userId) return;
  
  detailApi({ userId: userId })
    .then(res => {
      console.log('获取头像信息:', res.data);
      const userData = res.data;
      
      if (userData && userData.avatar) {
        userAvatar.value = BASE_URL + '/api/staticfiles/avatar/' + userData.avatar;
      }
    })
    .catch(err => {
      console.log('获取头像失败:', err);
    });
};

const handleClick = ({item, key, keyPath}) => {
  console.log('点击路由===>', key)
  router.push({
    name: key,
  })
}

const handlePreview = ()=>{
  let text = router.resolve({name: 'index'})
  window.open(text.href, '_blank')
}

onMounted(() => {
  console.log('当前路由===>', route.name)
  selectedKeys.value = [route.name]
  getUserAvatar(); // 获取用户头像
  
  // 添加事件监听器，从个人信息页面返回时更新头像
  window.addEventListener('focus', () => {
    // 如果当前不是个人信息页面，且从个人信息页面返回，则刷新头像
    if (route.name !== 'profile') {
      getUserAvatar();
    }
  });
  
  // 监听头像更新事件
  window.addEventListener('avatar-updated', () => {
    console.log('收到头像更新事件');
    getUserAvatar();
  });
})

// 监听路由变化
router.beforeEach((to, from, next) => {
  // 如果从个人信息页面离开，刷新头像
  if (from.name === 'profile') {
    setTimeout(() => {
      getUserAvatar();
    }, 500); // 短暂延迟确保数据已更新
  }
  next();
});

const handleLogout = () => {
  userStore.adminLogout().then(res => {
    router.push({name: 'adminLogin'})
  })
}

const handleProfileClick = () => {
  router.push({name: 'profile'})
}
</script>

<style scoped lang="less">
// header样式
.admin-header {
  background: linear-gradient(90deg, #2c3e50, #3a5476);
  padding: 0;
  height: 64px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 10;
}

.header {
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 100%;
  padding: 0 24px;

  .logo-section {
    display: flex;
    align-items: center;

    .sider-toggle {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      margin-right: 16px;
      color: rgba(255, 255, 255, 0.85);
      font-size: 18px;
      cursor: pointer;
      transition: all 0.3s;
      border-radius: 50%;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: #fff;
      }
    }
  }

  .header-logo {
    width: 36px;
    height: 36px;
    cursor: pointer;
    filter: drop-shadow(0 0 2px rgba(255, 255, 255, 0.5));
  }

  .header-title {
    margin-left: 16px;
    font-size: 20px;
    font-weight: 500;
    color: #ffffff;
    letter-spacing: 1px;
  }

  .empty {
    flex: 1;
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 24px;

    .ant-btn {
      border-radius: 4px;
      height: 36px;
      display: flex;
      align-items: center;
    }

    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }

      .user-avatar {
        background: #1890ff;
      }
      
      .user-avatar-img {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid rgba(255, 255, 255, 0.2);
      }

      .user-name {
        margin: 0 8px;
        color: #ffffff;
        font-weight: 500;
      }
    }
  }
}

// Sider样式
.admin-sider {
  background: #001529;
  position: relative;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  transition: all 0.2s;
  }

.admin-menu {
  border-right: 0;
  padding-top: 16px;

  :deep(.ant-menu-item), :deep(.ant-menu-submenu-title) {
    border-radius: 5px;

    &:hover {
      background-color: rgba(255, 255, 255, 0.08);
    }
  }

  :deep(.ant-menu-item-selected) {
    background: #4a5568 !important;

    &::after {
      display: none;
    }
  }

  :deep(.ant-menu-item-active) {
    background: rgba(255, 255, 255, 0.08) !important;
  }

  :deep(.ant-menu-submenu-selected) {
    color: white;
  }

  :deep(.ant-menu-submenu-active) {
    background: rgba(255, 255, 255, 0.08) !important;
  }
}

.content-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  :deep(.ant-card-body) {
    padding: 16px;
  }
}

#components-layout-demo-custom-trigger {
  height: 100%;
  background: #f0f2f5;
}

:deep(.ant-layout-content) {
  overflow-x: hidden;
}

:deep(.ant-layout-sider-trigger) {
  background-color: #001529;
  display: none;
}
</style>
