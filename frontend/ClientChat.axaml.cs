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
        // ����������Ϣ������
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
                    // �� UI �̸߳�����Ϣ��ʾ
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
                            textBlock.Text += "�����ѹر�\n";
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
                    textBlock.Text += $"������Ϣ����: {ex.Message}\n";
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
        // �����»س���ʱ������Ϣ
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
                    // �� UI �̸߳�����Ϣ��ʾ
                    await Dispatcher.UIThread.InvokeAsync(() =>
                    {
                        if (MessageDisplay is TextBlock textBlock)
                        {
                            textBlock.Text += $"��: {message}\n";
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
                            textBlock.Text += $"������Ϣ����: {ex.Message}\n";
                        }
                    });
                }
            }
        }
    }
    
    
}