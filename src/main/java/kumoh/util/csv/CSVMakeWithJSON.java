package kumoh.util.csv;

import org.json.CDL;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CSVMakeWithJSON {

    public static File getFile(JSONArray json, Path path, String pkRegex) throws IOException {
        File file = path.toFile();

        if(!file.exists()){
            File parent = file.getParentFile();
            parent.mkdirs();
            file.createNewFile();
        }

        String csvString = CDL.toString(json);
        CSVTable csvTable = new CSVTable(csvString, pkRegex);
        csvTable.writeToFile(file);
        return file;
    }
}
