package com.zogodo.myempty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zogod on 2016/7/23.
 */
public class FileString
{
    /**
     * 以行为单位读取文件，返回字符串数组
     */
    public static String[] readFileByLines(String fileName, int fileLines)
    {
        File file = new File(fileName);
        BufferedReader reader = null;
        String[] file_string = new String[fileLines];
        try
        {
            reader = new BufferedReader(new FileReader(file));
            file_string[0] = new String();
            int i = 0;
            while (i < fileLines)
            {
                file_string[i++] = new String();
                file_string[i - 1] = reader.readLine();
            }
            reader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException e1)
                {
                }
            }
        }
        return file_string;
    }

    public static int GetSimilarWordsStart(String tran, String[] fileLines)
    {
        if (tran.length() == 0)
            return -1;

        int low = 0;
        int high = fileLines.length - 1;

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);
            if (middle == 0 || tran.compareTo(fileLines[middle]) <= 0 && tran.compareTo(fileLines[middle - 1]) > 0)
            {
                return middle;
            }
            if (tran.compareTo(fileLines[middle]) < 0)
            {
                high = middle - 1;
            } else
            {
                low = middle + 1;
            }
        }
        return -1;
    }

    /*
     * 非递归二分查找算法
     * 参数:整型数组,需要比较的数.
     */
    public static int binarySearch(Integer[] srcArray, int des)
    {
        int low = 0;
        int high = srcArray.length - 1;

        while (low <= high)
        {
            int middle = low + ((high - low) >> 1);

            if (des == srcArray[middle])
            {
                return middle;
            } else if (des < srcArray[middle])
            {
                high = middle - 1;
            } else
            {
                low = middle + 1;
            }
        }
        return -1;
    }

}
