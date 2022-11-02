package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.*;

/**
 * @author 杨周
 * @data 2022/10/28
 * @time 9:40
 * 该类/对象，完成文件传输的服务
 */
public class FileCilentServer {
    /**
     *
     * @param src 源文件
     * @param dest 目标路径
     * @param sender 发送方
     * @param getter 接收方
     */
    public void sendFileToOne(String src,String dest,String sender,String getter){
        Message message = new Message();
        message.setSrc(src);
        message.setDest(dest);
        message.setSender(sender);
        message.setGetter(getter);
        message.setMessType(MessageType.MESSAGE_FILE_MES);
        //需要将文件读取
        FileInputStream fileInputStream = null;
        message.setFilelen((int)new File(src).length());
        byte[] fileByte = new byte[message.getFilelen()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileByte);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream!=null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //发送message
        message.setFileBytes(fileByte);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream
                    (ManageClientConnctServerThread.getClientConnectServerThread(message.getSender()).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sender+"给"+getter+"发送文件"+src+"对方的"+dest);
    }
}
