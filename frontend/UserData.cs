using System.IO;

namespace ChatHub;

public class UserData
{
    public string getName()
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
}