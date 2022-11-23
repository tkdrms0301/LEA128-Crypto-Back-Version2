package kumoh.config;

import kumoh.util.usb.USBConnectListener;
import kumoh.util.usb.USBHandler;
import kumoh.util.usb.USBLoginListener;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Lazy
@Getter
public class USBConnect {
    private USBHandler handler;
    private Boolean isLogin;
    public USBConnect(USBHandler handler){
        this.handler = handler;
        new USBConnectListener(handler);
        new USBLoginListener(handler);
        fixedRateScheduler();
    }

    public boolean isConnected(){
        handler.validate();
        return handler.isConnected();
    }

    public boolean isLogin(){
        handler.validate();
        return isLogin=handler.isLogin();
    }

    @Scheduled(fixedRate=1000) // 단위: ms
    public void fixedRateScheduler() {
        isLogin();
    }
}
