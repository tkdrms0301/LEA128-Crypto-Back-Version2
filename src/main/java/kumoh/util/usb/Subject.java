package kumoh.util.usb;

public interface Subject {
    void addListener(Listener listener);
    void deleteListener(Listener listener);
    void notifyListener();
}
