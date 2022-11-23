package kumoh.util.usb;

import kumoh.config.defaultconfig.ConfigLoader;

public class USBLoginListener implements Listener, Action {
    USBHandler handler;
    USBControl control;

    public USBLoginListener(USBHandler handler){
        this.handler = handler;
        this.control = new USBControl(handler);
        handler.addListener(this);
    }

    @Override
    public void listen() {
        action();
    }

    @Override
    public void action() {
        if (control.findDrive().isLogin()) {
            handler.setLogin(true);
            if(handler.getUsbDrive() != null){
                handler.getKeyConfig().setKEY(handler.getUsbDrive());
                handler.getKeyConfig().keyConfiguration();
                handler.getConfigLoader().onApplicationEvent(); // config 설정 활성화
            }
            else{
                handler.getKeyConfig().setKEY(null);
                handler.getKeyConfig().setConfiguration(false);
            }
        }
        if (!control.findDrive().isLogin()) {
            handler.setLogin(false);
            if (hasLinkedHashMap()){
                ConfigLoader.getLinkedHashMap().clear(); // 초기화
                ConfigLoader.ONE_TIME = false;
            }
        }
    }

    private boolean hasLinkedHashMap(){
        if (ConfigLoader.getLinkedHashMap() == null)
            return false;
        return true;
    }
}
