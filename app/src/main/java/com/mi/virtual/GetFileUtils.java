package com.mi.virtual;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/11/11.
 */
public class GetFileUtils {
    private File file;
    private FileOutputStream stream = null;

    public static final String apk_wx = "wx.apk_";


    public static String getWxDir() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        try {
            String external_storage = System.getenv("EXTERNAL_STORAGE");
            File fileExternal = new File(external_storage);
            if (!fileExternal.exists()) {
                external_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            String pathName = external_storage + "/.wx/";
            File fFile = new File(pathName);
            if (!fFile.exists()) {
                if (fFile.mkdir()) {
                    makeNoMediaFile(fFile);
                } else {
                    return null;
                }
            } else {
                makeNoMediaFile(fFile);
            }
            return pathName;
        } catch (Exception e) {
            return null;
        }
    }

    private static void makeNoMediaFile(File fFile) {
        if (fFile == null) {
            return;
        }
        File fNoMediaFile = new File(fFile, ".nomedia");
        if (!fNoMediaFile.exists()) {
            try {
                fNoMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createDirLoop(String strPath) {
        File fDir = new File(strPath == null ? "" : strPath);
        if (!fDir.exists())
            fDir.mkdirs();
    }

    public static String readFile(String strFilePath) {
        File fFile = new File(strFilePath == null ? "" : strFilePath);
        if (!fFile.exists() || !fFile.isFile())
            return "";

        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(fFile);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            String result = new String(b);
            return result;
        } catch (Exception e) {
        }
        return "";
    }

    //  /wx/ 目录下创建文件夹 strDirName/strFileName
    public File createDetectFile(String strDirName, String strFileName) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        try {
            String pathName = getWxDir();
            File path = new File(pathName + strDirName);
            if (!path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if (path != null) {
                file = new File(path, strFileName);
            }
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFileName);
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean writeString(String strData) throws IOException {
        if (strData == null)
            return false;
        if (file == null)
            return false;
        if (stream == null)
            stream = new FileOutputStream(file, true);
        byte[] buf = strData.getBytes();
        stream.write(buf);
        stream.flush();
//        stream.close();      //7.0 第二次write时出现Closed异常,导致不能写入,使用writeString后需调用closeFile();
        return true;
    }

    public void closeFile() {
        try {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        } catch (Exception e) {

        }
    }

    //删除数据，
    public static void deleteFile(String strPath) {
        try {
            File fPath = new File(strPath == null ? "" : strPath);
            if (fPath.exists()) { // 判断文件是否存在
                if (fPath.isFile()) { // 判断是否是文件
                    fPath.delete(); // delete()方法 你应该知道 是删除的意思;
                } else if (fPath.isDirectory()) { // 否则如果它是一个目录
                    File files[] = fPath.listFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFile(files[i].getAbsolutePath()); // 把每个文件 用这个方法进行迭代
                    }
                }
                fPath.delete();
            } else {
            }
        } catch (Exception e) {
        }
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(fileName == null ? "" : fileName);
        return file.exists();
    }

    public static void createDir(String fileName) {
        File file = new File(fileName == null ? "" : fileName);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public File createDetectFile(String fileName) throws IOException {
        File file = new File(fileName == null ? "" : fileName);
        file.createNewFile();
        return file;
    }

    public File write2SDFromInput(String path, InputStream inputstream) {
        File file = null;
        OutputStream output = null;
        try {
            file = createDetectFile(path);
            output = new FileOutputStream(file);
            // 4k为单位，每4K写一次
            byte buffer[] = new byte[4 * 1024];
            int temp = 0;
            while ((temp = inputstream.read(buffer)) != -1) {
                // 获取指定信,防止写入没用的信息
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    //复制文件
    public boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath == null ? "" : oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
        return false;
    }

}
