package kumoh.util.fileEncryption;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;


@Component
public class FileEncryptionDir {

    //TODO:저장 경로가 외부 파일에 저장되야됨. 그래야 저장했을 때 다음에 수정가능.
    private static String dir;
    private File target;

    public void setDir(String dir){
        FileEncryptionDir.dir = dir;
        target = new File(dir);

        try {
            dirValidation();
        } catch (FileNotFoundException e) {
            mkdirs(dir);
        }
    }

    public static String getDir() {
        return FileEncryptionDir.dir;
    }

    /**
     *
     * @param dir
     * @return : 파라미터의 하부 디렉토리들을 반환.
     * @throws FileNotFoundException
     */
//    public List<File> getChildDirs(String dir) throws FileNotFoundException {
//        target = new File(dir);
//        dirValidation();
//
//        File[] list = target.listFiles();
//
//        return Arrays.stream(Objects.requireNonNull(list)).toList();
//    }
//
//    /**
//     *
//     * @return 현재 설정되어 있는 파라미터의 디렉토리들을 반환.
//     * @throws FileNotFoundException
//     */
//    public List<File> getChildDirs() throws FileNotFoundException {
//        target = new File(dir);
//        dirValidation();
//
//        File[] list = target.listFiles();
//
//        return Arrays.stream(Objects.requireNonNull(list)).toList();
//    }
//    File getFile() { return target; }
//
    void mkdir(String dir) {
        target = new File(dir);

        if(!target.exists()){
            target.mkdir();
        }

    }

    void mkdirs(String dir){
        target = new File(dir);

        if(!target.exists()){
            target.mkdirs();
        }
    }

    private void dirValidation() throws FileNotFoundException {
        if(!target.exists()){
            throw new FileNotFoundException("해당 경로를 찾을 수 없습니다.");
        }
    }




}
