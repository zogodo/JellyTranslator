/**
 * Created by zogod on 2016/11/27.
 */
package com.zogodo.myempty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LinuxCmd
{
    public static void exportBz2FIle(String bz2_file_path) throws IOException
    {
        //解压 .tar.bz2 文件
        String dic_path = "/mnt/sdcard/Android/data/com.zogodo.jelly/files/dict/";
        String cmd = "tar -xjvf " + bz2_file_path + " -C " + dic_path;
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(cmd); //执行命令

        //删除压缩包
        cmd = "rm " + bz2_file_path;
        runtime.exec(cmd);
    }

    public static String[] getCmdReadLine(String cmd) throws IOException
    {
        Runtime runtime = Runtime.getRuntime();
        //执行命令，并且获得Process对象
        Process process = runtime.exec(cmd);
        //获得结果的输入流
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
