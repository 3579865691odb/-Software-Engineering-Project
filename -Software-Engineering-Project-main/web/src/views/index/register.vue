<template>
  <div id="userLayout">
    <div class="register-container">
      <div class="register-panel">
        <div class="register-form-container">
          <div class="register-header">
            <h2>注册新账号</h2>
            <p>创建您的个人账号</p>
          </div>
          
          <div class="register-form">
            <div class="form-item">
              <label class="form-label">邮箱</label>
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                </span>
                <input 
                  class="custom-input" 
                  type="text" 
                  placeholder="请输入邮箱" 
                  v-model="tData.loginForm.username"
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
                  placeholder="请输入密码" 
                  v-model="tData.loginForm.password"
                />
              </div>
            </div>
            
            <div class="form-item">
              <label class="form-label">确认密码</label>
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                </span>
                <input 
                  class="custom-input" 
                  type="password" 
                  placeholder="请再次输入密码" 
                  v-model="tData.loginForm.repassword"
                />
              </div>
            </div>
            
            <button class="register-button" @click="handleRegister">
              立即注册
            </button>
            
            <div class="login-link">
              已有账号？<a @click="router.push({name:'login'})" class="link">立即登录</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {userRegisterApi} from '/@/api/user'
import {message} from "ant-design-vue";

const router = useRouter();

const tData = reactive({
  loginForm: {
    username: '',
    password: '',
    repassword: ''
  }
})

const handleRegister = () => {
  if(tData.loginForm.username === ''
    || tData.loginForm.password === ''
    || tData.loginForm.repassword === ''){
    message.warn('请填写完整的注册信息！')
    return;
  }
  
  if(tData.loginForm.password !== tData.loginForm.repassword) {
    message.warn('两次输入的密码不一致！')
    return;
  }

  userRegisterApi({
    username: tData.loginForm.username,
    password: tData.loginForm.password,
    rePassword: tData.loginForm.repassword
  }).then(res => {
    message.success('注册成功！')
    router.push({name: 'login'})
  }).catch(err => {
    message.error(err.msg || '注册失败')
  })
}
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

  .register-container {
    display: flex;
    width: 450px;
    height: 550px;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  }

  .register-panel {
    width: 100%;
    background-color: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    padding: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .register-form-container {
    width: 100%;
    max-width: 360px;
  }

  .register-header {
    margin-bottom: 30px;
    text-align: center;

    h2 {
      font-size: 28px;
      color: #333;
      margin-bottom: 8px;
      font-weight: 600;
    }

    p {
      color: #666;
      margin: 0;
      font-size: 15px;
    }
  }

  .register-form {
    .form-item {
      margin-bottom: 20px;
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

  .register-button {
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
    margin-top: 10px;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(58, 123, 213, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .login-link {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
    color: #666;
    
    .link {
      color: #3a7bd5;
      text-decoration: none;
      cursor: pointer;
      font-weight: 500;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
</style>
