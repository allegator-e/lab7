package TCPServer;

import org.postgresql.util.PSQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerAutorization {
    private Logger LOGGER = Logger.getLogger(ServerAutorization.class.getName());
    private boolean access;
    private ArrayList<String> loginAndPassword;
    private String command;
    private String answer;
    private InteractionBD interactionBD;

    ServerAutorization(ArrayList<String> loginAndPassword, String command, InteractionBD interactionBD) {
        this.loginAndPassword = loginAndPassword;
        this.command = command;
        this.interactionBD = interactionBD;
    }

    public boolean access() {
        try {
            if (command.equals("reg")) {
                answer = interactionBD.registration(loginAndPassword.get(0), loginAndPassword.get(1));
            } else {
                try {
                    access = interactionBD.enter(loginAndPassword.get(0), loginAndPassword.get(1));
                    if (access) answer = "Доступ разрешен";
                    else answer = "Неверный логин/пароль. Попробуйте снова";
                } catch (PSQLException ex) {
                    answer = "Неверный логин/пароль. Попробуйте снова";
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Этого не должно было произойти. Обратитесь к разработчикам...(ненадо, пожалуйста)");
        }
        return access;
    }

    public String getAnswer() {
        return answer;
    }
}
