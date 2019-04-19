package com.example.kunrui.apcheck.MethodsClass;

import java.io.File;
import java.io.RandomAccessFile;

public class FileOpr {
    public void init_config_file() {
        File file = new File("data/data/com.example.kunrui.apcheck/config.txt");
        try {
            if(file.exists()) {
                return;
//                System.out.println("file.exists()");
//                if(file.delete())
//                {
//                    System.out.println("file.delete");
//                }
            }
            if(file.createNewFile()) {
                System.out.println("file.createNewFile()");
            }
            RandomAccessFile fw = new RandomAccessFile(file, "rw"); // 创建文件输出流

            fw.writeBytes("LANGUAGE   none                \n");//语言
            fw.writeBytes("SSID       none                \n");//wifi名称
            fw.writeBytes("BSSID      none                \n");//MAC
            fw.writeBytes("RSSI       none                \n");  //采信号强度
            fw.writeBytes("PASSWORD   none                \n");  //WIFI密码
            fw.writeBytes("ENCRYPT    none                \n");  //加密方式
            fw.writeBytes("Address    none                \n");  //登录地址
            fw.writeBytes("AppPath    none                \n");  //APP页面
            fw.writeBytes("TEP_SSID   none                \n");  //临时连接SSID

            fw.close();
        } catch (Exception e) {
            System.out.println("创建失败");
        }
    }

    public void write_status(String target, String msg) {
        File file = new File("data/data/com.example.kunrui.apcheck/config.txt");
        try {
            RandomAccessFile fw = new RandomAccessFile(file, "rw"); // 创建文件输出流
            String str;
            int length = 0;
            while((str = fw.readLine()) != null) {
                length += str.length() + 1;
                if(str.split(" ", 2)[0].equals(target)) {
                    fw.seek(length - str.length() -1);
                    fw.writeBytes(target + " " + msg + "\n");
                    break;
                }
            }
            fw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("写入文件失败");
        }
    }

    public String read_status(String target) {
        System.out.println("target: " + target);
        String status = "ERROR";
        File file = new File("data/data/com.example.kunrui.apcheck/config.txt");
        if(!file.exists()) return "Not exist";
        try {
            RandomAccessFile fw = new RandomAccessFile(file, "rw"); // 创建文件输出流
            String str;
            while((str = fw.readLine()) != null) {
//                System.out.println("fw.readLine:" +str);
                if(str.split(" ", 2)[0].equals(target)) {
//                    System.out.println("fw.readLine:" +str);
                    status = str.split(" ", 2)[1].trim();
                    break;
                }
            }
            fw.close();
            System.out.println("status:" +status);
            return status;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("读取文件失败");
        }
        return "failed";
    }
}
