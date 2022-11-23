package kumoh.util.usb;

public class USBConnectListener implements Listener, Action {
    USBHandler handler;
    USBControl control;

    public USBConnectListener(USBHandler handler){
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
        if (control.findDrive().isSecureAvailableDisk())
            handler.setConnected(false);
        if (control.findDrive().isSecureAvailableDisk())
            handler.setConnected(true);
    }
}
