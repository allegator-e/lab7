package TCPClient;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;

public class Autorization {

    private Console console = System.console();
    private boolean access = false;
    private String hostName;
    private int port;
    private ArrayList<String> loginAndPassword = new ArrayList<>();

    Autorization(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public ArrayList<String> access() throws IOException {
        String[] reg;
        String login, passwd;
        while (!access) {
            System.out.print("Для регистрации введите reg, для входа - что-нибудь: ");
            reg = console.readLine().trim().split(" ");
            loginAndPassword.clear();
            System.out.print("Логин: ");
            login = console.readLine();
            if (login.equals("")) {
                System.out.println("Логин не корректный...");
                continue;
            }
            System.out.print("Пароль: ");
            passwd = "" + new String(console.readPassword());
            loginAndPassword.add(login);
            loginAndPassword.add(passwd);
            TCPSender sender = new TCPSender(hostName, port, access, loginAndPassword);
            sender.checker(reg[0].equals("reg") ? reg : new String[]{"logIn"});
            access = sender.isAccess();
        }
        return loginAndPassword;
    }
}
