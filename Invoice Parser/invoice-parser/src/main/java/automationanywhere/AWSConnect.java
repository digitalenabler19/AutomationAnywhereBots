package automationanywhere;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.util.IOUtils;
import com.automationanywhere.core.security.SecureString;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;

public class AWSConnect {

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static AmazonTextractClientBuilder clientBuilder = AmazonTextractClientBuilder.standard().withRegion(Regions.US_WEST_1);

    public static String connectToAWS(String filename) throws JsonProcessingException, FileNotFoundException {

        clientBuilder.setCredentials(new AWSStaticCredentialsProvider(new
                BasicAWSCredentials("access key","secret key")));
        ByteBuffer buffer;

        try(InputStream in = new FileInputStream(new File(filename))){
            buffer = ByteBuffer.wrap(IOUtils.toByteArray(in));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AmazonTextract client =  clientBuilder.build();

        DetectDocumentTextRequest request = new DetectDocumentTextRequest()
                .withDocument(new Document()
                        .withBytes(buffer));

        DetectDocumentTextResult result = client.detectDocumentText(request);
        String y = result.toString();
        if(result!=null) {
            LOGGER.log(Level.INFO, "aws response avaiable");
        }
        return JsonUtils.toPrettyJsonString(result);




    }

}

