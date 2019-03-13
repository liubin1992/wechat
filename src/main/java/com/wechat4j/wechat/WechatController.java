package com.wechat4j.wechat;

import com.hotlcc.wechat4j.Wechat;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ShaoJun Liu
 * @mail
 * @create 2019-03-12 22:03
 */
@RestController
@AllArgsConstructor
public class WechatController {

    private Wechat wechat;

    @GetMapping("/login")
    public boolean login(){

        if(!wechat.isOnline()){
            wechat.logout();
            return wechat.autoLogin();
        }
        return false;
    }

    @GetMapping("/logout")
    public void logout(){
        if(!wechat.isOnline()){
            wechat.logout();
        }
    }
}
