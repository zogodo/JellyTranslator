package com.zogodo.myempty;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by zogod on 2016/7/25.
 */
public class StarDict
{
    public static class IndexItem
    {
        public String word;
        public int offset;
        public int length;
    }

    public static IndexItem[] GetAllIndexItems(String idx_file, int word_count) throws IOException
    {
        byte[] file_bytes = ReadFile.readFileByByte(idx_file);

        IndexItem[] index_items = new IndexItem[word_count];

        for (int i = 0, item = 0; item < word_count; item++)
        {
            index_items[item] = new IndexItem();
            byte[] word = new byte[48];
            byte[] offset_c = new byte[4];
            byte[] lenth_c = new byte[4];

            int j = 0;
            while (file_bytes[i] != 0)
            {
                word[j] = file_bytes[i];
                j++;
                i++;
            }
            i++;
            index_items[item].word = new String(word, StandardCharsets.UTF_8).trim();

            for (j = 0; j < 4; j++, i++)
            {
                offset_c[j] = file_bytes[i];
            }
            index_items[item].offset = to_int(offset_c);

            for (j = 0; j < 4; j++, i++)
            {
                lenth_c[j] = file_bytes[i];
            }
            index_items[item].length = to_int(lenth_c);
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

    public static int GetWordStart(String tran, IndexItem[] index_items)
    {
        int low = 0;
        int high = index_items.length - 1;

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if (middle == 0 ||
                tran.compareTo(index_items[middle].word) <= 0 &&
                tran.compareTo(index_items[middle - 1].word) > 0)
            {
                return middle;
            }
            if (tran.compareTo(index_items[middle].word) < 0)
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

    public static String GetMeaningOfWord(RandomAccessFile dic_file, IndexItem index_item) throws IOException
    {
        return ReadFile.readFileByOffset(
                dic_file,
                index_item.offset,
                index_item.length
        );
    }
}
