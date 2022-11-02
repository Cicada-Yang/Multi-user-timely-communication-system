package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author 杨周
 * @data 2022/10/24
 * @time 18:21
 * 这是服务器，在监听9999，等待客户端的连接，并保持通信
 */
public class QQServer {
    //注意：端口可以写在配置文件里
    private ServerSocket ss = null;
    //这里我们也可以使用ConcurrentHashMap，可以处理并发的集合，没有线程安全问题
    //HashMap 没有处理线程安全，因此在多线程情况下是不安全的
    //ConcurrentHashMap 处理的线程安全，即线程同步处理，在多线程情况下是安全的
    private static ConcurrentHashMap<String ,User> validUsers = new ConcurrentHashMap<>();
    //用来保存离线信息与文件
    private static ConcurrentHashMap<String , ArrayList<Message>> offlineMessage = new ConcurrentHashMap<>();
    static {
        validUsers.put("100",new User("100","123456"));
        validUsers.put("TZ",new User("TZ","123456"));
        validUsers.put("唐僧",new User("唐僧","123456"));
        validUsers.put("至尊宝",new User("至尊宝","123456"));
        validUsers.put("紫霞仙子",new User("紫霞仙子","123456"));
        validUsers.put("菩提老祖",new User("菩提老祖","123456"));
    }
    //验证用户是否有效的方法
    private boolean checkUser(String userId, String passwd){
        User user = validUsers.get(userId);
        if(user==null){//用户不存在
            return false;
        }
        if(!user.getPasswd().equals(passwd)){//userId正确，密码错误
            return false;
        }
        return true;
    }
    public QQServer(){
        try {
            System.out.println("服务器在9999端口监听...");
            ss = new ServerSocket(9999);
            //启动推送服务
            new Thread(new SendNewsToAllService()).start();
            while (true){//当和某个客户端连接后，会继续监听，因此while
                Socket socket = ss.accept();//如果没有客户端连接，就会阻塞
                //得到socket关联的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //得到socket关联的对象输出流
                ObjectOutputStream oss = new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();//读取客户端发送的User对象
                //创建一个Message对象，准备回到客户端
                Message message = new Message();
                if (checkUser(u.getUserId(),u.getPasswd())){
                    message.setMessType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复客户端
                    oss.writeObject(message);
                    //创建一个线程，和客户端保持通信，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket,u.getUserId());
                    serverConnectClientThread.start();
                    //把该线程对象放入到一个集合中进行管理
                    ManageServerConnectClientThreads.addclientThread(u.getUserId(),serverConnectClientThread);

                }else {//登录失败
                    System.out.println("用户 id="+u.getUserId()+"登录失败");
                    message.setMessType(MessageType.MESSAGE_LOGIN_FAIL);
                    oss.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }  finally {
            try {
                //如果服务端退出了while循环，说明服务器端不在监听，因此关闭ServerSocket
                assert ss != null;
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConcurrentHashMap<String, ArrayList<Message>> getOfflineMessage() {
        return offlineMessage;
    }
}
