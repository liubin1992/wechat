package com.wechat4j.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import com.hotlcc.wechat4j.model.ReceivedMsg;
import com.hotlcc.wechat4j.model.UserInfo;
import com.hotlcc.wechat4j.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

/**
 * 消息处理机制
 *
 * @author ShaoJun Liu
 * @mail
 * @create 2019-03-12 18:01
 */
@Slf4j
@Component(value = "receivedMsgHandler")
public class ReceivedMsgHandlerImpl implements ReceivedMsgHandler {
    @Override
    public void handleAllType(Wechat wechat, ReceivedMsg msg) {
        String fromUserName = msg.getFromUserName();

        log.info("用户：" + fromUserName + "发来信息:" + msg.getContent());

        //根据UserName获取联系人信息
        UserInfo contact = wechat.getContactByUserName(true, msg.getFromUserName());
        log.debug("UserInfo:" + JSON.toJSONString(contact));
        if(contact == null){
            return;
        }

        /*String name = StringUtil.isEmpty(contact.getRemarkName()) ? contact.getNickName() : contact.getRemarkName();
        System.out.println(name + ": " + msg.getContent());*/

        synchronized (this){
            sendMessage(wechat, msg.getContent(), contact.getUserName(), contact.getNickName(), contact.getRemarkName());
        }
    }


    public void sendMessage(Wechat wechat, String content, String userName, String nickName, String remarkName){
        if((StringUtils.isEmpty(userName) && StringUtils.isEmpty(nickName) && StringUtils.isEmpty(remarkName))){
            return;
        }

        int max=20;
        int min=3;
        Random random = new Random();

        int s = random.nextInt(max)%(max-min+1) + min;

        try {
            Thread.sleep(s * 1000L);
        } catch (InterruptedException e) {
            log.error("休眠被打断");
        }

        if (StringUtils.isEmpty(content)){
            content = "咦~ 你发的啥，这条不答复你[右哼哼]。。。";
        }else {
            try {
                content = tulingQA(content);
                if(StringUtil.isNotEmpty(content)){
                    JSONObject jsonObject = JSON.parseObject(content);
                    content = jsonObject.getString("text");
                }
            } catch (IOException e) {
                content = "哎呀！我脑壳卡住了，这个问题没办法回复！";
            }
        }

        if(content.length() > 100){
            List<String> divLines = StringUtil.getDivLines(content, 100);
            for (String msg: divLines) {
                wechat.sendText(userName, nickName, remarkName, msg+"【切片发送】");
                try {
                    Thread.sleep((random.nextInt(max)%(max-min+1) + min)*1000L);
                } catch (InterruptedException e) {
                    log.error("休眠被打断");
                }
            }
            log.info("自动发送信息："+content + " 给：" + (StringUtils.isEmpty(remarkName) ? userName : remarkName));
            return;
        }

        JSONObject jsonObject = wechat.sendText(userName, nickName, remarkName,   content);
        log.info("自动发送信息："+jsonObject.toJSONString() + " 给：" + (StringUtils.isEmpty(remarkName) ? userName : remarkName));

        // 通过userName发送文本消息
//        JSONObject sendText(String content, String userName);
// 通过昵称发送文本消息
//        JSONObject sendTextToNickName(String content, String nickName);
// 通过备注名发送文本消息
//        JSONObject sendTextToRemarkName(String content, String remarkName);
// 发送文本消息（根据多种名称）
//        JSONObject sendText(String userName, String nickName, String remarkName, String content);
    }

    private static final String APIKEY = "57b76ac52eb94307a162047e4f8c4b4b";

    public static String tulingQA(String question) throws UnsupportedEncodingException,
            MalformedURLException, IOException {
        String INFO = URLEncoder.encode(question, "utf-8");
        String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY
                + "&info=" + INFO;
        URL getUrl = new URL(getURL);
        HttpURLConnection connection = (HttpURLConnection) getUrl
                .openConnection();
        connection.connect();

        // 取得输入流，并使用Reader读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "utf-8"));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        // 断开连接
        connection.disconnect();
        System.out.println(sb);
        return sb.toString();
    }

    /*public static void main(String[] args) {
        try {
            String s = tulingQA("你好呀！");
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
