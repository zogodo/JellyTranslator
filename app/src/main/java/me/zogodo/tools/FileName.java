package me.zogodo.tools;

/**
 * Created by zogod on 17/11/12.
 */
public class FileName
{
    public static String removeExtension(String file_name)
    {
        if (file_name == null)
        {
            return null;
        }

        int pos = file_name.lastIndexOf(".");
        if (pos == -1)
        {
            return file_name;
        }
        return file_name.substring(0, pos);
    }

    public static String getDir(String file_path)
    {
        if (file_path == null)
        {
            return null;
        }

        int pos = file_path.lastIndexOf("/");
        if (pos == -1)
        {
            return file_path;
        }
        return file_path.substring(0, pos);
    }
}
