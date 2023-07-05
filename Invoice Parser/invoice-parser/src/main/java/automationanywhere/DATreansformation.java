package automationanywhere;

import com.automationanywhere.core.security.SecureString;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static automationanywhere.TextractCoordinatesConverter.textractOutput;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DATreansformation {

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static String dAEngineJson(String filename) throws IOException {

//        String joltSpecs = new String(Files.readAllBytes(Paths.get()));
//        ClassLoader classLoader = DATreansformation.class.getClassLoader();
//        File file = new File(classLoader.resources("jsonSpec_original.json").getFile());
//        InputStream inputStream = new FileInputStream(file);
        String joltSpecs;

        byte[] data = null;
        try (InputStream in = DATreansformation.class.getResourceAsStream("/jsonSpec_original.json")) {
            if (in == null) {
                System.out.println("Resource '/Test' does not exist");
                System.exit(-1);
            }
             joltSpecs = new String(in.readAllBytes());
        }

        String srcJson = textractOutput(filename);

        List<Object> specs = JsonUtils.jsonToList(joltSpecs);

        Chainr chainr = Chainr.fromSpec(specs);

        Map<String,Object>inputJSON = JsonUtils.jsonToMap(srcJson);
//        Object inputJSON = JsonUtils.classpathToObject(srcJson);
        Object transformedOutput = chainr.transform(inputJSON);
        if(transformedOutput==null){
            LOGGER.log(Level.INFO, "Transformation failed");
        }
        return JsonUtils.toPrettyJsonString(transformedOutput);

    }
}
