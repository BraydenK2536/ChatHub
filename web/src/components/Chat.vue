<template>
  <div class="chat-container">
    <div class="connect-status-container">
      <button @click="refreshConnection">连接服务器</button>
      <div class="status">连接状态: {{ connectionStatus }}</div>
    </div>
    <!-- 显示错误消息 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <!-- 显示消息历史 -->
    <div class="messages" ref="messagesRef">
      <div v-for="(msg, index) in messages" :key="index" :class="['message', { 'self-message': msg.isSelf }]">
        <!-- 新增消息头部容器（包含 name 和时间） -->
        <div class="message-header">
          <span class="message-name">{{ msg.name }}</span>
          <span class="message-time">{{ msg.time }}</span>
        </div>
        <!-- 消息内容单独显示 -->
        <div class="message-content">{{ msg.content }}</div>
      </div>
    </div>
    <!-- 输入消息的输入框和发送按钮 -->
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
import { useRouter, useRoute } from 'vue-router';  // 新增 useRoute

const route = useRoute();  // 获取路由参数
const router = useRouter();

// 定义响应式变量（与原 App.vue 中 Chat 相关的逻辑完全迁移）
const socket = ref(null);
const messages = ref([]);
const messageInput = ref('');
const connectionStatus = ref('未连接');
const serverUrl = ref(route.query.serverUrl || 'ws://47.109.103.88:7833/chat');
const errorMessage = ref('');
const messagesRef = ref(null);

// 格式化时间函数
const formatTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 滚动到底部函数
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
};

// 新增重连间隔时间（毫秒）
const RECONNECT_INTERVAL = 500;
// 新增重连定时器引用
let reconnectTimer = null;

// 清除重连定时器
const clearReconnectTimer = () => {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
};

// WebSocket 连接逻辑
const connectWebSocket = () => {
  if (socket.value?.readyState === WebSocket.OPEN) return;
  clearReconnectTimer();
  try {
    socket.value = new WebSocket(serverUrl.value);
    socket.value.onopen = () => {
      connectionStatus.value = '已连接';
      clearReconnectTimer();
    };
    socket.value.onmessage = (event) => {
      try {
        const msgData = JSON.parse(event.data);
        messages.value.push({
          content: msgData.message,
          isSelf: msgData.name === route.query.username,
          time: msgData.time ? msgData.time.replace('T', ' ').split(':').slice(0, 2).join(':') : formatTime(),
          name: msgData.name
        });
        scrollToBottom();
      } catch (parseError) {
        console.error('解析接收到的消息失败:', parseError);
        // 关键修复：使用三元表达式替代 if 语句
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
      // 启动重连定时器
      reconnectTimer = setTimeout(connectWebSocket, RECONNECT_INTERVAL);
    };
    socket.value.onclose = () => {
      connectionStatus.value = '连接已关闭';
      // 启动重连定时器
      reconnectTimer = setTimeout(connectWebSocket, RECONNECT_INTERVAL);
    };
  } catch (error) {
    errorMessage.value = `无效的WebSocket地址: ${error}`;
    connectionStatus.value = '连接失败';
    // 启动重连定时器
    reconnectTimer = setTimeout(connectWebSocket, RECONNECT_INTERVAL);
  }
};

// 生命周期钩子
onMounted(() => {
  connectWebSocket();
});

onBeforeUnmount(() => {
  if (socket.value) socket.value.close();
  clearReconnectTimer();
});

// 发送消息逻辑
const sendMessage = () => {
  if (messageInput.value.trim() && socket.value?.readyState === WebSocket.OPEN) {
    const messageObj = {
      type: 'USER_MESSAGE',
      message: messageInput.value,
      // 关键修改：从路由参数中获取用户名（优先使用传递的，否则匿名）
      name: route.query.username || '匿名用户'
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

// 刷新连接
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
  padding: 0.27rem;
  padding-left: 0.6rem;
  padding-right: 0.6rem;
  margin: 0.5rem 0;
  background: #f5f5f5;
  border-radius: 8px; /* 原4px → 增大为8px */
  max-width: 70%;
  width: fit-content;
}

button {
  padding: 0.3025rem 0.605rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 8px; /* 原4px → 增大为8px */
  cursor: pointer;
}

/* 自己的消息：右对齐 */
.self-message {
  background: #e3f2fd;
  text-align: right;
  margin-left: auto; /* 关键：左侧自动外边距实现右对齐 */
}

/* 他人的消息：左对齐 */
.message:not(.self-message) {
  background: #f5f5f5;
  text-align: left;
  margin-right: auto; /* 关键：右侧自动外边距实现左对齐 */
}

/* 消息头部容器（name + 时间） */
.message-header {
  display: flex;
  justify-content: space-between; /* name 和时间左右分布 */
  align-items: center;
  margin-bottom: 0.3rem; /* 与内容的间距 */
}

/* 自己的消息头部右对齐 */
.self-message .message-header {
  justify-content: flex-end; /* name 和时间整体右对齐 */
  gap: 0.5rem; /* 时间与 name 的间距 */
}

/* 他人消息头部左对齐 */
.message:not(.self-message) .message-header {
  justify-content: flex-start; /* name 和时间整体左对齐 */
  gap: 0.5rem; /* 时间与 name 的间距 */
}

/* 消息发送者名称样式 */
.message-name {
  font-weight: bold; /* 加粗显示 */
  color: #2c3e50; /* 深色字体更突出 */
}

/* 消息时间样式（调整原有样式） */
.message-time {
  font-size: 0.75rem;
  color: #888;
  white-space: nowrap; /* 时间不换行 */
}

/* 消息内容样式（调整原有样式） */
.message-content {
  word-break: break-word;
  line-height: 1.4; /* 内容行高更舒适 */
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

button {
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