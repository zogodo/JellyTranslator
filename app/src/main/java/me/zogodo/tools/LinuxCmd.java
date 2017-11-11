/**
 * Created by zogod on 2016/11/27.
 */
package me.zogodo.tools;

import me.zogodo.stardict.MainActivity;

import java.io.*;

public class LinuxCmd
{
    public static void PerformCmd(String cmd) throws IOException
    {
        // 执行Linux命令
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(cmd);
    }

    public static String[] exportBz2FIle(String bz2_file_path) throws IOException
    {
        // 解压 .tar.bz2 文件
        File bz2_file = new File(bz2_file_path);
        String bz2_dir = bz2_file.getParent();
        String cmd = MainActivity.busy_box_path + "tar -xjvf " + bz2_file_path + " -C " + bz2_dir;

        String[] result = getCmdReadLine(cmd, bz2_dir);

        // 删除字典压缩包
        cmd = "rm " + bz2_file_path;
        PerformCmd(cmd);

        return result;
    }

    public static String[] getCmdReadLine(String cmd) throws IOException
    {
        Runtime runtime = Runtime.getRuntime();
        // 执行命令，并且获得Process对象
        Process process = runtime.exec(cmd);
        // 获得结果的输入流
        InputStream input = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String strLine = br.readLine();
        String[] cmd_str = new String[100];
        for (int i = 0; i < 100 && strLine != null; i++)
        {
            cmd_str[i] = new String();
            cmd_str[i] = strLine;
            strLine = br.readLine();
        }
        return cmd_str;
    }

    public static String[] getCmdReadLine(String cmd, String working_dir) throws IOException
    {
        Runtime runtime = Runtime.getRuntime();
        // 执行命令，并且获得Process对象
        File file = new File(working_dir);
        Process process = runtime.exec(cmd, null, file);
        // 获得结果的输入流
        InputStream input = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String strLine = br.readLine();
        String[] cmd_str = new String[100];
        for (int i = 0; i < 100 && strLine != null; i++)
        {
            cmd_str[i] = new String();
            cmd_str[i] = strLine;
            strLine = br.readLine();
        }
        return cmd_str;
    }

}
