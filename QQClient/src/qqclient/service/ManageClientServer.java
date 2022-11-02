package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author 杨周
 * @data 2022/10/27
 * @time 18:46
 * 该类/对象，提供和消息相关的服务方法
 */
public class ManageClientServer {
    public void privateChat(String Getter, String Sender, String content){
        //私聊系统
        Message message = new Message();
        message.setMessType(MessageType.MESSAGE_COMM_MES);
        message.setGetter(Getter);
        message.setSender(Sender);
        message.setContent(content);
        //发送message
        try{
            //得到当前线程的Socekt 对应的ObjectOutputStream对象
            ObjectOutputStream oss = new ObjectOutputStream
                    (ManageClientConnctServerThread.getClientConnectServerThread(Sender).getSocket().getOutputStream());
            oss.writeObject(message);//发送一个Message对象，向服务端请求与某用户私聊，并发送私聊内容
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Sender+"向"+Getter+"发送了"+content);
    }
    public void groupChat(String sender,String content){
        //群发系统
        Message message = new Message();
        message.setMessType(MessageType.MESSAGE_ALLCOMM_MES);
        message.setContent(content);
        message.setSender(sender);
        //发送message
        try{
            //得到当前线程的Socekt 对应的ObjectOutputStream对象
            ObjectOutputStream oss = new ObjectOutputStream
                    (ManageClientConnctServerThread.getClientConnectServerThread(sender).getSocket().getOutputStream());
            oss.writeObject(message);//发送一个Message对象，向服务端请求与某用户私聊，并发送私聊内容
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sender+"向大家发送了"+content);
    }
}
