package com.jeetx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;

import org.apache.commons.lang.StringUtils;

public class TxtUtil4 {

    public static void readFileByLines(String filePath) {
    	String fn = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            int num = 1;
        	String bm = null;
        	String dm = null;
        	String lb = null;
        	String qy = null;
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
//            	if(tempString.contains("地名代码")) {
//            		tempString = tempString.replaceAll("\\s*", "");;
//            		//System.out.println("line " + line + ": " + tempString);
//            		bm = tempString.substring(tempString.indexOf("地名代码")+4, tempString.indexOf("类别名称"));
//
//            		lb = tempString.substring(tempString.indexOf("类别名称")+4);
//            	}else if(tempString.contains("汉 字")) {
//            		tempString = tempString.replaceAll("\\s*", "");
//            		//System.out.println("line " + line + ": " + tempString);
//            		dm = tempString.substring(tempString.indexOf("汉字")+2);
//            	}else if(tempString.contains("政区 ")) {
//            		//System.out.println(tempString);
//            		tempString = tempString.replaceAll("\\s*", "");
//            		//System.out.println("line " + line + ": " + tempString);
//            		qy = tempString.substring(tempString.indexOf("政区")+2);
//            	}
            	
            	if(tempString.contains("标志代码")) {
            		tempString = tempString.replaceAll("\\s*", "");;
            		//System.out.println("line " + line + ": " + tempString);
            		bm = tempString.substring(tempString.indexOf("标志代码")+4, tempString.indexOf("标准地名"));

            		lb = tempString.substring(tempString.indexOf("标准地名")+4);
            	}
            	
            	if(StringUtils.isNotBlank(bm) && StringUtils.isNotBlank(lb)) {
            		//System.out.println("num " + num + ": " + bm + "-" + dm + "-" + lb + "-" + qy);
                	StringBuffer sb = new StringBuffer();
                	sb.append("INSERT INTO test (fn,bm,lb,dm,qy) VALUES ('").append(fn).append("','").append(bm).append("','").append(dm).append("','").append(lb).append("','").append(qy).append("');");
                	//System.out.println(sb);
                	TxtUtil4.appendMethodA("D:/123.txt", sb.toString());
                	TxtUtil4.appendMethodA("D:/123.txt", "\n");
                	bm = null;
                	dm = null;
                	lb = null;
                	qy = null;
                	num ++;
            	}
                line++;
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
    }

    /**
     * A方法追加文件：使用RandomAccessFile
     */
    public static void appendMethodA(String filePath, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(filePath, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes( new String(content.getBytes("utf-8"),"ISO-8859-1"));
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public static void readAppointedLineNumber(File sourceFile, int lineNumber) throws IOException {
		FileReader in = new FileReader(sourceFile);
		LineNumberReader reader = new LineNumberReader(in);
		String s = reader.readLine();

		if (lineNumber < 0 || lineNumber > getTotalLines(sourceFile)) {
			System.out.println("不在文件的行数范围之内。");
		}

		{
			while (s != null) {
				System.out.println("当前行号为:" + reader.getLineNumber());

				System.out.println(s);
				System.exit(0);
				s = reader.readLine();
			}
		}
		reader.close();
		in.close();
	}
    
    // 文件内容的总行数。 
    public static int getTotalLines(File file) throws IOException {
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        int lines = 0;
        while (s != null) {
            lines++;
            s = reader.readLine();
        }
        reader.close();
        in.close();
        return lines;
    }

    public static void main(String[] args) throws IOException {
    	String path="D:/123";
    	File file=new File(path);
    	File[] tempList = file.listFiles();
    	System.out.println("目录下文件数："+tempList.length);
    	for (int i = 0; i < tempList.length; i++) {
    		if (tempList[i].isFile()) {
    			String tempString = tempList[i].getName();
    			String fileName = path.concat("/").concat(tempString);
    			System.out.println("开始处理文件："+fileName);
    			TxtUtil4.readFileByLines(fileName);
    		}
    	}
    }

}
