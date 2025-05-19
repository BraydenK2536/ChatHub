using System;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;

namespace ChatHub;

public partial class ClientStart : Window
{
    private ClientWebSocket webSocket;
    private string connectionStatus;

    public ClientStart()
    {
        InitializeComponent();
        UserData userData = new UserData();
        NameTextBlock.Text = userData.getName();
        // 启动 WebSocket 连接任务
        _ = ConnectToWebSocketServerAsync();
    }

    private async Task ConnectToWebSocketServerAsync()
    {
        ConnectionStatusTextBlock.Text = "正在连接服务器";
        webSocket = new ClientWebSocket();
        try
        {
            // 服务器的 WebSocket 地址
            var uri = new Uri("ws://26.240.93.251:7833/chat");
            // 连接到 WebSocket 服务器
            await webSocket.ConnectAsync(uri, CancellationToken.None);
            Console.WriteLine("已连接到 WebSocket 服务器");
            ConnectionStatusTextBlock.Text = "成功连接";
            connectionStatus = "成功连接";

            // 启动接收消息的任务
            _ = ReceiveMessagesAsync();

            // 示例：发送消息到服务器
            await SendMessageAsync("Hello, WebSocket Server!");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"连接出错: {ex.Message}");
            connectionStatus = $"连接失败: {ex.Message}";
        }
    }

    private async Task ReceiveMessagesAsync()
    {
        var buffer = new byte[1024];
        while (webSocket.State == WebSocketState.Open)
        {
            var result = await webSocket.ReceiveAsync(new ArraySegment<byte>(buffer), CancellationToken.None);
            if (result.MessageType == WebSocketMessageType.Text)
            {
                var message = Encoding.UTF8.GetString(buffer, 0, result.Count);
                Console.WriteLine($"收到消息: {message}");
            }
            else if (result.MessageType == WebSocketMessageType.Close)
            {
                await webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, string.Empty, CancellationToken.None);
                Console.WriteLine("连接已关闭");
            }
        }
    }

    private async Task SendMessageAsync(string message)
    {
        var buffer = Encoding.UTF8.GetBytes(message);
        await webSocket.SendAsync(new ArraySegment<byte>(buffer), WebSocketMessageType.Text, true, CancellationToken.None);
        Console.WriteLine($"已发送消息: {message}");
    }

    private void StartChatButton_OnClick(object? sender, RoutedEventArgs e)
    {
        // 检查 WebSocket 连接状态
        if (webSocket != null && webSocket.State == WebSocketState.Open)
        {
            ClientChat chat = new ClientChat(webSocket);
            chat.Show();
            this.Close();
        }
        else
        {
            // 处理连接未成功的情况
            Console.WriteLine("连接未成功，无法打开聊天窗口");
        }
    }
    private void BackButton_OnClick(object? sender, RoutedEventArgs e)
    {
        var MainWindow = new MainWindow();
        MainWindow.Show();
        this.Close();
    }
}

