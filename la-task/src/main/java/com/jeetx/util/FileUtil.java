package com.jeetx.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

public class FileUtil {
    //读取远程url图片
    public static File getURLImg(String imgUrl,String saveFilePath) {
    	File file = null;
        try {
            //实例化url
            URL url = new URL(imgUrl);
            //载入图片到输入流
            java.io.BufferedInputStream bis = new BufferedInputStream(url.openStream());
            //实例化存储字节数组
            byte[] bytes = new byte[100];
            //设置写入路径以及图片名称
            OutputStream bos = new FileOutputStream(new File(saveFilePath));
            int len;
            while ((len = bis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            bis.close();
            bos.flush();
            bos.close();
            //关闭输出流
            file = new File(saveFilePath);
            
            return file;
        } catch (Exception e) {
            //如果图片未找到
        	e.printStackTrace();
        }
        return file;
    }
}
