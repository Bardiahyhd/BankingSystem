import java.util.Map;

public class User {
    String username;
    private String password;
    private double balance;
    private int id;

    public User(String username, String password, Double balance, int id) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public int getId() {
        return id;
    }

    public void BalanceChange(double value) {
        balance += value;
    }

    public static User JSONtoUser(String json) {
        Map<String, String> map = JSONParser.JSONtoMap(json);
        String username = map.get("username");
        String password = map.get("password");
        double balance = Double.parseDouble(map.get("balance"));
        double id = Double.parseDouble(map.get("id"));
        return new User(username, password, balance, (int) id);
    }
}