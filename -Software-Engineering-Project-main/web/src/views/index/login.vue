<template>
  <div id="userLayout">
    <div class="login-container">
      <div class="login-panel">
        <div class="login-form-container">
          <div class="login-header">
            <h2>用户登录</h2>
          </div>

          <div class="login-form">
            <div class="form-item">
              <label class="form-label">账号</label>
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                </span>
                <input
                  class="custom-input"
                  type="text"
                  placeholder="请输入管理员账号"
                  v-model="pageData.loginForm.username"
                />
              </div>
            </div>

            <div class="form-item">
              <label class="form-label">密码</label>
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                </span>
                <input
                  class="custom-input"
                  type="password"
                  placeholder="请输入登录密码"
                  v-model="pageData.loginForm.password"
                />
              </div>
            </div>

            <div class="login-options">
            </div>

            <button class="login-button" @click="handleLogin">
              登录系统
            </button>
          </div>

          <div style="margin-top: 10px">
            <a @click="handleCreateUser" class="forget-pwd" style="text-align: left">注册新帐号</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useUserStore } from '/@/store';
  import { message } from 'ant-design-vue';

  const router = useRouter();
  const userStore = useUserStore();

  const pageData = reactive({
    loginForm: {
      username: '',
      password: '',
    },
  });

  const handleLogin = () => {
    if (!pageData.loginForm.username || !pageData.loginForm.password) {
      message.warning('请填写完整的登录信息');
      return;
    }

    userStore
      .login({
        username: pageData.loginForm.username,
        password: pageData.loginForm.password,
      })
      .then((res) => {
        loginSuccess();
        console.log('success==>', userStore.user_name);
        console.log('success==>', userStore.user_id);
        console.log('success==>', userStore.user_token);
      })
      .catch((err) => {
        message.warn(err.msg || '登录失败');
      });
  };

  const handleCreateUser = () => {
    router.push({ name: 'register' });
  };

  const loginSuccess = () => {
    router.push({ name: 'portal' });
    message.success('登录成功！');
  };
</script>

<style scoped lang="less">
  #userLayout {
    position: relative;
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background-image: url("../../assets/images/商城.jpg");
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .login-container {
    display: flex;
    width: 450px;
    height: 430px;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  }

  .login-panel {
    width: 100%;
    background-color: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    padding: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .login-form-container {
    width: 100%;
    max-width: 360px;
  }

  .login-header {
    margin-bottom: 40px;
    text-align: center;

    h2 {
      font-size: 28px;
      color: #333;
      margin-bottom: 12px;
      font-weight: 600;
    }

    p {
      color: #666;
      margin: 0;
      font-size: 15px;
    }
  }

  .login-form {
    .form-item {
      margin-bottom: 24px;
    }

    .form-label {
      display: block;
      font-weight: 500;
      color: #333;
      font-size: 14px;
      margin-bottom: 8px;
    }

    .input-wrapper {
      position: relative;

      .input-icon {
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        display: flex;
        align-items: center;
        color: #aaa;
      }
    }

    .custom-input {
      width: 100%;
      height: 46px;
      border-radius: 8px;
      border: 1px solid #e0e0e0;
      padding: 0 15px 0 40px;
      font-size: 14px;
      transition: all 0.3s;

      &:focus {
        border-color: #3a7bd5;
        box-shadow: 0 0 0 2px rgba(58, 123, 213, 0.1);
        outline: none;
      }
    }
  }

  .login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    font-size: 14px;

    .register-link, .forget-link {
      color: #3a7bd5;
      text-decoration: none;
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .login-button {
    width: 100%;
    height: 46px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    color: white;
    background: linear-gradient(135deg, #3a7bd5, #00d2ff);
    border: none;
    box-shadow: 0 4px 10px rgba(58, 123, 213, 0.3);
    transition: all 0.3s;
    cursor: pointer;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(58, 123, 213, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 40px;
    font-size: 13px;
    color: #999;
  }
</style>
