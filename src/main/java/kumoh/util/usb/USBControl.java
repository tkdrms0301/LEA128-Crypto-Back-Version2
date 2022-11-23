package kumoh.util.usb;

import java.io.File;
import java.io.IOException;

public class USBControl {
    public class Data {
        private String drive;
        private boolean isSecureAvailableDisk;

        private boolean isLogin;

        public String getDrive() {
            return drive;
        }

        public void setDrive(String drive) {
            this.drive = drive;
        }

        public boolean isSecureAvailableDisk() {
            return isSecureAvailableDisk;
        }

        public void setSecureAvailableDisk(boolean secureAvailableDisk) {
            isSecureAvailableDisk = secureAvailableDisk;
        }

        public boolean isLogin() {
            return isLogin;
        }

        public void setLogin(boolean login) {
            isLogin = login;
        }
    }

    private final USBHandler handler;

    public USBControl(USBHandler handler){
        this.handler = handler;
    }

    public Data findDrive() {
        String drive = null;
        double useSize;
        File[] roots = File.listRoots();
        boolean isSecureAvailableDisk = false;
        boolean isLogin = false;

        for (File root : roots) {
            drive = root.getAbsolutePath(); // 드라이브 정보
            useSize = root.getUsableSpace() / Math.pow(1024, 3); // 남은 용량
            //totalSize = root.getTotalSpace() / Math.pow(1024, 3); // 전체 용량
            //freeSize = totalSize - useSize; // 사용 용량

            if (isSecureAvailableDisk && useSize != 0) {
                isLogin = true;
                handler.setUsbDrive(drive);
                break;
            }

            if (useSize == 0)
                isSecureAvailableDisk = true;
        }

        Data data = new Data();
        data.setDrive(drive);
        data.setSecureAvailableDisk(isSecureAvailableDisk);
        data.setLogin(isLogin);
        return data;
    }


    public String makeFile(String path, String filename){ // drive = path
        File file = new File(path + filename);
        try {
            file.createNewFile();
            System.out.println("Created in " + path + " filename is " + filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path + filename;
    }

    public String getUSBDriveAbsolutePath(){
        return handler.getUsbDrive();
    }

}
