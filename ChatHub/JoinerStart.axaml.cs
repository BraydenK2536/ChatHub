using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;

namespace ChatHub;

public partial class JoinerStart : Window
{
    public JoinerStart()
    {
        InitializeComponent();
// 获取过滤后的 IPv4 地址并显示
        //string allIPv4Addresses = GetAllIPv4Addresses();
        //var localIPText = this.FindControl<TextBlock>("LocalIPText");
        //localIPText.Text = $"内网 IPv4 地址:\n{allIPv4Addresses}";
        var nameTextBlock = this.FindControl<TextBlock>("NameTextBlock");
        if (nameTextBlock != null)
        {
            nameTextBlock.Text = $"昵称: " + LoadName();
        }

        
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
        var MainWindow = new MainWindow();
        MainWindow.Show();
        this.Close();
    }
    
    
    private string LoadName()
    {
        string NamePath = "ChatHubName.txt";
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


    
    
    private async void ConnectButton_Click(object sender, RoutedEventArgs e)
        {
            var portTextBox = this.FindControl<TextBox>("PortTextBox");
            if (portTextBox != null && int.TryParse(portTextBox.Text, out int port))
            {
                var localIp = GetLocalIPv4();
                if (localIp != null)
                {
                    string baseIp = localIp.Substring(0, localIp.LastIndexOf('.') + 1);
                    List<Task> tasks = new List<Task>();

                    for (int i = 1; i <= 254; i++)
                    {
                        string ip = baseIp + i;
                        tasks.Add(CheckPortAsync(ip, port));
                    }

                    await Task.WhenAll(tasks);
                }
            }
        }

    private async Task CheckPortAsync(string ip, int port)
    {
        try
        {
            using (TcpClient client = new TcpClient())
            {
                
                await client.ConnectAsync(IPAddress.Parse(ip), port);
                bool success = client.Connected;
                if (success)
                {
                    
                    // 连接成功，发送连接请求
                    using (NetworkStream stream = client.GetStream())
                    {
                        string message = "连接请求";
                        byte[] data = Encoding.UTF8.GetBytes(message);
                        await stream.WriteAsync(data, 0, data.Length);

                        // 接收响应
                        byte[] buffer = new byte[1024];
                        int bytesRead = await stream.ReadAsync(buffer, 0, buffer.Length);
                        string response = Encoding.UTF8.GetString(buffer, 0, bytesRead);
                        Console.WriteLine($"从 {ip} 收到响应: {response}");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            // 处理异常
        }
    }

        private string GetLocalIPv4()
        {
            string localIp = null;
            using (Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, 0))
            {
                socket.Connect("8.8.8.8", 65530);
                IPEndPoint endPoint = socket.LocalEndPoint as IPEndPoint;
                localIp = endPoint.Address.ToString();
            }
            return localIp;
        }
}