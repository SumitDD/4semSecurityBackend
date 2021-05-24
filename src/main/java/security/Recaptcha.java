
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
        String secret = "6LfPttgaAAAAAD-rq4dfmTAG-hoEWSBA4uSf5EHK" ;
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=6LfliNgaAAAAANykS4dsxm7vEhSoUdAz1q8C2UpS&response=" + retoken;
        String response = HttpUtils.postData(url);
        RecapDTO recapDTO = GSON.fromJson(response, RecapDTO.class);
        System.out.println(recapDTO.success);
        if(recapDTO.success.equals("true")){
            isVerified = true;
        }
       
        return isVerified;
    }
    
}
