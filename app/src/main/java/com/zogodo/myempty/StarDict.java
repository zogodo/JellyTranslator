package com.zogodo.myempty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by zogod on 2016/7/25.
 */
public class StarDict
{
    StarDict(String idx_file_path, String dic_file_path, String info_file_path) throws IOException
    {
        String info_string[] = ReadFile.readFileByLines(info_file_path, 6);
        for (String word_count_info : info_string)
        {
            if (word_count_info.indexOf("wordcount=") == 0)
            {
                word_count_info = word_count_info.substring(10);
                this.word_count = Integer.parseInt(word_count_info);
            }
            //info_string[2] = info_string[2].substring(10);
        }

        File align_idx_file = new File(idx_file_path + "_align");
        if (align_idx_file.exists())
        {
            this.index_file_align = ReadFile.readFileByByte(idx_file_path + "_align");
        }
        else
        {
            this.index_file_align = this.GetAllIndexItems(idx_file_path);

            FileOutputStream fos = new FileOutputStream(idx_file_path + "_align");
            fos.write(this.index_file_align);
            fos.close();
        }

        this.dic_file = new RandomAccessFile(dic_file_path, "r");
    }

    public int word_count;
    public byte[] index_file_align;
    public RandomAccessFile dic_file;

    public byte[] GetAllIndexItems(String idx_file) throws IOException
    {
        byte[] file_bytes = ReadFile.readFileByByte(idx_file);

        byte[] index_file_align = new byte[this.word_count * 56];

        for (int i = 0, j = 0, w = 0; w < this.word_count; w++)
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

    public int to_int(byte[] bytes)
    {
        return   ((int) bytes[0] & 0x0FF) * 65536 * 256
                + ((int) bytes[1] & 0x0FF) * 65536
                + ((int) bytes[2] & 0x0FF) * 256
                + ((int) bytes[3] & 0x0FF);
    }

    public int GetWordStart(String tran)
    {
        int low = 0;
        int high = this.index_file_align.length/56 - 1;

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if (middle == 0)
            {
                return 0;
            }

            byte[] word_byte = new byte[48];
            System.arraycopy(this.index_file_align, middle*56, word_byte, 0, 48);
            byte[] word_befor_byte = new byte[48];
            System.arraycopy(this.index_file_align, middle*56 - 56, word_befor_byte, 0, 48);

            String word = new String(word_byte, StandardCharsets.UTF_8).trim();
            String word_befor = new String(word_befor_byte, StandardCharsets.UTF_8).trim();

            if (word_befor.indexOf(tran) == -1 && word.indexOf(tran) == 0)
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

    public String GetMeaningOfWord(int index) throws IOException
    {
        byte[] offset_byte = new byte[4];
        System.arraycopy(this.index_file_align, index + 48, offset_byte, 0, 4);
        byte[] length_byte = new byte[4];
        System.arraycopy(this.index_file_align, index + 52, length_byte, 0, 4);

        int offset = to_int(offset_byte);
        int length = to_int(length_byte);
        return ReadFile.readFileByOffset(this.dic_file, offset, length);
    }
}
