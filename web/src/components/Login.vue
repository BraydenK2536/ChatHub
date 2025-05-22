<template>
  <div class="login-container">
    <!-- 使用 div 容器包裹标题并设置内联显示 -->
    <div class="title-container">
      <h2>登录</h2>
      <h2></h2>
    </div>
    <!-- 主内容容器（账号密码框与服务器框水平排列） -->
    <div class="main-content">
      <!-- 账号密码+按钮框 -->
      <div class="auth-box">
        <div class="auth-section">
          <input v-model="username" placeholder="用户名" />
          <input v-model="password" type="password" placeholder="密码" />
        </div>
        <!-- 使用 div 容器包裹登录按钮和注册提示 -->
        <div class="login-register-container">
          <button @click="login">登录</button>
          <p>还没有账号？<a @click="goToRegister">注册</a></p>
        </div>
      </div>
      <!-- 服务器独立框 -->
      <div class="server-box">
        <label for="serverUrl">服务器</label>
        <input 
          id="serverUrl"
          v-model="serverUrl"
          placeholder="ws://服务器地址"
          class="server-input"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const password = ref('');
// 新增服务器地址变量
const serverUrl = ref('ws://1.14.191.95:7833/chat'); 

const login = () => {
  // 这里添加登录逻辑
  // 将服务器地址传递到聊天界面
  router.push({ 
    name: 'Chat', 
    query: { serverUrl: serverUrl.value } 
  });
};

const goToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container {
  max-width: 800px;
  margin: 0 auto; 
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center; 
  align-items: center; 
  min-height: 100vh; 
}

/* 标题容器样式 */
.title-container {
  display: flex;
  align-items: center;
  gap: 10px; /* 两个标题之间的间距 */
  margin-bottom: 20px; /* 标题容器与下方内容的间距 */
}

.title-container h2 {
  margin: 0; /* 移除 h2 默认的外边距 */
}

/* 主内容区水平排列并整体居中 */
.main-content {
  display: flex;
  gap: 30px;
  width: 100%;
  justify-content: center; 
}

/* 账号密码+按钮框（调整内部元素居中） */
.auth-box {
  max-width: 300px;
  flex: 1; 
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 20px;
  background: #f9f9f9;
  min-height: 11vh;
}

/* 输入框居中显示（关键修改） */
.auth-box input {
  width: 98%; /* 宽度略小于父容器 */
  margin: 0 auto 15px; /* 左右自动外边距实现居中 */
  display: block; /* 使输入框成为块级元素 */
  min-height: 4vh; /* 最小高度 */
  font-weight: bold;
  color: #666;
}

/* 登录和注册提示容器样式 */
.login-register-container {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 让按钮和提示分别位于两端 */
  gap: 5px; /* 按钮和提示之间的间距 */
}

/* 登录按钮样式 */
button {
  width: 100px; /* 调整按钮宽度为自动 */
  padding: 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin: 0; /* 移除原有的上下外边距 */
}

/* 注册提示样式 */
.login-register-container p {
  margin: 0; /* 移除 p 标签默认的外边距 */
  font-size: 0.9em; /* 调整字体大小 */
}

/* 服务器独立框（保持原有样式） */
.server-box {
  max-width: 300px;
  flex: 0.7; 
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 20px;
  background: #f9f9f9;
}

.server-box label {
  display: block;
  margin-bottom: 10px;
  font-weight: bold;
  color: #666;
}

.server-box input {
  width: 100%;
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

a {
  color: #42b983;
  cursor: pointer;
}
</style>