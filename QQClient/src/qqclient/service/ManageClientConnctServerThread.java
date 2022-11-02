package qqclient.service;

import java.util.HashMap;

/**
 * @author 杨周
 * @data 2022/10/24
 * @time 12:56
 * 该类管理客户端连接到服务器端的线程的类
 */
public class ManageClientConnctServerThread {
    //我们把多个线程放入一个HahsMap集合，key接收用户id，value就是线程
    private static HashMap<String ,ClientConnectServerThread> hm = new HashMap<>();
    //将某个线程加入到集合
    public static void addClientConnectServerThread(String userId,ClientConnectServerThread clientConnectServerThread){
        hm.put(userId,clientConnectServerThread);
    }
    //通过userId 可以得到对应的线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hm.get(userId);
    }
}
