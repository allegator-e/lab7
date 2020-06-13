package TCPServer;

import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.util.PSQLException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class ServerAutorization {
    private boolean access;
    private ArrayList<String> login_and_password;
    private String command;
    private Connection connection;
    private String answer;
    private MessageDigest md = MessageDigest.getInstance("SHA-224");

    ServerAutorization(Connection connection, ArrayList<String> login_and_password, String command) throws NoSuchAlgorithmException {
        this.login_and_password = login_and_password;
        this.command = command;
        this.connection = connection;
    }

    public boolean access() throws SQLException {
        ResultSet rs;

        String pepper = "*63&^mVLC(#";
        if (command.equals("reg")) {
            byte[] array = new byte[10];
            new Random().nextBytes(array);
            String salt = new String(array, StandardCharsets.UTF_8);
            byte[] hash = md.digest((pepper + login_and_password.get(1) + salt).getBytes());
            String password_plus = new String(hash, StandardCharsets.UTF_8);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            statement.setString(1, login_and_password.get(0));
            rs = statement.executeQuery();
            if (!rs.next()) {
                statement = connection.prepareStatement("INSERT INTO users VALUES(nextval('sequence_user_id'), ?, ?, ?)");
                statement.setString(1, login_and_password.get(0));
                statement.setString(2, password_plus);
                statement.setString(3, salt);
                statement.execute();
                access = true;
                answer = "Вы успешно зарегестрировались! Выполнен вход в систему";
            } else answer = "Пользователь с таким логином уже существует. Введите данные снова:";
        } else {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT id, salt, password FROM users WHERE login = ?");
                statement.setString(1, login_and_password.get(0));
                rs = statement.executeQuery();
                rs.next();
                System.out.println(rs.getInt("id"));
                byte[] hash = md.digest((pepper + login_and_password.get(1) + rs.getString("salt")).getBytes());
                String password_plus = new String(hash, StandardCharsets.UTF_8);
                access = password_plus.equals(rs.getString("password"));
                if (access)  answer = "Доступ разрешен";
                else  answer = "Неверный логин/пароль. Попробуйте снова";
            } catch (PSQLException ex) {
                answer = "Неверный логин/пароль. Попробуйте снова";
            }
        }
        return access;
    }

    public String getAnswer() {
        return answer;
    }
}
