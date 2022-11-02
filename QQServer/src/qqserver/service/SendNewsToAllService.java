package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;
import utility.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author 杨周
 * @data 2022/11/1
 * @time 17:24
 */
public class SendNewsToAllService implements Runnable{
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while(true) {
            System.out.println("请输入通知内容(输入 exit 退出推送服务)：");
            String news = Utility.readString(100);
            if ("exit".equals(news))
                break;
            //群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            message.setMessType(MessageType.MESSAGE_ALLCOMM_MES);
            //遍历当前所有信息，得到socket，并发送message
            HashMap<String, ServerConnectClientThread> hm = ManageServerConnectClientThreads.hm;
            for (String s : hm.keySet()) {
                String onlineUserId = s.toString();
                try {
                    ObjectOutputStream objectOutputStream = new
                            ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }System.out.println("服务器推送消息给所有人说："+news);
        }
    }
}
