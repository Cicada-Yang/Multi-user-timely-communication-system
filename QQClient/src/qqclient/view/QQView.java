package qqclient.view;
/**
 * @author 杨周
 * @data 2022/10/23
 * @time 17:38
 * 客户端的菜单界面
 */

import qqclient.service.FileCilentServer;
import qqclient.service.ManageClientServer;
import qqclient.service.UserClientService;
import qqclient.utils.Utility;

import java.net.UnknownHostException;

public class QQView {
    private boolean loop = true;//控制是否显示菜单
    private String key = "";//接收用户的键盘输入
    private UserClientService  userClientService = new UserClientService();//对象是用于登录服务器/注册用户
    private ManageClientServer manageClientServer = new ManageClientServer();//对象是用于用户的私聊/群发
    private FileCilentServer fileCilentServer = new FileCilentServer();//对象是用于用户发送文件
    public static void main(String[] args) throws UnknownHostException {
        new QQView().mainmenu();
        System.out.println("客户端退出系统......");
    }
    private void mainmenu() throws UnknownHostException {
        while(loop){
            System.out.println("=============欢迎登录网络通信系统=============");
            System.out.println("\t\t1 登录系统");
            System.out.println("\t\t9 退出系统");
            System.out.println("请输入你的选择：");
            key = Utility.readString(1);
            //根据用户的输入，来处理不同的逻辑
            switch (key){
                case "1":
                    System.out.println("登录系统");
                    System.out.println("请输入你的账号：");
                    String userId=Utility.readString(50);
                    System.out.println("请输入密码");
                    String pwd=Utility.readString(50);
                    //这里就比较麻烦了，需要到服务器去验证该用户是否合法
                    //这里又很多代码，我们这里编写一个类UserClientService[用户登录/注册]
                    if(userClientService.chreckUser(userId,pwd)){//还没有写完，先把这个逻辑打通。
                        System.out.println("=============欢迎(用户"+userId+"登录成功)=============");
                        while (loop){
                            System.out.println("\n=============网络通信系统二级菜单(用户 "+userId+")=============");
                            System.out.println("\t\t1 显示在线用户列表");
                            System.out.println("\t\t2 群发消息");
                            System.out.println("\t\t3 私聊消息");
                            System.out.println("\t\t4 发送文件");
                            System.out.println("\t\t9 退出系统");
                            System.out.println("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    System.out.println("显示在线用户列表");
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("请输入你想说的话：");
                                    String content = Utility.readString(100);
                                    manageClientServer.groupChat(userId,content);
                                    break;
                                case "3":
                                    System.out.println("请输入想聊天的用户号(在线)：");
                                    String name = Utility.readString(20);
                                    System.out.println("请输入想说的话：");
                                    String content1 = Utility.readString(100);
                                    manageClientServer.privateChat(name,userId,content1);
                                    break;
                                case "4":
                                    System.out.println("请输入发送文件的地址：");
                                    String src = Utility.readString(50);
                                    System.out.println("请输入接收方的ID：");
                                    String getter = Utility.readString(20);
                                    System.out.println("请输入接收放的默认存放地址：");
                                    String dest = Utility.readString(50);
                                    fileCilentServer.sendFileToOne(src,dest,userId,getter);
                                    break;
                                case "9":
                                    //调用方法，给服务一个系统的message
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    }
                    else
                        System.out.println("=============登录失败=============");
                    break;
                case "9":
                    loop=false;
                    break;
            }
        }
    }
}
