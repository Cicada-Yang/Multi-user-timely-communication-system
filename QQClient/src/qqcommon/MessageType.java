package qqcommon;/**
 * @author 杨周
 * @data 2022/10/23
 * @time 14:28
 * 表示消息类型
 */

public interface MessageType {
    //1.在接口定义了一些常量
    //2.不同的常量的值，表示不同的消息类型
    String MESSAGE_LOGIN_SUCCEED = "1";//表示登录成功
    String MESSAGE_LOGIN_FAIL = "2";//表示登录失败
    String MESSAGE_COMM_MES = "3";//普通信息包
    String MESSAGE_GET_ONLINE_FRIEND = "4";//发送在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    String MESSAGE_ALLCOMM_MES = "7";//群聊信息报
    String MESSAGE_FILE_MES = "8";//文件传输
}
