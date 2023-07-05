package automationanywhere;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.json.JSONArray;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import static automationanywhere.AWSConnect.connectToAWS;

public class TextractCoordinatesConverter {
    public static String textractOutput(String filename) throws IOException {

        String jsonBlockString = connectToAWS(filename);
        JSONObject jsonInput = new JSONObject(jsonBlockString);

        File file = new File(filename);
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        int pageHeight = 0;
        int pageWidth = 0;

        if (readers.hasNext()) {
            ImageReader reader = readers.next();
            reader.setInput(iis, true);
            pageWidth = reader.getWidth(0);
            pageHeight = reader.getHeight(0);
        }

        JSONArray blocks = jsonInput.getJSONArray("blocks");
        for (int i = blocks.length()-1; i >=0; i--) {
            JSONObject block = blocks.getJSONObject(i);
            if(block.getString("blockType").equals("PAGE")){
                blocks.remove(i);
            }else {
                convertBlockCoordinates(block, pageWidth, pageHeight);
            }

        }

        return jsonInput.toString();
    }

    public static void convertBlockCoordinates(JSONObject block, int pageWidth, int pageHeight) {
        double left = block.getJSONObject("geometry").getJSONObject("boundingBox").getDouble("left");
        double top = block.getJSONObject("geometry").getJSONObject("boundingBox").getDouble("top");
        double width = block.getJSONObject("geometry").getJSONObject("boundingBox").getDouble("width");
        double height = block.getJSONObject("geometry").getJSONObject("boundingBox").getDouble("height");

        int leftPixel = (int) (left * pageWidth);
        int topPixel = (int) (top * pageHeight);
        int rightPixel = (int) ((left + width) * pageWidth);
        int bottomPixel = (int) ((top + height) * pageHeight);

        String[] y = {};
        block.put("relationships",y);
        block.getJSONObject("geometry").remove("boundingBox");
        block.getJSONObject("geometry").put("left", leftPixel);
        block.getJSONObject("geometry").put("top", topPixel);
        block.getJSONObject("geometry").put("width", rightPixel );
        block.getJSONObject("geometry").put("height", bottomPixel);
        block.getJSONObject("geometry").remove("polygon");
        block.remove("rowSpan");
        block.remove("columnSpan");
        block.remove("selectionStatus");
        block.remove("query");
        block.remove("textType");
        block.remove("rowIndex");
        block.remove("columnIndex");
        block.remove("entityTypes");
        block.remove("page");
    }
}