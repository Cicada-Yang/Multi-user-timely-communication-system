package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 杨周
 * @date 2022/10/24
 * @time 18:54
 * 该类的一个对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userid;//连接到服务的用户的id


    public ServerConnectClientThread(Socket socket, String userid) {
        this.socket = socket;
        this.userid = userid;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        //接收离线信息与文件
        ConcurrentHashMap<String , ArrayList<Message>> offlineMessage = QQServer.getOfflineMessage();
        if(offlineMessage.containsKey(userid)){
            ArrayList<Message> arrayList = offlineMessage.get(userid);
            for (Message o :arrayList) {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(o);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            offlineMessage.remove(userid);
        }
        while (true) {
            try {
                System.out.println("服务端和客户端" + userid + "保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //后面会用到Message,根据message的类型，做相应的业务处理
                if (message.getMessType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端要在线用户列表
                    /*
                      在线用户列表形式100 200 紫霞仙子
                    */
                    System.out.println(message.getSender() + "请求获得在线用户列表");
                    String onlineUser = ManageServerConnectClientThreads.getOnlineUser();
                    //返回message
                    //构建一个Message对象，返回给客户端
                    Message message1 = new Message();
                    message1.setMessType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message1.setContent(onlineUser);
                    message1.setGetter(message.getSender());
                    //message1.setSender(message.getGetter());
                    //返回给客户端
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(message1);
                    System.out.println("已发送");
                } else if (message.getMessType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println(message.getSender() + "请求与用户" + message.getGetter() + "私聊");
                    //查找接收用户是否在线,若在线，则获得其线程
                    ServerConnectClientThread clientThread =
                            ManageServerConnectClientThreads.getServerConnectClientThread(message.getGetter());
                    if (clientThread != null) {
                        //获得该线程对应的socket
                        Socket GetterSocket = clientThread.getSocket();//是接收者的socket
                        //将message发送至接收者
                        ObjectOutputStream outputStream = new ObjectOutputStream(GetterSocket.getOutputStream());
                        outputStream.writeObject(message);
                        System.out.println("已帮助用户" + message.getSender() + "发送私聊信息给" + message.getGetter());
                    } else {
                        //该用户未上线，将离线保存信息。
                        Message message1 = new Message();
                        message1.setSender("Server");
                        message1.setGetter(message.getSender());
                        message1.setMessType(MessageType.MESSAGE_COMM_MES);
                        message1.setContent("该用户未上线，等他上线时将会收到你的信息");
                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject(message1);//如果客户不在，可以将内容保留到数据库，这样就能实现离线留言
                        //判断是否已经创了该离线用户的信息集合
                        if(offlineMessage.containsKey(message.getGetter())){
                            ArrayList<Message> arrayList = offlineMessage.get(message.getGetter());//将该用户的arraylist提取出了
                            arrayList.add(message);
                            offlineMessage.put(message.getGetter(),arrayList);
                        }
                        else {//否
                            ArrayList<Message> arrayList = new ArrayList<>();
                            arrayList.add(message);
                            offlineMessage.put(message.getGetter(),arrayList);
                        }
                    }
                } else if (message.getMessType().equals(MessageType.MESSAGE_ALLCOMM_MES)) {
                    //群发消息
                    System.out.println(message.getSender() + "请求群发信息");
                    String onlineUser = ManageServerConnectClientThreads.getOnlineUser();//收集到所有在线用户的用户名
                    String[] Username = onlineUser.split(" ");
                    for (String username : Username) {
                        System.out.println(username);
                        if (!username.equals(message.getSender())) {//不群发给自己
                            Socket GetterSocket2 = ManageServerConnectClientThreads.getServerConnectClientThread(username).getSocket();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(GetterSocket2.getOutputStream());
                            objectOutputStream.writeObject(message);
                        }
                    }

                } else if (message.getMessType().equals(MessageType.MESSAGE_FILE_MES)) {
                    //私发文件
                    System.out.println("用户" + message.getSender() + "要向用户" + message.getGetter() + "发送文件");
                    //查找接收用户是否在线,若在线，则获得其线程
                    ServerConnectClientThread clientThread1 =
                            ManageServerConnectClientThreads.getServerConnectClientThread(message.getGetter());
                    if (clientThread1 != null) {
                        //获得该线程对应的socket
                        Socket GetterSocket = clientThread1.getSocket();//是接收者的socket
                        //将message发送至接收者
                        ObjectOutputStream outputStream = new ObjectOutputStream(GetterSocket.getOutputStream());
                        outputStream.writeObject(message);
                        System.out.println("已帮助用户" + message.getSender() + "发送文件给" + message.getGetter());
                    } else {
                        //该用户未上线，该离线保存文件
                        Message message1 = new Message();
                        message1.setSender("Server");
                        message1.setGetter(message.getSender());
                        message1.setMessType(MessageType.MESSAGE_COMM_MES);
                        message1.setContent("该用户未上线，已离线保存该文件");
                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject(message1);//如果客户不在，可以将内容保留到数据库，这样就能实现离线留言
                        //判断是否已经创了该离线用户的信息集合
                        if(offlineMessage.containsKey(message.getGetter())){
                            ArrayList<Message> arrayList = offlineMessage.get(message.getGetter());//将该用户的arraylist提取出了
                            arrayList.add(message);
                            offlineMessage.put(message.getGetter(),arrayList);
                        }
                        else {//否
                            ArrayList<Message> arrayList = new ArrayList<>();
                            arrayList.add(message);
                            offlineMessage.put(message.getGetter(),arrayList);
                        }
                    }


                } else if (message.getMessType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + "退出");
                    //将这个客户端对应的线程，从集合删除
                    ManageServerConnectClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();//关闭连接
                    //退出线程
                    break;
                } else {
                    System.out.println("其他类型的message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
