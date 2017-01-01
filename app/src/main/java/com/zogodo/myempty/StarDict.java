package com.zogodo.myempty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by zogod on 2016/7/25.
 */
public class StarDict
{
    public int wordcount;
    public int idxfilesize;
    public String version;
    public String bookname;
    public String author;
    public String email;
    public String description;
    public String date;
    public String sametypesequence;
    public String idx_file_path;
    public String dic_file_path;
    public String info_file_path;
    public byte[] index_file_align;
    public RandomAccessFile dic_file;
    private static final int word_witdh = 48;

    StarDict(String idx_file_path, String dic_file_path, String info_file_path) throws IOException
    {
        this.info_file_path = info_file_path;
        this.idx_file_path = idx_file_path;
        this.dic_file_path = dic_file_path;
        String[] info_string = ReadFile.readFileByLines(info_file_path, 15);
        for (String info_item : info_string)
        {
            //获取字典信息
            if (info_item == null)
            {
                break;
            }
            else if (info_item.indexOf("wordcount") == 0)
            {
                info_item = info_item.split("=")[1];
                this.wordcount = Integer.parseInt(info_item);
            }
            else if (info_item.indexOf("idxfilesize") == 0)
            {
                info_item = info_item.split("=")[1];
                this.idxfilesize = Integer.parseInt(info_item);
            }
            else if (info_item.indexOf("version") == 0)
            {
                info_item = info_item.split("=")[1];
                this.version = info_item;
            }
            else if (info_item.indexOf("bookname") == 0)
            {
                info_item = info_item.split("=")[1];
                this.bookname = info_item;
            }
            else if (info_item.indexOf("author") == 0)
            {
                info_item = info_item.split("=")[1];
                this.author = info_item;
            }
            else if (info_item.indexOf("email") == 0)
            {
                info_item = info_item.split("=")[1];
                this.email = info_item;
            }
            else if (info_item.indexOf("description") == 0)
            {
                info_item = info_item.split("=")[1];
                this.description = info_item;
            }
            else if (info_item.indexOf("date") == 0)
            {
                info_item = info_item.split("=")[1];
                this.date = info_item;
            }
            else if (info_item.indexOf("sametypesequence") == 0)
            {
                info_item = info_item.split("=")[1];
                this.sametypesequence = info_item;
            }
        }

        //对齐索引文件
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

        //字典内容
        this.dic_file = new RandomAccessFile(dic_file_path, "rw");
    }

    public byte[] GetAllIndexItems(String idx_file) throws IOException
    {
        byte[] file_bytes = ReadFile.readFileByByte(idx_file);
        byte[] index_file_align = new byte[this.wordcount * (word_witdh + 8)];

        for (int i = 0, j = 0, w = 0; w < this.wordcount; w++)
        {
            while (file_bytes[i] != 0 && j%(word_witdh + 8) < word_witdh)
            {
                //记录单词
                index_file_align[j] = file_bytes[i];
                j++;
                i++;
            }
            j = j + (word_witdh - j%(word_witdh + 8));
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
        return  ((int) bytes[0] & 0x0FF) * 65536 * 256
              + ((int) bytes[1] & 0x0FF) * 65536
              + ((int) bytes[2] & 0x0FF) * 256
              + ((int) bytes[3] & 0x0FF);
    }

    public int GetWordStart(String tran)
    {
        //二分法查找索引文件

        tran = tran.toLowerCase();

        int low = 0;
        int high = this.index_file_align.length/(word_witdh + 8) - 1;

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if (middle == 0)
            {
                return 0;
            }

            byte[] word_byte = new byte[word_witdh];
            System.arraycopy(this.index_file_align, middle*(word_witdh + 8), word_byte, 0, word_witdh);
            byte[] word_befor_byte = new byte[word_witdh];
            System.arraycopy(this.index_file_align, middle*(word_witdh + 8) - (word_witdh + 8), word_befor_byte, 0, word_witdh);

            String word = new String(word_byte, StandardCharsets.UTF_8).toLowerCase();
            String word_befor = new String(word_befor_byte, StandardCharsets.UTF_8).toLowerCase();

            if ((word_befor).indexOf(tran) != 0 && (word).indexOf(tran) == 0)
            {
                return middle*(word_witdh + 8);
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
        System.arraycopy(this.index_file_align, index + word_witdh, offset_byte, 0, 4);
        byte[] length_byte = new byte[4];
        System.arraycopy(this.index_file_align, index + (word_witdh + 4), length_byte, 0, 4);

        int offset = to_int(offset_byte);
        int length = to_int(length_byte);
        return ReadFile.readFileByOffset(this.dic_file, offset, length);
    }

    public byte[] MergeIndex(byte[] index_file_align)
    {
        int total_index_size = this.index_file_align.length + index_file_align.length;
        byte[] merge_index = new byte[total_index_size];
        int i = 0, j = 0, k = 0;
        byte[] word_byte1 = new byte[word_witdh];
        byte[] word_byte2 = new byte[word_witdh];
        String word1, word2;
        while(i < this.index_file_align.length && j < index_file_align.length)
        {
            System.arraycopy(this.index_file_align, i, word_byte1, 0, word_witdh);
            System.arraycopy(index_file_align, i, word_byte2, 0, word_witdh);
            word1 = new String(word_byte1, StandardCharsets.UTF_8).toLowerCase();
            word2 = new String(word_byte2, StandardCharsets.UTF_8).toLowerCase();
            if (word1.compareTo(word2) < 0)
            {
                for (int ii = 0; ii < (word_witdh + 8); ii++)
                {
                    merge_index[i++] = word_byte1[i++];
                }
            }
            else
            {
                for (int ii = 0; ii < (word_witdh + 8); ii++)
                {
                    merge_index[i++] = word_byte2[j++];
                }
            }
        }
        return merge_index;
    }

    public void AddDic(StarDict add_from) throws IOException
    {
        //合并信息文件
        File file = new File(info_file_path);
        PrintStream ps = new PrintStream(new FileOutputStream(file));
        ps.println("StarDict's dict ifo file");
        ps.println("version=" + version + "|" + add_from.version);
        ps.println("wordcount=" + (wordcount + add_from.wordcount));
        ps.println("idxfilesize=" + (idxfilesize + add_from.idxfilesize));
        ps.println("bookname=" + bookname + "|" + add_from.bookname);
        ps.println("author=" + author + "|" + add_from.author);
        ps.println("email=" + email + "|" + add_from.email);
        ps.println("description=" + description + "|" + add_from.description);
        ps.println("date=" + date + "|" + add_from.date);
        ps.println("sametypesequence=" + sametypesequence + "|" + add_from.sametypesequence);
        ps.close();

        //合并索引
        index_file_align = MergeIndex(add_from.index_file_align);

        //合并内容文件
        ReadFile.AppendToEnd(dic_file, add_from.dic_file);
        //dic_file.close();
    }
}
