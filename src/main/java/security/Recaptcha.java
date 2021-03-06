
package security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.RecapDTO;
import java.io.IOException;
import utils.HttpUtils;

public class Recaptcha {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static boolean validateHuman(String retoken) throws IOException{
        boolean isVerified = false;
        String secret = "6LfiyusaAAAAADKkO8-qPX_Go3FIbCFQVD8ZZINl" ;
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=6LfiyusaAAAAADKkO8-qPX_Go3FIbCFQVD8ZZINl&response=" + retoken;
        String response = HttpUtils.postData(url);
        RecapDTO recapDTO = GSON.fromJson(response, RecapDTO.class);
        System.out.println(recapDTO.success);
        if(recapDTO.success.equals("true")){
            isVerified = true;
        }
       
        return isVerified;
    }
    
}
