package com.zogodo.myempty;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by zogod on 2016/7/25.
 */
public class ReadFile
{
    public static byte[] readFileByByte(String fileName) throws IOException
    {
        File file = new File(fileName);
        RandomAccessFile randomFile = new RandomAccessFile(fileName, "r");
        byte[] file_bytes = new byte[(int)file.length()];
        randomFile.read(file_bytes, 0, (int)file.length());
        return file_bytes;
    }

    public static byte[] readFileByByte(RandomAccessFile randomFile) throws IOException
    {
        byte[] file_bytes = new byte[(int)randomFile.length()];
        randomFile.read(file_bytes, 0, (int)randomFile.length());
        return file_bytes;
    }

    public static String readFileByOffset(RandomAccessFile randomFile, int offset, int lenght) throws IOException
    {
        randomFile.seek(offset);
        byte[] bytes1 = new byte[lenght];
        randomFile.read(bytes1, 0, lenght);
        String str1 = new String(bytes1, StandardCharsets.UTF_8);
        return str1;
    }

    public static String[] readFileByLines(String fileName, int fileLines) throws IOException
    {
        File file = new File(fileName);
        BufferedReader reader = null;
        String[] file_string = new String[fileLines];
        reader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < fileLines; i++)
        {
            file_string[i] = new String();
            file_string[i] = reader.readLine();
            if (file_string[i] == null)
            {
                break;
            }
        }
        reader.close();
        return file_string;
    }

    public int getFileLines(InputStream stream) throws IOException
    {
        //获取文件行数
        byte[] c = new byte[1024];
        int count = 1;
        int readChars = 0;
        while ((readChars = stream.read(c)) != -1)
        {
            for (int i = 0; i < readChars; ++i)
            {
                if (c[i] == '\n')
                {
                    ++count;
                }
            }
        }
        return count;
    }

    public static void AppendToEnd(RandomAccessFile append_to, RandomAccessFile append_from) throws IOException
    {
        //文件长度，字节数
        long fileLength = append_to.length();
        //将写文件指针移到文件尾。
        append_to.seek(fileLength);
        byte[] content = readFileByByte(append_from);
        append_to.write(content);
    }

    public static void UpdateFile(String file_name, byte[] content) throws IOException
    {
        RandomAccessFile randomFile = new RandomAccessFile(file_name, "rw");
        randomFile.write(content);
    }

    public static void WriteStringToFile(String txt_file_path, String content) throws IOException
    {
        FileWriter fw = new FileWriter(txt_file_path, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.append(content);// 往已有的文件上添加字符串
        bw.close();
        fw.close();
    }
}
