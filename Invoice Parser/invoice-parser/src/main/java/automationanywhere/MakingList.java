package automationanywhere;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import static automationanywhere.DATreansformation.dAEngineJson;
public class MakingList {
    public static String reFormat(String filepath) throws Exception {

        String inputJson1 = dAEngineJson(filepath);
        JSONObject jsonInput = new JSONObject(inputJson1);

        JSONObject filepathOb = jsonInput.getJSONObject("metadata");
        filepathOb.put("filepath",filepath);

        JSONObject ocrFilepath = jsonInput.getJSONObject("ocrResult").getJSONObject("metadata").getJSONObject("pages");
        ocrFilepath.put("filepath",filepath);

        JSONObject imageFilepath = jsonInput.getJSONObject("imagePreprocessingResult").getJSONObject("metadata");
        imageFilepath.put("filepath",filepath);

        JSONObject imagePageFilepath = jsonInput.getJSONObject("imagePreprocessingResult").getJSONObject("pages");
        imagePageFilepath.put("filepath",filepath);

        JSONObject extractionFilepath = jsonInput.getJSONObject("extractionResult").getJSONObject("metadata");
        extractionFilepath.put("filepath",filepath);


        JSONObject pagesObject = jsonInput.getJSONObject("imagePreprocessingResult").getJSONObject("pages");

        JSONArray pagesArray = new JSONArray();
        pagesArray.put(pagesObject);

        JSONObject imagePreprocessingResultArray = jsonInput.getJSONObject("imagePreprocessingResult");
        imagePreprocessingResultArray.put("pages", pagesArray);

//        JSONObject relationship = jsonInput.getJSONObject("extractionResult").getJSONObject("addressFeatures").getJSONObject("relationships");
//
        JSONArray relationshipArray = new JSONArray();
        relationshipArray.put("CF70FFE2-B816-453A-9646-AD0F232F081C");

        JSONObject relationsShipResultArray = jsonInput.getJSONObject("extractionResult").getJSONObject("addressFeatures");
        relationsShipResultArray.put("relationships", relationshipArray);

//        JSONObject valueRelationship = jsonInput.getJSONObject("extractionResult").getJSONObject("keyValueFeatures").getJSONObject("value").getJSONObject("relationships");
        JSONArray keyArray = new JSONArray();
        keyArray.put("7C33EF23-BBAF-44B1-A7D4-2B81B0454E23");
        JSONObject valueRelationshipArray = jsonInput.getJSONObject("extractionResult").getJSONObject("keyValueFeatures").getJSONObject("value");
        valueRelationshipArray.put("relationships",keyArray);

        JSONArray valueArray = new JSONArray();
        valueArray.put("181205DC-D4BB-4C85-B619-AF01D48AF49E");
        JSONObject keyRelationshipArray = jsonInput.getJSONObject("extractionResult").getJSONObject("keyValueFeatures").getJSONObject("key");
        keyRelationshipArray.put("relationships",valueArray);


        JSONObject pagesObject1 = jsonInput.getJSONObject("ocrResult").getJSONObject("metadata").getJSONObject("pages");
        JSONArray pagesArray1 = new JSONArray();
        pagesArray1.put(pagesObject1);

        JSONObject ocrResultArray = jsonInput.getJSONObject("ocrResult").getJSONObject("metadata");
        ocrResultArray.put("pages", pagesArray1);


        JSONArray langCodesArray = new JSONArray();
        langCodesArray.put("eng");

        JSONObject executionStatusArray = jsonInput.getJSONObject("ocrResult").getJSONObject("metadata").getJSONObject("learningInstanceSetting");
        executionStatusArray.put("langCodes", langCodesArray);

        JSONObject addressFeatures = jsonInput.getJSONObject("extractionResult").getJSONObject("addressFeatures");
        JSONArray addressFeaturesArray = new JSONArray();
        addressFeaturesArray.put(addressFeatures);

        JSONObject keyValueFeatures = jsonInput.getJSONObject("extractionResult").getJSONObject("keyValueFeatures");
        JSONArray keyValueFeaturesArray = new JSONArray();
        keyValueFeaturesArray.put(keyValueFeatures);
        JSONObject exrtactResultArray = jsonInput.getJSONObject("extractionResult");
        exrtactResultArray.put("keyValueFeatures",keyValueFeaturesArray);
        exrtactResultArray.put("addressFeatures", addressFeaturesArray);




        JSONObject featureObject = jsonInput.getJSONObject("docDetectResult").getJSONObject("featureObjects");

        JSONArray featureArray = new JSONArray();
        featureArray.put(featureObject);

        JSONObject docDetectResultArray = jsonInput.getJSONObject("docDetectResult");
        docDetectResultArray.put("featureObjects", featureArray);

        return jsonInput.toString();
    }
}
