package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author 杨周
 * @data 2022/10/24
 * @time 11:59
 * 该类的对象和服务端保持通信
 */
public class ClientConnectServerThread extends Thread{
    //该线程需要持有Socket
    private Socket socket;
    //构造器可以接受一个Socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        //因为Thread需要在后台一直和服务器端通信，因此我们while循环
        while (true){
            try {
                System.out.println("客户端线程等待读取从服务器端发送的消息");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //如果服务器端没有发送来Message数据，线程就会阻塞在这里
                Message message = (Message) objectInputStream.readObject();
                System.out.println("客户端已接收");
                if(message.getMessType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    //取出在线链表，并显示
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("========当前用户列表=======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户："+onlineUsers[i]);
                    }
                } else if (message.getMessType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //私聊
                    System.out.println(message.getSender()+"对"+message.getGetter()+"说："+message.getContent());
                } else if (message.getMessType().equals(MessageType.MESSAGE_ALLCOMM_MES)) {
                    //群聊
                    System.out.println(message.getSender()+"对你说："+message.getContent());
                } else if (message.getMessType().equals(MessageType.MESSAGE_FILE_MES)) {
                    //接收文件
                    FileOutputStream fileOutputStream = null;
                    fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes(),0,message.getFilelen());
                    System.out.println("你收到了"+message.getSender()+"的文件，存放在"+message.getDest());
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
