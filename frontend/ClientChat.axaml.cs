using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using System;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Avalonia.Input;
using Avalonia.Threading;

namespace ChatHub;

public partial class ClientChat : Window
{
    private ClientWebSocket _webSocket;
    private CancellationTokenSource _cancellationTokenSource;

    public ClientChat(ClientWebSocket webSocket)
    {
        InitializeComponent();
        _webSocket = webSocket;
        _cancellationTokenSource = new CancellationTokenSource();
        // 启动接收消息的任务
        _ = ReceiveMessagesAsync();
    }

    private async Task ReceiveMessagesAsync()
    {
        var buffer = new byte[1024];
        try
        {
            while (_webSocket.State == WebSocketState.Open && !_cancellationTokenSource.Token.IsCancellationRequested)
            {
                var result = await _webSocket.ReceiveAsync(new ArraySegment<byte>(buffer), _cancellationTokenSource.Token);
                if (result.MessageType == WebSocketMessageType.Text)
                {
                    var message = Encoding.UTF8.GetString(buffer, 0, result.Count);
                    // 在 UI 线程更新消息显示
                    await Dispatcher.UIThread.InvokeAsync(() =>
                    {
                        if (MessageDisplay is TextBlock textBlock)
                        {
                            textBlock.Text += $"{message}\n";
                        }
                    });
                }
                else if (result.MessageType == WebSocketMessageType.Close)
                {
                    await _webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, string.Empty, _cancellationTokenSource.Token);
                    await Dispatcher.UIThread.InvokeAsync(() =>
                    {
                        if (MessageDisplay is TextBlock textBlock)
                        {
                            textBlock.Text += "连接已关闭\n";
                        }
                    });
                }
            }
        }
        catch (Exception ex)
        {
            await Dispatcher.UIThread.InvokeAsync(() =>
            {
                if (MessageDisplay is TextBlock textBlock)
                {
                    textBlock.Text += $"接收消息出错: {ex.Message}\n";
                }
            });
        }
    }

    private async void SendMessage_Click(object sender, RoutedEventArgs e)
    {
        SendMessageAsync();
    }

    protected override void OnClosed(EventArgs e)
    {
        base.OnClosed(e);
        _cancellationTokenSource.Cancel();
        if (_webSocket.State == WebSocketState.Open)
        {
            _webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, string.Empty, CancellationToken.None).Wait();
        }
        _webSocket.Dispose();
    }
    
    private async void MessageInput_KeyDown(object sender, KeyEventArgs e)
    {
        // 当按下回车键时发送消息
        if (e.Key == Key.Enter && !e.KeyModifiers.HasFlag(KeyModifiers.Shift))
        {
            await SendMessageAsync();
            e.Handled = true;
        }
    }

    private async Task SendMessageAsync()
    {
        if (MessageInput is TextBox textBox)
        {
            var message = textBox.Text.Trim();
            if (!string.IsNullOrEmpty(message))
            {
                try
                {
                    var buffer = Encoding.UTF8.GetBytes(message);
                    await _webSocket.SendAsync(new ArraySegment<byte>(buffer), WebSocketMessageType.Text, true, _cancellationTokenSource.Token);
                    // 在 UI 线程更新消息显示
                    await Dispatcher.UIThread.InvokeAsync(() =>
                    {
                        if (MessageDisplay is TextBlock textBlock)
                        {
                            textBlock.Text += $"我: {message}\n";
                        }
                        textBox.Text = string.Empty;
                    });
                }
                catch (Exception ex)
                {
                    await Dispatcher.UIThread.InvokeAsync(() =>
                    {
                        if (MessageDisplay is TextBlock textBlock)
                        {
                            textBlock.Text += $"发送消息出错: {ex.Message}\n";
                        }
                    });
                }
            }
        }
    }
    
    
}