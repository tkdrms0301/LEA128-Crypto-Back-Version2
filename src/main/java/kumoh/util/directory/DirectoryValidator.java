package kumoh.util.directory;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
@Component
public class DirectoryValidator {
    private String directory;

    public DirectoryValidator(){
        directory = System.getProperty("user.dir");
    }

    public void setDirectory(String directory){
        if (isEmptyString(directory))
            throw new RuntimeException("Directory can not be null or empty");

        if (hasSlashedInStringWhereLast(directory)){
            directory = deleteStringWhereLast(directory);
        }
        this.directory = directory;
    }

    private boolean isEmptyString(String value){
        if (value == null)
            return true;

        return value.length() == 0;
    }

    private boolean hasSlashedInStringWhereLast(String directory){
        String lastString = getLastString(directory);
        return lastString.equals("\\") || lastString.equals("/");
    }

    private String getLastString(String str){
        return str.substring(str.length() - 1);
    }

    private String deleteStringWhereLast(String str){
        return str.substring(0, str.length() - 1);
    }

    public boolean hasAuthorityInDirectory(){
        if (isEmptyString(directory))
            throw new RuntimeException("Need to setting directory");

        String fileChecker = "/fileChecker.txt";
        File file = new File(directory + fileChecker);
        return createAndDeleteFile(file);
    }

    private boolean createAndDeleteFile(File file){
        try{
            file.createNewFile();
            file.delete();
            return true;
        }catch (IOException e){
            return false;
        }
    }
}
