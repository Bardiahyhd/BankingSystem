import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class BankSystem {
    private final String DatabaseFilename;
    private String LogFilename;
    private ArrayList<User> Users = new ArrayList<>();

    public static String Time() {
        Date Clock = new Date();
        String hours = String.valueOf(Clock.getHours());
        String minutes = String.valueOf(Clock.getMinutes());
        String seconds = String.valueOf(Clock.getSeconds());

        if (hours.length() == 1) {
            hours = '0' + hours;
        }
        if (minutes.length() == 1) {
            minutes = '0' + minutes;
        }
        if (seconds.length() == 1) {
            seconds = '0' + seconds;
        }
        return hours + "." + minutes + "." + seconds;
    }


    BankSystem(String DatabaseFilename, String LogFilename) {
        this.DatabaseFilename = DatabaseFilename;
        this.LogFilename = LogFilename;
        log("System", "BankSystem is back online.");
        LoadUsers();
    }

    private void LoadUsers() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DatabaseFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() <= 2)
                    continue;
                Users.add(User.JSONtoUser(line));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public int NumberofUsers() {
        return Users.size();
    }

    public void UpdateUsers() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DatabaseFilename));
            writer.write("[\n");
            for (int i = 0; i < Users.size(); i++) {
                User UserSample = Users.get(i);
                String JSONString = JSONParser.UsertoJSON(UserSample);
                if (i == Users.size() - 1) {
                    JSONString = JSONString.substring(0, JSONString.length() - 1);
                }
                writer.write(JSONString + '\n');
            }
            writer.write("]");
            writer.close();
        } catch (IOException ioe) {
            System.out.println("Couldn't write to file");
        }
    }

    public boolean UserExist(String username) {
        for (User user : Users) {
            if (user.username.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean UserExist(int id) {
        for (User user : Users) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private User FindUser(String username) {
        for (User user : Users) {
            if (user.username.equalsIgnoreCase(username)) {
                return user;
            }
        }
        return new User("NoUser", "NoPass", 0.0, 0);
    }

    private User FindUser(int id) {
        for (User user : Users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return new User("NoUser", "NoPass", 0.0, 0);
    }

    public void AddUser(User UserSample) {
        log(UserSample.username, "A user called " + UserSample.username + " created an account with the account number of " + UserSample.getId() + ".");
        Users.add(UserSample);
        UpdateUsers();
    }

    public boolean PasswordMatch(String username, String password, boolean log) throws NoSuchAlgorithmException {
        if (FindUser(username).getPassword().equals(BankSecurity.Hash(password))) {
            if (log) {
                log(username, "User " + username + " entered their account.");
            }
            return true;
        }
        return false;
    }

    public int IdFinder(String username, boolean log) {
        int temp = FindUser(username).getId();
        if (log) {
            log(username, "User " + username + " checked their account number value which was " + temp + ".");
        }
        return temp;
    }

    public double BalanceFinder(String username, boolean log) {
        double temp = FindUser(username).getBalance();
        if (log) {
            log(username, "User " + username + " checked their balance which was " + temp + ".");
        }
        return temp;
    }

    private void UserBalanceChanger(String username, double value) {
        for (User user : Users) {
            if (user.username.equalsIgnoreCase(username)) {
                user.BalanceChange(value);
                break;
            }
        }
        UpdateUsers();
    }

    private void UserBalanceChanger(int id, double value) {
        for (User user : Users) {
            if (user.getId() == id) {
                user.BalanceChange(value);
                break;
            }
        }
        UpdateUsers();
    }

    public void Withdraw(String username, double value) {
        log(username, "User " + username + " withdrew " + value + " dollar from their account.");
        UserBalanceChanger(username, -value);
        UpdateUsers();
    }

    public void Deposit(String username, double value) {
        log(username, "User " + username + " deposited " + value + " dollar into their account.");
        UserBalanceChanger(username, value);
        UpdateUsers();
    }

    public void Transition(String username, int id, double value) {
        String transformusername = FindUser(id).username;
        log(username, transformusername, value, "User " + username + " transfered " + value + " dollar into " + transformusername + "'s account .");
        UserBalanceChanger(username, -value);
        UserBalanceChanger(id, value);
        UpdateUsers();
    }

    public int LogCount() {
        int cnt = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LogFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                cnt++;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return cnt;
    }

    private boolean TransactionCheck(double id) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LogFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                StringBuilder transactionid = new StringBuilder();
                int cnt = 0;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '~') {
                        cnt++;
                        continue;
                    }
                    if (cnt == 3) {
                        transactionid.append(line.charAt(i));
                    }
                }
                if (String.valueOf(-id).equalsIgnoreCase(String.valueOf(transactionid))) {
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

    public void UndoTransaction(int id, String username) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LogFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                StringBuilder usernameCheck = new StringBuilder();
                StringBuilder usernameCheck2 = new StringBuilder();
                StringBuilder transactionid = new StringBuilder();;
                StringBuilder valuestring = new StringBuilder();
                int cnt = 0;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '~') {
                        cnt++;
                        continue;
                    }
                    if (cnt == 0) {
                        usernameCheck.append(line.charAt(i));
                    }
                    if (cnt == 2) {
                        usernameCheck2.append(line.charAt(i));
                    }
                    if (cnt == 1) {
                        transactionid.append(line.charAt(i));
                    }
                    if (cnt == 3) {
                        valuestring.append(line.charAt(i));
                    }
                }
                if (username.equalsIgnoreCase(String.valueOf(usernameCheck)) && String.valueOf(id).equalsIgnoreCase(String.valueOf(transactionid))) {
                    double value = 0.0;
                    try {
                        value = (int) Double.parseDouble(String.valueOf(valuestring));
                    } catch (NumberFormatException e) {
                        System.out.println("Transaction failed.");
                        return;
                    }
                    if(TransactionCheck(id)) {
                        System.out.println("This undotransaction has been done before.");
                        return;
                    }
                    UserBalanceChanger(username, value);
                    UserBalanceChanger(String.valueOf(usernameCheck2), -value);
                    UpdateUsers();
                    log(username, String.valueOf(usernameCheck2), -id, "User " + username + " transaction with the value of " + value + " dollars into " + String.valueOf(usernameCheck2) + "'s account has beed canceled with the transaction id of "+id+".");
                    System.out.println("UndoTransaction has been successfully done.");
                    return;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("You do not have a transaction with this id.");
    }

    public void HistoryCheck(String username) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LogFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                StringBuilder usernameCheck = new StringBuilder();
                StringBuilder usernameCheck2 = new StringBuilder();
                int cnt = 0;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '~') {
                        cnt++;
                        continue;
                    }
                    if (cnt == 0) {
                        usernameCheck.append(line.charAt(i));
                    }
                    if (cnt == 2) {
                        usernameCheck2.append(line.charAt(i));
                    }
                }
                if (username.equalsIgnoreCase(String.valueOf(usernameCheck)) || username.equalsIgnoreCase(String.valueOf(usernameCheck2))) {
                    System.out.println(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        log(username, "User " + username + " checked their history.");
    }

    public void monitor() {
        for (User user : Users) {
            System.out.print(user.username + " " + user.getBalance() + "\n");
        }
    }

    public void logout(String username) {
        log(username, "user " + username + " logged out.");
    }

    public void close() {
        log("System", "BankSystem is offline.");
    }

    private void log(String username, String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(LogFilename, true));
            writer.write(username + "~" + (LogCount() + 1) + "~ " + new Date() + " - " + message + "\n");
            writer.close();
        } catch (IOException ioe) {
            System.out.println("Couldn't write to file");
        }
    }

    private void log(String username, String username2, double value, String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(LogFilename, true));
            writer.write(username + "~" + (LogCount() + 1) + "~" + username2 + "~" + String.valueOf(value) + "~ " + new Date() + " - " + message + "\n");
            writer.close();
        } catch (IOException ioe) {
            System.out.println("Couldn't write to file");
        }
    }
}