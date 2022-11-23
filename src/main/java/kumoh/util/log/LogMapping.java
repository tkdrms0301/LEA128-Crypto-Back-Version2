package kumoh.util.log;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


@Getter
@Component
public class LogMapping {
    private String year;
    private String month;
    private String day;
    private String division;

    private final LogUtils logUtils;

    public LogMapping(LogUtils logUtils){
        this.logUtils = logUtils;
        division = "-";
        year = "";
        month = "";
        day = "";

    }

    public void setYear(String year) {
        this.year = year + division;
    }

    public void setMonth(String month) {
        this.month = month + division;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public List<File> getMappedFiles(){
        return getMappedFiles(year + month + day);
    }

    public List<File> getMappedFiles(String keyWord){
        File dir = new File(logUtils.getLOG_DIR());
        List<File> dirs = Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList();

        return dirs.stream().filter(Predicate.not(di -> !di.toString().contains(keyWord))).toList();
    }


}
