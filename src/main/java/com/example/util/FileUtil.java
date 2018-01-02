package com.example.util;

import java.io.*;

/**
 * Created by chenhaorj on 2017/11/14.
 */
public class FileUtil {
    public static byte[] getBytes(String filePath) throws IOException {
        byte[] buffer = null;
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1000];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        buffer = bos.toByteArray();
        return buffer;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath, String fileName) throws IOException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        File dir = new File(filePath);
        if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
            dir.mkdirs();
        }
        file = new File(filePath + "\\" + fileName);
        fos = new FileOutputStream(file);
        bos = new BufferedOutputStream(fos);
        bos.write(bfile);
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
