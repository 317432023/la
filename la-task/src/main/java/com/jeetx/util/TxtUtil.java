package com.jeetx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;

public class TxtUtil {

	/**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static void readFileByBytes(String fileName) {
        File file = new File(fileName);
        InputStream in = null;
        try {
            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
            // 一次读一个字节
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                System.out.write(tempbyte);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            TxtUtil.showAvailableBytes(in);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                System.out.write(tempbytes, 0, byteread);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
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
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                //System.out.println("line " + line + ": " + tempString);
                //System.out.println("line " + line + ": " + new String(tempString.getBytes("GBK"),"UTF-8"));
            	tempString = tempString.replace(" ", "");
            	StringBuffer sb = new StringBuffer();
            	sb.append("INSERT INTO TB_K6_MAP (ID, SEQ,TYPE,CONTENT) VALUES (").append(line).append(",").append(line).append(",2,'");
            	
            	int i = tempString.indexOf("新");
            	String first = tempString.substring(i+2, i+9);
            	if(first.contains("。")){
            		continue;
            	}
            	sb.append(first).append(";");
            	int j = tempString.indexOf("老");
            	String second = tempString.substring(j+2, j+9);
            	if(second.contains("。")){
            		continue;
            	}
            	sb.append(tempString.substring(j+2, j+9)).append(".");
            	
            	sb.append("');");
            	System.out.println(sb);
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
     * 随机读取文件内容
     */
    public static void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 显示输入流中还剩的字节数
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 以行为单位读取文件，根据KEY值查找对应的value值
     */
    public static String getValueByKey(String fileName,String key) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            //reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	//System.out.println(tempString);
            	if(StringUtils.isNotBlank(tempString) && tempString.contains("=")){
            		if(key.equalsIgnoreCase(tempString.split("=")[0])){
            			//System.out.println(tempString.split("=")[0]);
            			//System.out.println(tempString.split("=")[1]);
            			return tempString.split("=")[1];
            		}
            	}
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
        return null;
    }
    
    /**
     * A方法追加文件：使用RandomAccessFile
     */
    public static void appendMethodA(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * B方法追加文件：使用FileWriter
     */
    public static void appendMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
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
//    	File directory = new File("");//参数为空 
//    	String courseFile = directory.getCanonicalPath() ; 
//    	System.out.println(courseFile); 
        //String fileName = System.getProperty("user.dir").concat("\\src\\station.txt");
        //String fileName = TxtUtil.class.getRealyPath("")+"\\station.txt";
        //System.out.println(TxtUtil.getValueByKey("D:\\MyOffice\\apache-tomcat-6.0.33\\webapps\\lljf\\station.txt","北京"));
    	//TxtUtil.readFileByLines("D:/MyOffice/Workspaces/k6c/WebRoot/treasureMap/test.txt");
    	

//    	String path="D:/MyOffice/Workspaces/k6c/WebRoot/treasureMap";
//    	File file=new File(path);
//    	File[] tempList = file.listFiles();
//    	System.out.println("该目录下对象个数："+tempList.length);
//    	for (int i = 0; i < tempList.length; i++) {
//    		if (tempList[i].isFile()) {
//    			String tempString = tempList[i].getName();
//            	StringBuffer sb = new StringBuffer();
//            	sb.append("INSERT INTO TB_K6_MAP (ID, SEQ,TYPE,CONTENT) VALUES (").append(i+1127).append(",").append(i).append(",1,'");
//            	sb.append(tempString);
//            	sb.append("');");
//            	if(tempString.contains(".txt")){
//            		continue;
//            	}
//            	System.out.println(sb);
//    		    //System.out.println("文     件："+tempList[i]);
//    		}
////    		if (tempList[i].isDirectory()) {
////    		    System.out.println("文件夹："+tempList[i]);
////    		}
//    	}

        
        //TxtUtil.readFileByBytes(fileName);
        //TxtUtil.readFileByChars(fileName);
        //TxtUtil.readFileByRandomAccess(fileName);
        
        String fileName = "D:/robot.txt";
//        String content = "new append!";
//        //按方法A追加文件
//        TxtUtil.appendMethodA(fileName, content);
//        TxtUtil.appendMethodA(fileName, "append end. \n");
//        //显示文件内容
//        TxtUtil.readFileByLines(fileName);
//        //按方法B追加文件
//        TxtUtil.appendMethodB(fileName, content);
//        TxtUtil.appendMethodB(fileName, "append end. \n");
//        //显示文件内容
        TxtUtil.readFileByLines(fileName);
    	
    	TxtUtil.readAppointedLineNumber(new File("D:/robot.txt"),2);
    }

}
