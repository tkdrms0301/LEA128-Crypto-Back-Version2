package kumoh.util.file;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileDirList {
    public List<String> fileDirNameList(String folderPath){
        File dir = new File(folderPath);
        List<String> fileNameList = new ArrayList<>();
        if(dir.isDirectory()){
            File[] fileObjects = dir.listFiles();
            if (fileObjects != null) {
                for (File file : fileObjects){
                    if(file.isFile()){
                        fileNameList.add(file.getName());
                    }
                }
            }
        }
        return fileNameList;
    }

    public List<String> filePathList(String folderPath){
        File dir = new File(folderPath);
        List<String> fileNameList = new ArrayList<>();
        if(dir.isDirectory()){
            File[] fileObjects = dir.listFiles();
            if (fileObjects != null) {
                for (File file : fileObjects){
                    fileNameList.add(file.getPath());
                }
            }
        }
        return fileNameList;
    }
}
