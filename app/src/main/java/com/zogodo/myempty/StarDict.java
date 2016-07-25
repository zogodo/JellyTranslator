package com.zogodo.myempty;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by zogod on 2016/7/25.
 */
public class StarDict
{
    public static byte[][][] GetAllIndexItems(String idx_file, int word_count) throws IOException
    {
        byte[] file_bytes = ReadFile.readFileByByte(idx_file);

        byte[][][] index_items = new byte[word_count][3][];

        for (int i = 0, item = 0; item < word_count; item++)
        {
            index_items[item] = new byte[3][];
            index_items[item][0] = new byte[48];
            index_items[item][1] = new byte[4];
            index_items[item][2] = new byte[4];

            int j = 0;
            while (file_bytes[i] != 0)
            {
                index_items[item][0][j] = file_bytes[i];
                j++;
                i++;
            }
            i++;

            for (j = 0; j < 4; j++, i++)
            {
                index_items[item][1][j] = file_bytes[i];
            }

            for (j = 0; j < 4; j++, i++)
            {
                index_items[item][2][j] = file_bytes[i];
            }
        }
        return index_items;
    }

    public static int to_int(byte[] bytes)
    {
        return   ((int) bytes[0] & 0x0FF) * 65536 * 256
                + ((int) bytes[1] & 0x0FF) * 65536
                + ((int) bytes[2] & 0x0FF) * 256
                + ((int) bytes[3] & 0x0FF);
    }

    public static int GetWordStart(String tran, byte[][][] index_items)
    {
        int low = 0;
        int high = index_items.length - 1;

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if (middle == 0)
            {
                return middle;
            }
            String word = new String(index_items[middle][0], StandardCharsets.UTF_8).trim();
            String word_befor = new String(index_items[middle - 1][0], StandardCharsets.UTF_8).trim();
            if (tran.compareTo(word) <= 0 && tran.compareTo(word_befor) > 0)
            {
                return middle;
            }
            if (tran.compareTo(word) < 0)
            {
                high = middle - 1;
            }
            else
            {
                low = middle + 1;
            }
        }
        return -1;
    }

    public static String GetMeaningOfWord(RandomAccessFile dic_file, byte[][] index_item) throws IOException
    {
        int offset = to_int(index_item[1]);
        int length = to_int(index_item[2]);
        return ReadFile.readFileByOffset(dic_file, offset, length);
    }
}
