package automationanywhere;

import com.automationanywhere.core.security.SecureString;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {

        ExtractionCommand ec = new ExtractionCommand();
//         ec.extract("C:\\Users\\Administrator\\Downloads\\Syed's test\\Date_page-0001.jpg",new SecureString("JTPnaephGbIdhF46PAI5Sm7enpX4ZQ9VjTqqeVRz".getBytes()));
//        System.out.println(AWSConnect.connectToAWS("C:\\Users\\Administrator\\Downloads\\Syed's test\\Date_page-0001.jpg"));

        System.out.println(ec.compute("C:\\Users\\Administrator\\Downloads\\Syed's test\\image_new.png"));

    }


}
