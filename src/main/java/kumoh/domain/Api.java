package kumoh.domain;

import kumoh.util.json.request.ApiRequest;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import java.util.InputMismatchException;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Api {
    private Integer id;
    private String tableName;
    private String api;
    private Boolean isValidation;

    private static final ApiRequest apiRequest = new ApiRequest();

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id:::");
        stringBuilder.append(id);
        stringBuilder.append("----");
        stringBuilder.append("tableName:::");
        stringBuilder.append(tableName);
        stringBuilder.append("----");
        stringBuilder.append("api:::");
        stringBuilder.append(api);
        stringBuilder.append("----");
        stringBuilder.append("isValidation:::");
        stringBuilder.append(isValidation);
        return stringBuilder.toString();
    }

    public static Api parseApi(String api) throws InputMismatchException {
        Api api1 = new Api();

        String[] data = api.split("----");

        if(data.length != 4){
            throw new InputMismatchException();
        }

        String[] idData = data[0].split(":::");
        if(idData.length != 2){
            throw new InputMismatchException();
        }

        String[] tableNameData = data[1].split(":::");
        if(tableNameData.length != 2){
            throw new InputMismatchException();

        }

        String[] apiData = data[2].split(":::");
        if(apiData.length != 2){
            throw new InputMismatchException();
        }

        String[] isValidationData = data[3].split(":::");
        if(isValidationData.length != 2){
            throw new InputMismatchException();
        }

        api1.setId(Integer.parseInt(idData[1]));
        api1.setApi(apiData[1]);
        api1.setTableName(tableNameData[1]);
        api1.setIsValidation(Boolean.parseBoolean(isValidationData[1]));
        return api1;
    }

    public Api setValidation() {
        try{
            HttpStatus apiStatus = apiRequest.getHttpValidate(api);
            boolean isFirstAPIStatus = apiStatus != null && HttpStatus.OK.value() == apiStatus.value();

            setIsValidation(isFirstAPIStatus);
        }catch (IllegalArgumentException | HttpClientErrorException e) {
            setIsValidation(false);
        }
        return this;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Api api1 = (Api) o;
        return Objects.equals(id, api1.id) && Objects.equals(tableName, api1.tableName) && Objects.equals(api, api1.api) && Objects.equals(isValidation, api1.isValidation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tableName, api, isValidation);
    }
}
