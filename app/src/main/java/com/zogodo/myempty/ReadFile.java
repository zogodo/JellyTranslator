package com.zogodo.myempty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
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
        }
        reader.close();
        return file_string;
    }
}
