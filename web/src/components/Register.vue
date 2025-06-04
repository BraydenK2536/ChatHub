<template>
  <div class="register-container">
    <h2>注册</h2>
    <div class="register-box">
      <input v-model="username" placeholder="用户名" />
      <input v-model="password" type="password" placeholder="密码" />
      <button @click="register">注册</button>
      <p>已有账号？<a @click="goToLogin">登录</a></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// 引入组件


const router = useRouter();
const username = ref('');
const password = ref('');
const errorMessage = ref('');

const register = async () => {
  try {
    const response = await fetch('http://47.109.103.88:7833/api/auth/register', { 
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value,
      }),
    });

    if (!response.ok) {
      throw new Error(`注册失败: ${response.statusText}（状态码：${response.status}）`);
    }

    const data = await response.json();
    console.log('注册成功:', data);
    router.push('/chat');
  } catch (error) {
    // 关键修改：区分网络错误和接口错误
    if (error.name === 'TypeError' && error.message.includes('Failed to fetch')) {
      errorMessage.value = '无法连接到服务器，请检查网络或服务器地址是否正确';
    } else {
      errorMessage.value = error.message;
    }
    console.error('注册出错:', error);
  }
};

const goToLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
.register-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.register-box input {
  width: 98%; /* 宽度略小于父容器 */
  margin: 0 auto 15px; /* 左右自动外边距实现居中 */
  display: block; /* 使输入框成为块级元素 */
  min-height: 4vh; /* 最小高度 */
  font-weight: bold;
  color: #666;
}

.register-box {
  max-width: 300px;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 20px;
  background: #f9f9f9;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  /* 关键修改：移除固定高度限制，让容器高度自动适配内容 */
  /* 移除原 max-height: 23vh; */
  /* 移除原 flex: 100; （flex 默认值为 0 1 auto，无需设置过大数值） */
}

input {
  width: 100%;
  margin-bottom: 10px;
  padding: 8px;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
a {
  color: #42b983;
  cursor: pointer;
}
</style>