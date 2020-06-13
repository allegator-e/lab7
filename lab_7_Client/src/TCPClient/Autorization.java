package TCPClient;

import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

public class Autorization {

    Console console = System.console();
    boolean access = false;
    String hostName;
    int port;
    ArrayList<String> login_and_password = new ArrayList<>();

    Autorization(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public ArrayList<String> access() throws IOException {
        System.out.print("Для регистрации введите reg, для входа - что-нибудь: ");
        String[] reg = console.readLine().trim().split(" ");
        String login, passwd;
        while (!access) {
            login_and_password.clear();
            System.out.print("Логин: ");
            login = console.readLine();
            System.out.print("Пароль: ");
            passwd = "" + new String(console.readPassword());
            login_and_password.add(login);
            login_and_password.add(passwd);
            TCPSender sender = new TCPSender(hostName, port, access, login_and_password);
            sender.checker(reg[0].equals("reg") ? reg : new String[]{"logIn"});
            access = sender.isAccess();
        }
        return login_and_password;
    }
}
