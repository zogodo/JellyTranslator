package me.zogodo.stardict.cmd;

/**
 * Created by zogod on 17/5/5.
 */
public class StarDictWord
{
    public int index;
    public int start;
    public int meaning_offset;
    public int meaning_length;
    public String word;
    public String meaning;
    public byte[] meaning_offset_byte = new byte[4];
    public byte[] meaning_length_byte = new byte[4];
    public byte[] word_byte = new byte[StarDict.word_width];
}
