package kumoh.util.usb;

import kumoh.config.KeyConfig;
import kumoh.config.defaultconfig.ConfigLoader;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
public class USBHandler implements Subject{
    private List<Listener> listeners;
    private boolean isConnected = false;
    private final short vendorId = 8353;
    private final short productId = 28675;
    private boolean isLogin = false;
    private final ConfigLoader configLoader;
    private String usbDrive;
    private final KeyConfig keyConfig;

    public USBHandler(ConfigLoader configLoader, KeyConfig keyConfig){
        this.configLoader = configLoader;
        this.keyConfig = keyConfig;
        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void deleteListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListener() {
        listeners.forEach(Listener::listen);
    }

    public void validate(){
        notifyListener();
    }
}
