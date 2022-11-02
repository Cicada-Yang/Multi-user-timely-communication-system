package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author 杨周
 * @data 2022/10/24
 * @time 9:34
 * 该类完成用户登录验证和用户注册等功能
 */
public class UserClientService {
    //因为我们可能在其它地方使用user信息，因此作出成员属性
    private User u = new User();
    public boolean chreckUser(String userId,String pwd) throws UnknownHostException {
        boolean b = false;
        //创建User对象
        u.setUserId(userId);
        u.setPasswd(pwd);
        //连接到服务器，发送u对象
        try {
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oss = new ObjectOutputStream(socket.getOutputStream());
            oss.writeObject(u);//发送User对象

            //读取从服务器回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if(ms.getMessType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){//登录成功
                //创建一个和服务器端保持通信的线程 ->创建一个类ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端线程
                clientConnectServerThread.start();
                //这里是为了后面客户端的扩展，我们将线程放入到集合进行管理
                ManageClientConnctServerThread.addClientConnectServerThread(userId,clientConnectServerThread);
                b = true;
            }else {
                //如果登录失败，我们就不能启动和服务器通信的线程，关闭socket
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
    //向服务器端请求在线用户列表
    public void onlineFriendList(){
        //发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setSender(u.getUserId());
        message.setMessType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        //发送给服务器
        try{
            //得到当前线程的Socekt 对应的ObjectOutputStream对象
            ObjectOutputStream oss = new ObjectOutputStream
                    (ManageClientConnctServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oss.writeObject(message);//发送一个Message对象，向服务端要求发送在线用户列表
        }catch (IOException e) {
                e.printStackTrace();
        }
    }


    public void logout(){
        //退出客户端退出系统
        Message message = new Message();
        message.setMessType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定我是哪个客户端id
        //发送message
        try {
            ObjectOutputStream oos = new
                    ObjectOutputStream(ManageClientConnctServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId()+"退出系统");
            System.exit(0);//退出它的主进程，所有线程全部结束。
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
