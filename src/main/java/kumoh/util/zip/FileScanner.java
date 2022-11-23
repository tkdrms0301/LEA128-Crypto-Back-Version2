package kumoh.util.zip;

import java.io.File;
import java.util.Arrays;

public class FileScanner {
    private String path;

    public FileScanner(String path){
        this.path = path;
    }

    public String[] getAllFiles(){
        return getAllFiles(this.path);
    }

    public String[] getAllFiles(String path){
        File file = new File(path);
        if (file.list() != null)
            return Arrays.stream(file.list()).map(f -> f = path + "/" + f).toArray(String[]::new);
        return null;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }

    public String getPathWithLastSlash(){
        return path + "/";
    }

    public File getFile(){
        return new File(path);
    }


}
