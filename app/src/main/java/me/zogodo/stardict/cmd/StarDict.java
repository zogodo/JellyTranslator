package me.zogodo.stardict.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by zogod on 2016/7/25.
 */

public class StarDict
{
    public int wordcount;           // 单词数量
    public int idxfilesize;         // 索引文件大小 (byte)
    public int dicfilesize;         // 字典内容文件大小
    public String version;          // 版本
    public String bookname;         // 字典名
    public String author;           // 作者
    public String email;            // 作者 email
    public String description;      // 描述
    public String date;             // 制作日期
    public String sametypesequence; // 未知属性
    public String dic_name;         // 字典文件名
    public String dic_path;         // 字典文件所在文件夹路径
    public String idx_file_path;    // 字典索引文件路径，后缀 .idx
    public String dic_file_path;    // 字典内容文件路径，后缀 .dict
    public String info_file_path;   // 字典信息文件路径，后缀 .ifo
    public byte[] index_file_align; // 字典索引文件对齐后所得字节流
    public RandomAccessFile dic_file;           // 字典文件流
    public static final int word_width = 48;    // 一个单词的宽度
    public static final int index_width = 56;   // 一个索引的宽度

    public StarDict(String dic_path, String dic_name) throws IOException
    {
        /* 构造字典
         * 参数：
         * dic_path：三个字典文件所在文件夹路径，三个文件必须在同一文件夹
         * dic_name：字典文件名，三个文件除了后缀，必须同同名
         */
        this.dic_path = dic_path;
        this.dic_name = dic_name;
        this.info_file_path = dic_path + dic_name + ".ifo";
        this.idx_file_path = dic_path + dic_name + ".idx";
        this.dic_file_path = dic_path + dic_name + ".dict";
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
        File align_idx_file = new File(this.idx_file_path + "_align");
        if (align_idx_file.exists())
        {
            this.index_file_align = ReadFile.readFileByByte(this.idx_file_path + "_align");
        }
        else
        {
            this.index_file_align = this.GetAllIndexItems(this.idx_file_path);

            FileOutputStream fos = new FileOutputStream(this.idx_file_path + "_align");
            fos.write(this.index_file_align);
            fos.close();
        }

        //字典内容
        this.dic_file = new RandomAccessFile(this.dic_file_path, "rw");
        this.dicfilesize = (int)dic_file.length();
    }

    public byte[] GetAllIndexItems(String idx_file) throws IOException
    {
        // 从字典索引文件获取对齐的字节流
        byte[] file_bytes = ReadFile.readFileByByte(idx_file);
        byte[] index_file_align = new byte[this.wordcount * index_width];

        for (int i = 0, j = 0, w = 0; w < this.wordcount && i < file_bytes.length; w++)
        {
            while (file_bytes[i] != 0 && j%index_width < word_width)
            {
                //记录单词
                index_file_align[j] = file_bytes[i];
                j++;
                i++;
            }
            j = j + (word_width - j%index_width);
            while(file_bytes[i] != 0)
            {
                i++;
            }
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
        // 将四个字节转换成一个整数
        return  ((int) bytes[0] & 0x0FF) * 65536 * 256
                + ((int) bytes[1] & 0x0FF) * 65536
                + ((int) bytes[2] & 0x0FF) * 256
                + ((int) bytes[3] & 0x0FF);
    }

    public byte[] byte_add_int(byte[] bytes, int number)
    {
        // 将一个整数转换成四个字节
        int temp_number = to_int(bytes);
        bytes = ByteBuffer.allocate(4).putInt(number + temp_number).array();
        return bytes;
    }

    public int GetWordStart(String tran)
    {
        // 二分法查找索引文件，返回单词所在索引字节流中的字节序号，没有则返回 -1
        // 参数是要翻译的单词

        tran = tran.toLowerCase();

        int low = 0;
        int high = this.index_file_align.length/index_width - 1;
        StarDictWord word1 = new StarDictWord();
        StarDictWord word_befor = new StarDictWord();

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if (middle == 0)
            {
                return 0;
            }

            System.arraycopy(this.index_file_align, middle*index_width, word1.word_byte, 0, word_width);
            System.arraycopy(this.index_file_align, middle*index_width - index_width, word_befor.word_byte, 0, word_width);

            word1.word = new String(word1.word_byte, StandardCharsets.UTF_8).toLowerCase();
            word_befor.word = new String(word_befor.word_byte, StandardCharsets.UTF_8).toLowerCase();

            if ((word_befor.word).indexOf(tran) != 0 && (word1.word).indexOf(tran) == 0)
            {
                return middle*index_width;
            }
            if (tran.compareTo(word1.word) < 0)
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
        // 获取单词意思，参数 index 是单词所在索引字节流中的字节序号
        StarDictWord word = new StarDictWord();
        word.index = index;
        System.arraycopy(this.index_file_align, index + word_width, word.meaning_offset_byte, 0, 4);
        System.arraycopy(this.index_file_align, index + (word_width + 4), word.meaning_length_byte, 0, 4);

        word.meaning_offset = to_int(word.meaning_offset_byte);
        word.meaning_length = to_int(word.meaning_length_byte);
        if (word.meaning_offset < 0 || word.meaning_offset >= dicfilesize)
        {
            return "outofsizeerror";
        }
        return ReadFile.readFileByOffset(this.dic_file, word.meaning_offset, word.meaning_length);
    }

    public void PrintfromTran(String tran, int print_count) throws IOException
    {
        StarDictWord word = new StarDictWord();
        word.start = this.GetWordStart(tran);
        //word.start = 0;
        if (word.start == -1)
        {
            System.out.println("//------------not found-------------//");
            return;
        }
        for (int i = 0; i < print_count && i < this.wordcount;
             i++, word.start = word.start + StarDict.index_width)
        {
            System.arraycopy(this.index_file_align, word.start, word.word_byte, 0, 48);
            word.word = new String(word.word_byte, StandardCharsets.UTF_8);
            word.meaning = this.GetMeaningOfWord(word.start);

            System.out.println(word.start/56+1 + "\t" + word.word + "|" + word.meaning.replaceAll("\n", " "));
        }
    }

    public void PrintDicToTxt(String txt_file_path) throws IOException
    {
        StarDictWord word = new StarDictWord();
        word.start = 0;
        String one_word;
        for (int i = 0; i < this.wordcount;
             i++, word.start = word.start + StarDict.index_width)
        {
            System.arraycopy(this.index_file_align, word.start, word.word_byte, 0, 48);
            word.word = new String(word.word_byte, StandardCharsets.UTF_8);
            word.meaning = this.GetMeaningOfWord(word.start);

            one_word = word.word.trim() + "\t" + word.meaning.replaceAll("\n", " ") + "\r\n";
            if(i%5000 == 0 || i == this.wordcount)
            {
                System.out.println(word.start/56);
            }
            ReadFile.WriteStringToFile(txt_file_path, one_word);
        }
    }
}
