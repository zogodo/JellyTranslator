package com.zogodo.myempty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zogod on 2016/7/23.
 */
public class FileString {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        String path_now = System.getProperty("user.dir");
        String file_path = path_now + "\\oxfordjm-ec.txt";
        System.out.println(file_path);


        long Time1 = System.currentTimeMillis();
        String[] file_string = readFileByLines(file_path, 142367);
        long Time2 = System.currentTimeMillis();
        System.out.println(Time2 - Time1);


        long Time3 = System.currentTimeMillis();
        String tran = "look";
        String[] all = new String[]{
                "aa","aba","abab","abac",
                "ac","acd","acf","ad",
                "ada","adf","aff","aee",
                "af","afa","afb","afc"
        };
        int start = GetSimilarWordsStart(tran, file_string);
        String[] similar_word = new String[100];
        for (int i = 0; i < 100; i++)
        {
            similar_word[i] = file_string[start + i];
        }
        System.out.println(start);
        long Time4 = System.currentTimeMillis();
        System.out.println(Time4 - Time3);


        for (int i = 0; i < 100; i++)
        {
            System.out.println(similar_word[i]);
        }
    }

    /**
     * 以行为单位读取文件，返回字符串数组
     */
    public static String[] readFileByLines(String fileName, int fileLines) {
        File file = new File(fileName);
        BufferedReader reader = null;
        String[] file_string = new String[fileLines];
        try {
            reader = new BufferedReader(new FileReader(file));
            file_string[0] = new String();
            int i = 0;
            while (i < fileLines) {
                file_string[i++] = new String();
                file_string[i - 1] = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return file_string;
    }

    public static int GetSimilarWordsStart(String tran, String[] fileLines)
    {
        int low=0;
        int high=fileLines.length - 1;

        while(low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if(tran.compareTo(fileLines[middle]) <= 0
                    && tran.compareTo(fileLines[middle - 1]) > 0)
            {
                return middle;
            }
            if(tran.compareTo(fileLines[middle]) < 0)
            {
                high=middle-1;
            }
            else
            {
                low=middle+1;
            }
        }
        return -1;
    }

    /*
     * 非递归二分查找算法
     * 参数:整型数组,需要比较的数.
     */
    public static int binarySearch(Integer[]srcArray,int des)
    {
        int low=0;
        int high=srcArray.length-1;

        while(low<=high)
        {
            int middle=low+((high-low)>>1);

            if(des==srcArray[middle])
            {
                return middle;
            }
            else if(des<srcArray[middle])
            {
                high=middle-1;
            }
            else
            {
                low=middle+1;
            }
        }
        return-1;
    }

}
