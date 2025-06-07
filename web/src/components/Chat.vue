<template>
  <div class="chat-container">
    <div class="connect-status-container">
      <button @click="refreshConnection">连接服务器</button>
      <div class="status">连接状态: {{ connectionStatus }}</div>
    </div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <div class="messages" ref="messagesRef">
      <div v-for="(msg, index) in messages" :key="index" :class="['message', { 'self-message': msg.isSelf }]">
        <div class="message-header">
          <span class="message-name">{{ msg.name }}</span>
          <span class="message-time">{{ msg.time }}</span>
        </div>
        <div class="message-content">{{ msg.content }}</div>
      </div>
    </div>
    <div class="input-area">
      <input
          v-model="messageInput"
          @keyup.enter="sendMessage"
          placeholder="输入消息"
      />
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 定义 ref 来存储从 sessionStorage 获取的数据
const username = ref('');
const serverUrl = ref('');

const socket = ref(null);
const messages = ref([]);
const messageInput = ref('');
const connectionStatus = ref('未连接');
const errorMessage = ref('');
const messagesRef = ref(null);

const formatTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
};

const connectWebSocket = () => {
  if (!serverUrl.value) {
    errorMessage.value = "服务器地址未设置，无法连接。";
    connectionStatus.value = '连接失败';
    return;
  }
  if (socket.value?.readyState === WebSocket.OPEN) return;

  try {
    socket.value = new WebSocket(serverUrl.value);
    socket.value.onopen = () => {
      connectionStatus.value = '已连接';
      errorMessage.value = '';
    };
    socket.value.onmessage = (event) => {
      try {
        const msgData = JSON.parse(event.data);
        messages.value.push({
          content: msgData.message,
          isSelf: msgData.name === username.value, // 使用 ref 判断是否为自己的消息
          time: msgData.time ? msgData.time.replace('T', ' ').split(':').slice(0, 2).join(':') : formatTime(),
          name: msgData.name
        });
        scrollToBottom();
      } catch (parseError) {
        console.error('解析接收到的消息失败:', parseError);
        messages.value.push({
          content: event.data,
          isSelf: false,
          time: formatTime(),
          name: '未知用户'
        });
        scrollToBottom();
      }
    };
    socket.value.onerror = (error) => {
      console.error('WebSocket错误:', error);
      connectionStatus.value = '连接错误';
      errorMessage.value = "无法连接到WebSocket服务器，请检查地址或网络状态。";
    };
    socket.value.onclose = () => {
      connectionStatus.value = '连接已关闭';
    };
  } catch (error) {
    errorMessage.value = `无效的WebSocket地址: ${error.message}`;
    connectionStatus.value = '连接失败';
  }
};

const sendMessage = () => {
  if (messageInput.value.trim() && socket.value?.readyState === WebSocket.OPEN) {
    const messageObj = {
      type: 'USER_MESSAGE',
      message: messageInput.value,
      name: username.value || '匿名用户' // 使用 ref 作为发送者名称
    };
    const messageJson = JSON.stringify(messageObj);
    messages.value.push({
      content: messageObj.message,
      isSelf: true,
      time: formatTime(),
      name: messageObj.name
    });
    socket.value.send(messageJson);
    messageInput.value = '';
    scrollToBottom();
  }
};

// 组件加载时，从 sessionStorage 获取登录信息并尝试连接
onMounted(() => {
  const storedUsername = sessionStorage.getItem('username');
  const storedServerUrl = sessionStorage.getItem('serverUrl');

  if (storedUsername && storedServerUrl) {
    // 如果信息存在，则更新 ref 并自动连接
    username.value = storedUsername;
    serverUrl.value = storedServerUrl;
    connectWebSocket();
  } else {
    // 如果信息不存在，提示并跳转回登录页
    errorMessage.value = '未获取到登录信息，请先登录。';
    setTimeout(() => {
      router.push('/login');
    }, 2000);
  }
});

onBeforeUnmount(() => {
  if (socket.value) socket.value.close();
});

const refreshConnection = () => {
  if (socket.value) socket.value.close();
  connectWebSocket();
};
</script>

<style scoped>
.chat-container {
  max-width: 1200px;
  margin: 1.21rem auto;
  padding: 0.605rem;
  border: 1px solid #eee;
  height: auto;
}

.connect-status-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.status {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0;
}

.messages {
  height: 650px;
  overflow-y: auto;
  margin: 0.9075rem 0;
  border: 2px solid #ddd;
  padding: 0.605rem;
}

.message {
  padding: 0.3025rem;
  margin: 0.3025rem 0;
  background: #f5f5f5;
  border-radius: 8px;
  max-width: 70%;
  width: fit-content;
}

button {
  padding: 0.3025rem 0.605rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.self-message {
  background: #e3f2fd;
  text-align: right;
  margin-left: auto;
}

.message:not(.self-message) {
  background: #f5f5f5;
  text-align: left;
  margin-right: auto;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.3rem;
}

.self-message .message-header {
  justify-content: flex-end;
  gap: 0.5rem;
}

.message:not(.self-message) .message-header {
  justify-content: flex-start;
  gap: 0.5rem;
}

.message-name {
  font-weight: bold;
  color: #2c3e50;
}

.message-time {
  font-size: 0.75rem;
  color: #888;
  white-space: nowrap;
}

.message-content {
  word-break: break-word;
  line-height: 1.4;
}

.input-area {
  display: flex;
  gap: 0.3025rem;
}

input {
  flex: 1;
  padding: 0.3025rem;
  border: 1px solid #ddd;
}

.input-area button {
  padding: 0.3025rem 0.605rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.error-message {
  color: red;
  margin-bottom: 0.605rem;
}
</style>