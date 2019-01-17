package gdou.laiminghai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScriptLoad {
    public static String load(String path){
        StringBuilder sb = new StringBuilder();
        InputStream is = ScriptLoad.class.getClassLoader().getResourceAsStream(path);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return sb.toString();
    }
}
