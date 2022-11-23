package kumoh.util.txt;


import kumoh.service.RSAService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.LinkedHashMap;


@Component
@RequiredArgsConstructor
public class MappingHashMap {

    private final RSAService rsaService;
    public LinkedHashMap<String, String> readHashMap(File file) throws IOException {

        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while ((line = br.readLine()) != null) {
                String decryption = rsaService.decryptionString(line);
                if (decryption == null){
                    break;
                }
                String[] parts = decryption.split(":", 2);
                if (parts.length >= 2) {
                    String key = parts[0];
                    String value = parts[1];
                    map.put(key, value);
                } else {
                    System.out.println("ignore line : " + line);
                }
            }
        }
        return map;
    }

    public void saveHashMap(String key, String value, File file) throws IOException {


        String line = key + ":" + value;
        String encrypt = rsaService.encryptingString(line);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {

            bw.write(encrypt);
            bw.newLine();
        }
    }

    public boolean updateHashMap(String key, String value, File file) throws IOException {

        LinkedHashMap<String, String> savedData = readHashMap(file);

        if (!savedData.containsKey(key)) {
            savedData.put(key, value);
        }
        else {
            savedData.replace(key,value);
        }

        rewrite(savedData, file);

        return true;
    }

    public void deleteHashMap(String key, File file) throws IOException {

        LinkedHashMap<String, String> savedData = readHashMap(file);

        if (!savedData.containsKey(key)) {
            return;
        }
        else {
            savedData.remove(key);
        }

        rewrite(savedData, file);

    }

    private void rewrite(LinkedHashMap<String, String> savedData ,File file) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))){
            savedData.forEach((strKey, strValue) -> {
                try {
                    String encryptData = rsaService.encryptingString(strKey + ":" + strValue);
                    bw.write(encryptData);
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException("updateHashMap RuntimeException");
                }
            });
        }
    }
}
