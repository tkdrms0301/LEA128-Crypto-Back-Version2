package kumoh.util.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class LogDirBuilder {

    private Integer year;
    private Integer month;
    private Integer day;
    private String defaultDir = LogFileDir.getDir();
    private final static String LOG_FILE = "log.txt";


    public String getDir(){
        StringBuilder sb = new StringBuilder();
        setDefaultDir(sb);
        setYear(sb);
        setMonth(sb);
        setDay(sb);
        return sb.toString();
    }

    private void setDefaultDir(StringBuilder dirBuilder){
        dirBuilder.append(defaultDir);
    }

    private void setYear(StringBuilder dirBuilder) {
        if(year != null){
            dirBuilder.append(year);
        }
        if(month != null){
            dirBuilder.append("/");
        }
    }

    private void setMonth(StringBuilder dirBuilder) {
        if(month != null){
            dirBuilder.append(month);
        }
        if(day != null){
            dirBuilder.append("/");
        }
    }

    private void setDay(StringBuilder dirBuilder) {
        if(day != null){
            dirBuilder.append(day);
        }
    }

    private void setFile(StringBuilder dirBuilder){
        if(year != null && month != null && day != null){
            dirBuilder.append("/");
            dirBuilder.append(LOG_FILE);
        }
    }
}
