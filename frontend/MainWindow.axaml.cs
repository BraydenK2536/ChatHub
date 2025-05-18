using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using Avalonia.Interactivity;

namespace ChatHub
{
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            LoadName();
#if DEBUG
            this.AttachDevTools();
            
#endif
            // 获取过滤后的 IPv4 地址并显示
            string allIPv4Addresses = GetAllIPv4Addresses();
            var localIPText = this.FindControl<TextBlock>("LocalIPText");
            localIPText.Text = $"内网 IPv4 地址:\n{allIPv4Addresses}";
            
            
            
            
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
        
        
        // 按钮点击事件处理程序
        private void HostButton_Click(object sender, Avalonia.Interactivity.RoutedEventArgs e)
        {
            // 创建 HolderStart 窗口实例
            var holderStartWindow = new HosterStart();
            // 显示 HolderStart 窗口
            holderStartWindow.Show();
            // 关闭当前窗口
            this.Close();
        }

        private void JoinButton_Click(object sender, Avalonia.Interactivity.RoutedEventArgs e)
        {
            // 创建 JoinerStart 窗口实例
            var joinerStartWindow = new JoinerStart();
            joinerStartWindow.Show();
            this.Close();
        }
        

        private void InitializeComponent()
        {
            AvaloniaXamlLoader.Load(this);
            
        }

      
        private void ClientButton_OnClick(object? sender, RoutedEventArgs e)
        {
            var clientStartWindow = new ClientStart();
            clientStartWindow.Show();
            this.Close();
        }
        
        
        private const string NamePath = "ChatHubName.txt";

        private void LoadName()
        {
            if (File.Exists(NamePath))
            {
                try
                {
                    string nickname = File.ReadAllText(NamePath);
                    if (this.FindControl<TextBox>("NameTextBox") is TextBox NameTextBox)
                    {
                        NameTextBox.Text = nickname;
                    }
                }
                catch (IOException)
                {
                    // 处理文件读取错误
                }
            }
        }
        private void SaveNameButtom_Click(object? sender, RoutedEventArgs e)
        {
            if (this.FindControl<TextBox>("NameTextBox") is TextBox NameTextBox)
            {
                string name = NameTextBox.Text;
                try
                {
                    File.WriteAllText(NamePath, name);
                }
                catch (IOException)
                {
                    // 处理文件写入错误
                }
            }
        }
        

    }
}