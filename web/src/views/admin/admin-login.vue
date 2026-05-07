<!-- eslint-disable prettier/prettier -->
<template>
  <div id="userLayout">
    <div class="login-container">
      <div class="right-panel">
        <div class="login-form-container">
          <div class="login-header">
            <h2>管理员登录</h2>
          </div>

          <a-form
            ref="myform"
            layout="vertical"
            :model="data.loginForm"
            :rules="data.rules"
            :hideRequiredMark="true"
            class="login-form"
          >
            <a-form-item name="username" :colon="false">
              <template #label>
                <span class="form-label">账号</span>
              </template>
              <a-input
                size="large"
                placeholder="请输入管理员账号"
                v-model:value="data.loginForm.username"
                @pressEnter="handleSubmit"
                class="custom-input"
              >
                <template #prefix>
                  <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="password" :colon="false">
              <template #label>
                <span class="form-label">密码</span>
              </template>
              <a-input
                size="large"
                type="password"
                placeholder="请输入登录密码"
                v-model:value="data.loginForm.password"
                @pressEnter="handleSubmit"
                class="custom-input"
              >
                <template #prefix>
                  <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                </template>
              </a-input>
            </a-form-item>

            <div class="login-options">
            </div>

            <a-form-item>
              <a-button
                class="login-button"
                type="primary"
                :loading="loginBtn"
                size="large"
                block
                @click="handleSubmit"
              >
                登录系统
              </a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useUserStore } from '/@/store';
  import logoImage from '/@/assets/images/shop-logo.png';

  const router = useRouter();
  const userStore = useUserStore();

  import { message } from 'ant-design-vue';

  const myform = ref();

  const loginBtn = ref<Boolean>(false);
  const checked = ref<Boolean>(false);
  const data = reactive({
    loginForm: {
      username: '',
      password: '',
    },
    rules: {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    },
  });

  const handleSubmit = () => {
    myform.value
      ?.validate()
      .then(() => {
        loginBtn.value = true;
        handleLogin();
      })
      .catch(() => {
        message.warning('请填写完整的登录信息');
      });
  };

  const handleLogin = () => {
    userStore
      .adminLogin({
        username: data.loginForm.username,
        password: data.loginForm.password,
      })
      .then((res) => {
        loginSuccess();
      })
      .catch((err) => {
        loginBtn.value = false;
        message.error(err.msg || '登录失败，请检查账号密码');
      });
  };

  const loginSuccess = () => {
    router.push({ path: '/admin' });
    message.success('登录成功！');
  };
</script>

<style lang="less" scoped>
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
    width: 500px;
    height: 430px;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  }

  .left-panel {
    width: 50%;
    background: linear-gradient(135deg, #3a7bd5, #00d2ff);
    padding: 40px;
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .branding {
    display: flex;
    align-items: center;
    margin-bottom: 30px;

    .logo {
      width: 40px;
      height: 40px;
      margin-right: 15px;
      border-radius: 8px;
      background: white;
      padding: 5px;
    }

    .branding-text {
      h1 {
        font-size: 22px;
        margin: 0;
        font-weight: 600;
      }
      p {
        margin: 5px 0 0;
        opacity: 0.8;
        font-size: 14px;
      }
    }
  }

  .features {
    margin-top: auto;

    .feature-item {
      display: flex;
      margin-bottom: 30px;
      align-items: flex-start;

      .feature-icon {
        background: rgba(255, 255, 255, 0.2);
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
      }

      .feature-content {
        h3 {
          margin: 0 0 5px;
          font-size: 16px;
          font-weight: 600;
        }
        p {
          margin: 0;
          opacity: 0.8;
          font-size: 14px;
          line-height: 1.5;
        }
      }
    }
  }

  .right-panel {
    width: 100%;
    background: white;
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
    margin-bottom: 30px;
    text-align: center;

    h2 {
      font-size: 26px;
      color: #333;
      margin-bottom: 10px;
      font-weight: 600;
    }

    p {
      color: #666;
      margin: 0;
      font-size: 15px;
    }
  }

  .login-form {
    .form-label {
      font-weight: 500;
      color: #333;
      font-size: 14px;
    }

    :deep(.ant-form-item) {
      margin-bottom: 20px;
    }

    :deep(.ant-form-item-label) {
      padding-bottom: 6px;
    }

    .custom-input {
      border-radius: 8px;
      border: 1px solid #e0e0e0;
      transition: all 0.3s;

      &:hover, &:focus {
        border-color: #3a7bd5;
        box-shadow: 0 0 0 2px rgba(58, 123, 213, 0.1);
      }
    }

    .input-icon {
      color: #aaa;
      margin-right: 6px;
    }
  }

  .login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    font-size: 14px;

    .forget-link {
      color: #3a7bd5;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .login-button {
    height: 46px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    background: linear-gradient(135deg, #3a7bd5, #00d2ff);
    border: none;
    box-shadow: 0 4px 10px rgba(58, 123, 213, 0.3);
    transition: all 0.3s;

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
    margin-top: 20px;
    font-size: 13px;
    color: #999;

    a {
      color: #3a7bd5;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
</style>
