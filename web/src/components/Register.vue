<template>
  <div class="register-container">
    <h2>注册</h2>
    <input v-model="username" placeholder="用户名" />
    <input v-model="password" type="password" placeholder="密码" />
    <button @click="register">注册</button>
    <p>已有账号？<a @click="goToLogin">登录</a></p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const password = ref('');

const register = () => {
  // 这里添加注册逻辑
  router.push('/chat');
};

const goToLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
.register-container {

  max-width: 350px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
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