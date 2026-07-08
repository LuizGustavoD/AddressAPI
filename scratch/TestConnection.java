import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Starting connection test to http://auth-server:8080/.well-known/jwks.json");
            URL url = new URL("http://auth-server:8080/.well-known/jwks.json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            
            int status = con.getResponseCode();
            System.out.println("Response Status: " + status);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            
            System.out.println("Response Content: " + content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
