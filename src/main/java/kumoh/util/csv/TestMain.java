package kumoh.util.csv;

import kumoh.util.json.request.ApiRequest;
import org.json.CDL;
import org.json.JSONArray;

import java.io.File;

public class TestMain {
    public static void main(String[] args) {
        ApiRequest apiRequest = new ApiRequest();

        JSONArray jsonArray = apiRequest.getJsonArray("https://jsonplaceholder.typicode.com/users");
        CSVTable csvTable = new CSVTable(CDL.toString(jsonArray), "id");
        File file = new File("/Users/kimhyunjin/IdeaProjects/Blockchain-LEA128-Crypto/file/live/User.csv");
        csvTable.writeToFile(file);

        jsonArray = apiRequest.getJsonArray("https://jsonplaceholder.typicode.com/users");
        CSVTable csvTable1 = new CSVTable(CDL.toString(jsonArray), "id");
        csvTable.merge(csvTable1);
        csvTable.writeToFile(file);
    }
}
