using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;

namespace ChatHub;

public partial class ClientStart : Window
{
    public ClientStart()
    {
        InitializeComponent();
        
    }

    private void BackButton_OnClick(object? sender, RoutedEventArgs e)
    {
        var MainWindow = new MainWindow();
        MainWindow.Show();
        this.Close();
    }
    
}

