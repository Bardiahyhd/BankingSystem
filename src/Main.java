import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        BankSystem system = new BankSystem("Database.json", "Log.txt");

        while (true) {
            System.out.print(BankSystem.Time() + " - " + "Enter your command(Signup/Login/Exit): ");
            String Command = scanner.nextLine();

            if (Command.equalsIgnoreCase("signup")) {
                String username;
                String password = "";
                while (true) {
                    System.out.print(BankSystem.Time() + " - " + "Enter your username(Username/Undo): ");
                    username = scanner.nextLine();
                    if (username.equalsIgnoreCase("undo")) {
                        break;
                    } else if (!BankSecurity.CharCheck(username)) {
                        System.out.println(BankSystem.Time() + " - " + "Username must contain at least one character.");
                    } else if (system.UserExist(username)) {
                        System.out.println(BankSystem.Time() + " - " + "This username already exists.");
                    } else {
                        while (true) {
                            System.out.print(BankSystem.Time() + " - " + "Enter your password(Password/Undo): ");
                            password = scanner.nextLine();
                            if (username.equalsIgnoreCase("undo")) {
                                break;
                            }
                            if (!BankSecurity.CharCheck(password)) {
                                System.out.println(BankSystem.Time() + " - " + "Password must contain at least one character.");
                            } else {
                                break;
                            }
                        }
                        if (!password.equalsIgnoreCase("undo")) {
                            break;
                        }
                    }
                }
                if (username.equalsIgnoreCase("undo")) {
                    continue;
                }

                User UserSample = new User(username, BankSecurity.Hash(password), 0.0, 60370001 + system.NumberofUsers());
                system.AddUser(UserSample);
                System.out.println(BankSystem.Time() + " - " + "Your account has been successfully created.");
            } else if (Command.equalsIgnoreCase("login")) {
                String username;
                String password = "";
                while (true) {
                    System.out.print(BankSystem.Time() + " - " + "Enter your username(Username/Undo): ");
                    username = scanner.nextLine();
                    if (username.equalsIgnoreCase("undo")) {
                        break;
                    } else if (!system.UserExist(username)) {
                        System.out.println(BankSystem.Time() + " - " + "This username does not exist.");
                    } else {
                        while (true) {
                            System.out.print(BankSystem.Time() + " - " + "Enter your password(Password/Undo): ");
                            password = scanner.nextLine();
                            if (username.equalsIgnoreCase("undo")) {
                                break;
                            } else {
                                if (system.PasswordMatch(username, password, false)) {
                                    break;
                                } else {
                                    System.out.println(BankSystem.Time() + " - " + "Incorrect Password.");
                                }
                            }
                        }
                        if (!password.equalsIgnoreCase("undo")) {
                            break;
                        }
                    }
                }
                if (username.equalsIgnoreCase("undo")) {
                    continue;
                }

                if (system.PasswordMatch(username, password, true)) {
                    System.out.println(BankSystem.Time() + " - " + "You have successfully logged into your account.");

                    while (true) {
                        System.out.print(BankSystem.Time() + " - " + "Enter your command(AccountNumber/CheckBalance/Deposit/Withdraw/Transfer/UndoTransfer/History/Logout): ");
                        String UserCommand = scanner.nextLine();

                        if (UserCommand.equalsIgnoreCase("CheckBalance")) {
                            System.out.println(BankSystem.Time() + " - " + "Your balance: " + system.BalanceFinder(username, true));
                        } else if (UserCommand.equalsIgnoreCase("Deposit")) {
                            String inp;
                            double value = 0.0;
                            while (true) {
                                System.out.print(BankSystem.Time() + " - " + "Specify the amount(Value/Undo): ");
                                inp = scanner.nextLine();
                                if (inp.equalsIgnoreCase("undo")) {
                                    break;
                                }
                                try {
                                    value = Double.parseDouble(inp);
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a number.");
                                    continue;
                                }
                                if (value < 0.0) {
                                    System.out.println(BankSystem.Time() + " - " + "Your transaction value can not be negative.");
                                    continue;
                                }
                                break;
                            }
                            if (inp.equalsIgnoreCase("undo")) {
                                continue;
                            }
                            system.Deposit(username, value);
                            System.out.println(BankSystem.Time() + " - " + "Transaction completed");
                        } else if (UserCommand.equalsIgnoreCase("Withdraw")) {
                            String inp;
                            double value = 0.0;
                            while (true) {
                                System.out.print(BankSystem.Time() + " - " + "Specify the amount(Value/Undo): ");
                                inp = scanner.nextLine();
                                if (inp.equalsIgnoreCase("undo")) {
                                    break;
                                }
                                try {
                                    value = Double.parseDouble(inp);
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a number.");
                                    continue;
                                }
                                if (value < 0.0) {
                                    System.out.println(BankSystem.Time() + " - " + "Your transaction value can not be negative.");
                                    continue;
                                }
                                if (system.BalanceFinder(username, false) < value) {
                                    System.out.println(BankSystem.Time() + " - " + "Low balance.");
                                    continue;
                                }
                                break;
                            }
                            if (inp.equalsIgnoreCase("undo")) {
                                continue;
                            }
                            system.Withdraw(username, value);
                            System.out.println(BankSystem.Time() + " - " + "Transaction completed");
                        } else if (UserCommand.equalsIgnoreCase("Transfer")) {
                            String inp;
                            String inp2;
                            int id = 0;
                            double value = 0.0;
                            while (true) {
                                System.out.print(BankSystem.Time() + " - " + "Enter the account number that you want to transfer money to(ID/Undo): ");
                                inp2 = scanner.nextLine();
                                if (inp2.equalsIgnoreCase("undo")) {
                                    break;
                                }
                                id = 0;
                                try {
                                    id = (int) Double.parseDouble(inp2);
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a number.");
                                    continue;
                                }
                                if (id == system.IdFinder(username, false)) {
                                    System.out.println(BankSystem.Time() + " - " + "You can not wire money to yourself.");
                                } else if (!system.UserExist((id))) {
                                    System.out.println(BankSystem.Time() + " - " + "This account number do not exist.");
                                } else {
                                    while (true) {
                                        System.out.print(BankSystem.Time() + " - " + "Specify the amount(Value/Undo): ");
                                        inp = scanner.nextLine();
                                        if (inp.equalsIgnoreCase("undo")) {
                                            break;
                                        }
                                        try {
                                            value = Double.parseDouble(inp);
                                        } catch (NumberFormatException e) {
                                            System.out.println("Please enter a number.");
                                            continue;
                                        }
                                        if (value < 0.0) {
                                            System.out.println(BankSystem.Time() + " - " + "Your transaction value can not be negative.");
                                            continue;
                                        }
                                        break;
                                    }
                                    if (!inp.equalsIgnoreCase("undo")) {
                                        break;
                                    }
                                }
                            }
                            if (inp2.equalsIgnoreCase("undo")) {
                                continue;
                            }

                            if (system.BalanceFinder(username, false) < value) {
                                System.out.println(BankSystem.Time() + " - " + "Low balance.");
                            } else {
                                system.Transition(username, id, value);
                                System.out.println(BankSystem.Time() + " - " + "Transaction with the id of " + system.LogCount() + " has been successfully done.");
                            }

                        } else if (UserCommand.equalsIgnoreCase("undotransfer")) {
                            String inp;
                            int id = 0;
                            while(true) {
                                System.out.print(BankSystem.Time() + " - " + "Enter the transactionID(ID/Undo): ");
                                 inp = scanner.nextLine();
                                if (inp.equalsIgnoreCase("undo")) {
                                    break;
                                }
                                try {
                                    id = (int) Double.parseDouble(inp);
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a number.");
                                    continue;
                                }
                                break;
                            }
                            if (inp.equalsIgnoreCase("undo")) {
                                continue;
                            }
                            system.UndoTransaction((int)id, username);
                        } else if (UserCommand.equalsIgnoreCase("history")) {
                            system.HistoryCheck(username);
                        } else if (UserCommand.equalsIgnoreCase("accountnumber")) {
                            System.out.println(BankSystem.Time() + " - " + "Your account number: " + system.IdFinder(username, true));
                        } else if (UserCommand.equalsIgnoreCase("Logout")) {
                            system.logout(username);
                            System.out.println(BankSystem.Time() + " - " + "See you next time.");
                            try {
                                Thread.sleep(300);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            break;
                        } else {
                            System.out.println(BankSystem.Time() + " - " + "Invalid command.");
                        }
                    }
                } else {
                    System.out.println(BankSystem.Time() + " - " + "Incorrect Password.");
                }
            } else if (Command.equalsIgnoreCase("exit")) {
                System.out.println(BankSystem.Time() + " - " + "Goodbye!");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;

            } else {
                System.out.println(BankSystem.Time() + " - " + "Invalid Command!");

            }
        }
        system.close();
    }
}
