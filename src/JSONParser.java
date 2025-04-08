import java.util.HashMap;
import java.util.Map;

public class JSONParser {

    public static Map<String, String> JSONtoMap(String json) {

        Map<String, String> map = new HashMap<>();
        StringBuilder Entity = new StringBuilder();
        StringBuilder Value = new StringBuilder();
        boolean onValue = false;

        for (int point = 0; point < json.length(); point++) {

            char c = json.charAt(point);

            if (c == '}' || c == ',') {
                if (Entity.equals("")) {
                    continue;
                }
                map.put(Entity.toString(), Value.toString());
                Entity = new StringBuilder();
                Value = new StringBuilder();
                onValue = false;
                continue;
            }
            if (c == ':') {
                onValue = true;
                continue;
            }
            if (c == '{' || c == ' ' || c == '[' || c == ']' || c == '\"') {
                continue;
            }

            if (!onValue) {
                Entity.append(c);
            } else {
                Value.append(c);
            }
        }

        return map;
    }

    public static String UsertoJSON(User UserSample) {
        return "{\"id\": \"" + UserSample.getId() + "\",\"password\": \"" + UserSample.getPassword() + "\", \"balance\": \"" + UserSample.getBalance() + "\", \"username\": \"" + UserSample.username + "\"},";
    }
}