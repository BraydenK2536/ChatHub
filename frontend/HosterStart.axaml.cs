using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using System.Text;
using System.Threading;

namespace ChatHub
{
    public partial class HosterStart : Window
    {
        private string NamePath = "ChatHubName.txt";
        
        // 声明 _tcpListener 字段
        private TcpListener _tcpListener;
        // 声明 _cancellationTokenSource 字段
        private CancellationTokenSource _cancellationTokenSource;



        public HosterStart()
        {
            InitializeComponent();
            // 获取过滤后的 IPv4 地址并显示
            string allIPv4Addresses = GetAllIPv4Addresses();
            var localIpText = this.FindControl<TextBlock>("LocalIPText");
            if (localIpText != null)
            {
                localIpText.Text = $"内网 IPv4 地址:\n{allIPv4Addresses}";
            }

            // 随机生成一个四位数字的连接码
            Random random = new Random();
            int connectPin = random.Next(1000, 10000);
            var connectPinText = this.FindControl<TextBlock>("ConnectPinText");
            if (connectPinText != null)
            {
                connectPinText.Text = $"您的端口: {connectPin}";
            }

            // 显示加入用户信息的 TextBlock
            var joinUserText = this.FindControl<TextBlock>("JoinUserText");
            if (joinUserText != null)
            {
                joinUserText.Text = "这里代码还没写";
            }

            // 获取显示昵称的 TextBlock 并设置文本
            var nameTextBlock = this.FindControl<TextBlock>("NameTextBlock");
            if (nameTextBlock != null)
            {
                nameTextBlock.Text = $"昵称: " + LoadName();
            }
            
            StartListening(connectPin);
        }

        private string GetAllIPv4Addresses()
        {
            List<string> ipv4InfoList = new List<string>();
            NetworkInterface[] networkInterfaces = NetworkInterface.GetAllNetworkInterfaces();

            foreach (NetworkInterface networkInterface in networkInterfaces)
            {
                IPInterfaceProperties ipProperties = networkInterface.GetIPProperties();
                foreach (UnicastIPAddressInformation ipInfo in ipProperties.UnicastAddresses)
                {
                    if (ipInfo.Address.AddressFamily == AddressFamily.InterNetwork &&
                        !IsLocalOrLinkLocal(ipInfo.Address)) // 过滤本地和链路本地地址
                    {
                        // 拼接适配器名称和对应的 IPv4 地址
                        ipv4InfoList.Add($"{networkInterface.Name}: {ipInfo.Address}");
                    }
                }
            }

            if (ipv4InfoList.Count == 0)
            {
                return "未找到符合条件的 IPv4 地址";
            }

            // 使用换行符连接每个条目
            return string.Join("\n", ipv4InfoList);
        }

        private bool IsLocalOrLinkLocal(IPAddress address)
        {
            // 检查是否为本地回环地址
            if (IPAddress.IsLoopback(address))
            {
                return true;
            }

            // 检查是否为链路本地地址（169.254.x.x）
            byte[] bytes = address.GetAddressBytes();
            if (bytes[0] == 169 && bytes[1] == 254)
            {
                return true;
            }

            return false;
        }

        private void InitializeComponent()
        {
            AvaloniaXamlLoader.Load(this);
        }

        private void BackButton_OnClick(object? sender, RoutedEventArgs e)
        {
            var mainWindow = new MainWindow();
            mainWindow.Show();
            this.Close();
        }

        private string LoadName()
        {
            // 初始化返回值为空字符串
            string name = string.Empty;
            if (File.Exists(NamePath))
            {
                try
                {
                    // 读取文件内容
                    name = File.ReadAllText(NamePath);
                }
                catch (IOException)
                {
                    // 处理文件读取错误，保持返回值为空字符串
                }
            }

            // 返回读取到的内容或空字符串
            return name;
        }
        private void StartListening(int port)
       {
           try
           {
               // 选择一个端口，这里使用 8888 作为示例
               _tcpListener = new TcpListener(IPAddress.Any, port);
               _tcpListener.Start();

               _cancellationTokenSource = new CancellationTokenSource();
               var cancellationToken = _cancellationTokenSource.Token;

               // 在新线程中开始接受连接
               ThreadPool.QueueUserWorkItem(async _ =>
               {
                   try
                   {
                       while (!cancellationToken.IsCancellationRequested)
                       {
                           // 等待客户端连接
                           TcpClient client = await _tcpListener.AcceptTcpClientAsync(cancellationToken);
                           // 处理客户端连接
                           HandleClient(client);
                       }
                   }
                   catch (OperationCanceledException)
                   {
                       // 操作被取消
                   }
                   catch (Exception ex)
                   {
                       // 处理异常
                       Console.WriteLine($"监听错误: {ex.Message}");
                   }
               });
           }
           catch (Exception ex)
           {
               // 处理异常
               Console.WriteLine($"启动监听失败: {ex.Message}");
           }
       }

       private async void HandleClient(TcpClient client)
       {
           try
           {
               using (NetworkStream stream = client.GetStream())
               {
                   byte[] buffer = new byte[1024];
                   int bytesRead = await stream.ReadAsync(buffer, 0, buffer.Length);
                   string message = Encoding.UTF8.GetString(buffer, 0, bytesRead);

                   // 处理接收到的消息
                   Console.WriteLine($"收到连接: {message}");

                   // 发送响应
                   string response = "连接成功";
                   byte[] responseBytes = Encoding.UTF8.GetBytes(response);
                   await stream.WriteAsync(responseBytes, 0, responseBytes.Length);
               }
           }
           catch (Exception ex)
           {
               // 处理异常
               Console.WriteLine($"处理客户端连接错误: {ex.Message}");
           }
           finally
           {
               client.Close();
           }
       }

       protected override void OnClosed(EventArgs e)
       {
           base.OnClosed(e);
           // 停止监听
           if (_tcpListener != null)
           {
               _tcpListener.Stop();
           }
           if (_cancellationTokenSource != null)
           {
               _cancellationTokenSource.Cancel();
           }
       }

   }


}