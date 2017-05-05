/**
 * Created by zogod on 2016/11/27.
 */
package com.zogodo.myempty.cmd;

import com.zogodo.myempty.activity.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        String cmd = "tar -xjvf " + bz2_file_path + " -C " + MainActivity.sd_dic_path;

        String[] result = getCmdReadLine(cmd);

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

}
