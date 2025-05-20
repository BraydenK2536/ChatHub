<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';

// 定义响应式变量
const socket = ref(null);
const messages = ref([]);
const messageInput = ref('');
const connectionStatus = ref('未连接');
const serverUrl = ref('ws://103.36.220.55:3003/chat'); // 默认地址
const errorMessage = ref('');
const username = ref(''); // 新增用户名变量
let connectionInterval = null;
const messagesRef = ref(null);

// 格式化时间函数
const formatTime = () => {
  const now = new Date();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  return `[${month}-${day} ${hours}:${minutes}]`;
};

// 滚动到聊天栏底部的函数
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
};

// 连接 WebSocket 服务器的函数
const connectWebSocket = () => {
  if (socket.value?.readyState === WebSocket.OPEN) {
    return;
  }

  try {
    socket.value = new WebSocket(serverUrl.value);

    socket.value.onopen = () => {
      connectionStatus.value = '已连接';
      // 连接成功后清除定时器
      if (connectionInterval) {
        clearInterval(connectionInterval);
        connectionInterval = null;
      }
    };

    socket.value.onmessage = (event) => {
      try {
        const msgData = JSON.parse(event.data);
        messages.value.push({ 
          content: msgData.message, 
          isSelf: false, 
          time: msgData.time || formatTime(),
          name: msgData.name
        });
        scrollToBottom(); // 收到消息后滚动到聊天栏底部
      } catch (parseError) {
        console.error('解析接收到的消息失败:', parseError);
        messages.value.push({ 
          content: event.data, 
          isSelf: false, 
          time: formatTime(),
          name: '未知用户'
        });
        scrollToBottom(); // 收到消息后滚动到聊天栏底部
      }
    };

    socket.value.onerror = (error) => {
      console.error('WebSocket错误:', error);
      connectionStatus.value = '连接错误';
    };

    socket.value.onclose = () => {
      connectionStatus.value = '连接已关闭';
      // 连接关闭后重新启动定时器
      if (!connectionInterval) {
        connectionInterval = setInterval(connectWebSocket, 1000);
      }
    };
  } catch (error) {
    errorMessage.value = `无效的WebSocket地址: ${error}`;
    connectionStatus.value = '连接失败';
  }
};

// 发送消息的函数
const sendMessage = () => {
  if (messageInput.value.trim() && socket.value?.readyState === WebSocket.OPEN) {
    const messageObj = {
      type: 'USER_MESSAGE',
      message: messageInput.value,
      name: username.value.trim() || '匿名用户'
    };
    const messageJson = JSON.stringify(messageObj);
    // 发送消息时，将消息添加到消息列表，并标记为自己发送的，记录时间
    messages.value.push({ 
      content: messageObj.message, 
      isSelf: true, 
      time: formatTime(),
      name: messageObj.name
    });
    socket.value.send(messageJson);
    messageInput.value = '';
    scrollToBottom(); // 发送消息后滚动到聊天栏底部
  }
};

// 组件挂载时启动连接定时器
onMounted(() => {
  connectionInterval = setInterval(connectWebSocket, 1000);
});

// 组件卸载时清除定时器并关闭连接
onBeforeUnmount(() => {
  if (connectionInterval) {
    clearInterval(connectionInterval);
  }
  if (socket.value) {
    socket.value.close();
  }
});

// 新增刷新连接函数
const refreshConnection = () => {
  if (connectionInterval) {
    clearInterval(connectionInterval);
  }
  if (socket.value) {
    socket.value.close();
  }
  connectionInterval = setInterval(connectWebSocket, 1000);
};
</script>

<template>
  <main>
    <div class="chat-container">
      <!-- 新增输入用户名和服务器地址的输入框容器 -->
      <div class="username-server-container">
        <!-- 交换位置，先显示服务器输入框 -->
        <div class="server-config">
          <label for="serverUrl">服务器</label>
          <input 
            id="serverUrl"
            v-model="serverUrl"
            placeholder="ws://服务器地址"
            class="server-input"
          />
        </div>
        <div class="username-input">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="username"
            placeholder="输入用户名"
          />
        </div>
      </div>
      <!-- 新增一个容器让按钮和连接状态平行 -->
      <div class="connect-status-container">
        <button @click="refreshConnection">连接服务器</button>
        <div class="status">连接状态: {{ connectionStatus }}</div>
      </div>
      <!-- 显示错误消息 -->
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <!-- 显示消息历史 -->
      <div class="messages" ref="messagesRef">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', { 'self-message': msg.isSelf }]">
          <div class="message-time">{{ msg.time }}</div>
          <div class="message-content">{{ msg.name }}: {{ msg.content }}</div>
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
  </main>
</template>

<style scoped>
.chat-container {
  max-width: 1200px;
  margin: 1.21rem auto; /* 在 1.1rem 基础上增大 10% */
  padding: 0.605rem; /* 在 0.55rem 基础上增大 10% */
  border: 1px solid #eee;
}

/* 新增连接状态和按钮容器样式 */
.connect-status-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.status {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0; /* 重置底部外边距 */
}

/* 用户名和服务器输入框容器样式 */
.username-server-container {
  display: flex;
  gap: 0.605rem; /* 在 0.55rem 基础上增大 10% */
  margin-bottom: 0.605rem; /* 在 0.55rem 基础上增大 10% */
}

.username-server-container > div {
  flex: 1;
}

/* 新增用户名输入框样式 */
.username-input input,
.server-config input {
  margin-bottom: 0.3025rem; /* 在 0.275rem 基础上增大 10% */
  width: 100%;
}

/* 添加标签样式 */
.username-input label,
.server-config label {
  display: block;
  margin-bottom: 0.3025rem; /* 在 0.275rem 基础上增大 10% */
  font-weight: bold;
}

/* 调整服务器配置区域为弹性布局 */
.server-config {
  display: flex;
  flex-direction: column;
}

/* 新增连接服务器按钮样式 */
.server-config button {
  align-self: flex-start;
  padding: 0.5rem 1rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.messages {
  height: 600px;
  overflow-y: auto;
  margin: 0.9075rem 0; /* 在 0.825rem 基础上增大 10% */
  border: 2px solid #ddd;
  padding: 0.605rem; /* 在 0.55rem 基础上增大 10% */
}

.message {
  padding: 0.3025rem; /* 在 0.275rem 基础上增大 10% */
  margin: 0.3025rem 0; /* 在 0.275rem 基础上增大 10% */
  background: #f5f5f5;
  border-radius: 4px;
}

/* 自己发送的消息样式 */
.self-message {
  background: #e3f2fd;
  text-align: right;
}

.message-time {
  font-size: 0.8rem;
  color: #888;
  margin-bottom: 0.121rem; /* 在 0.11rem 基础上增大 10% */
}

.message-content {
  word-break: break-word;
}

.input-area {
  display: flex;
  gap: 0.3025rem; /* 在 0.275rem 基础上增大 10% */
}

input {
  flex: 1;
  padding: 0.3025rem; /* 在 0.275rem 基础上增大 10% */
  border: 1px solid #ddd;
}

button {
  padding: 0.3025rem 0.605rem; /* 在 0.275rem 和 0.55rem 基础上增大 10% */
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.error-message {
  color: red;
  margin-bottom: 0.605rem; /* 在 0.55rem 基础上增大 10% */
}
</style>