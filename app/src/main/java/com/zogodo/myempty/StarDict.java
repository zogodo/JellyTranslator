package com.zogodo.myempty;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by zogod on 2016/7/25.
 */
public class StarDict
{
    public static byte[] GetAllIndexItems(String idx_file, int word_count) throws IOException
    {
        byte[] file_bytes = ReadFile.readFileByByte(idx_file);

        byte[] index_file_align = new byte[word_count * 56];

        for (int i = 0, j = 0, w = 0; w < word_count; w++)
        {
            while (file_bytes[i] != 0 && j%56 < 48)
            {
                //记录单词
                index_file_align[j] = file_bytes[i];
                j++;
                i++;
            }
            j = j + (48 - j%56);
            i++;

            for (int k = 0; k < 8; k++, j++, i++)
            {
                //记录索引和长度
                index_file_align[j] = file_bytes[i];
            }
        }
        System.gc();
        return index_file_align;
    }

    public static int to_int(byte[] bytes)
    {
        return   ((int) bytes[0] & 0x0FF) * 65536 * 256
                + ((int) bytes[1] & 0x0FF) * 65536
                + ((int) bytes[2] & 0x0FF) * 256
                + ((int) bytes[3] & 0x0FF);
    }

    public static int GetWordStart(String tran, byte[] index_file_align)
    {
        int low = 0;
        int high = index_file_align.length/56 - 1;

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if (middle == 0)
            {
                return 0;
            }

            byte[] word_byte = new byte[48];
            System.arraycopy(index_file_align, middle*56, word_byte, 0, 48);
            byte[] word_befor_byte = new byte[48];
            System.arraycopy(index_file_align, middle*56 - 56, word_befor_byte, 0, 48);

            String word = new String(word_byte, StandardCharsets.UTF_8).trim();
            String word_befor = new String(word_befor_byte, StandardCharsets.UTF_8).trim();

            if (tran.compareTo(word) == 0
                || (tran.compareTo(word_befor) > 0 && (tran).compareTo(word) < 0 && (tran + "|").compareTo(word) > 0))
            {
                return middle*56;
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

    public static String GetMeaningOfWord(byte[] index_file_align, RandomAccessFile dic_file, int index) throws IOException
    {
        byte[] offset_byte = new byte[4];
        System.arraycopy(index_file_align, index + 48, offset_byte, 0, 4);
        byte[] length_byte = new byte[4];
        System.arraycopy(index_file_align, index + 52, length_byte, 0, 4);

        int offset = to_int(offset_byte);
        int length = to_int(length_byte);
        return ReadFile.readFileByOffset(dic_file, offset, length);
    }
}
