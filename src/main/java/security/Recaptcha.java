
package security;

import java.io.IOException;
import utils.HttpUtils;

public class Recaptcha {
    
    public static boolean validateHuman(String retoken) throws IOException{
        boolean isVerified = false;
        String secret = "6LfliNgaAAAAAGgsgBd7tHREZC8b2pewXlBsVkT1" ;
        String url = String.format("https://www.google.com/recaptcha/api/siteverify?secret=%1$s&response=%2$s", secret, retoken);
        String response = HttpUtils.postData(url);
        System.out.println("---------" + response);
        return isVerified;
    }
    
}
