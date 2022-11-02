package qqcommon;/**
 * @author 杨周
 * @data 2022/10/23
 * @time 14:27
 * 表示客户端和服务器端通信时的消息对象
 */

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;//发送者
    private String getter;//接收者
    private String content;//消息内容
    private String sendTime;//发送时间
    private String messType;//消息类型【可以在接口定义消息类型】

    //进行扩展，文件相关的成员
    private byte[] fileBytes;
    private int filelen = 0;
    private String src;//源文件路径
    private String dest;//将文件传输到哪里

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public int getFilelen() {
        return filelen;
    }

    public String getDest() {
        return dest;
    }

    public String getSrc() {
        return src;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setFilelen(int filelen) {
        this.filelen = filelen;
    }

    public String getMessType() {
        return messType;
    }

    public void setMessType(String messType) {
        this.messType = messType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getContent() {
        return content;
    }

    public String getGetter() {
        return getter;
    }

    public String getSender() {
        return sender;
    }
}
