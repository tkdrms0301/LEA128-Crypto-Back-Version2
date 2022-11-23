package kumoh.config.defaultconfig;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

@Component
public class UserLoginInfo {


    public void saveLoginInfo(String id, String pw, File file){

    }

    public void loadUserInfo(HashMap<String, String> configData){

    }

    public void updateUserInfo(String id, String pw, String auth){

    }
}
