package kumoh.util.zip;

import java.io.File;

public class ZipTest {
    public static void main(String[] args) {
        ZipConvertor convertor = new ZipConvertor(new FileScanner("src/test")); // 파일이 모여 있는 경로
        File zipFile = convertor.convert("test.zip", "src/test"); // 파일 이름만 넣으면 모여 있는 경로에 저장, 혹은 src/test/a/a.zip 과 같이 넣을 수 있음
    }
}
