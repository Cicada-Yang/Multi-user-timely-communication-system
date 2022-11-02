package qqserver.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 杨周
 * @data 2022/10/24
 * @time 19:31
 * 该类用于管理和客户端通信的线程
 */
public class ManageServerConnectClientThreads {
    public static HashMap<String,ServerConnectClientThread> hm = new HashMap<>();
    //添加线程到hm集合
    public static void addclientThread(String userId,ServerConnectClientThread serverConnectClientThread){
        hm.put(userId,serverConnectClientThread);
    }
    //根据userId 返回ServerConnectClientThread线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }
    //在这里编写方法，可以返回在线用户列表
    public static String getOnlineUser(){
        //集合遍历，遍历hashmap的key
        //2.迭代器
        Iterator<String> iterator = hm.keySet().iterator();
        StringBuilder onlineUserList = new StringBuilder();
        while (iterator.hasNext()){
            onlineUserList.append(iterator.next()).append(" ");
            System.out.println(onlineUserList);
        }
        return onlineUserList.toString();
    }
    //将某用户端所连接的socket剔除
    public static void removeServerConnectClientThread(String userId){
        hm.remove(userId);
    }
}
