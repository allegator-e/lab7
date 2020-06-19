package Command;

import TCPServer.CollectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;
import Object.*;

/**
 *  Очистить коллецию.
 */
public class Clear extends Command{
    private ArrayList<String> login_and_password;
    private Connection connection;

    public Clear(CollectionManager manager, Connection connection, java.util.ArrayList<String> login_and_password) {
        super(manager);
        this.login_and_password = login_and_password;
        this.connection = connection;
        setDescription("Очистить коллецию.");
    }

    @Override
    public String execute(Object args) {
        try {
            PreparedStatement get_UserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
            get_UserId.setString(1, login_and_password.get(0));
            ResultSet resultSet = get_UserId.executeQuery();
            resultSet.next();
            int user_id = resultSet.getInt("id");
            PreparedStatement statement = connection.prepareStatement("DELETE FROM flats WHERE user_id = ?");
            statement.setInt(1, user_id);
            statement.execute();
            synchronized (getManager().getHouses()) {
                getManager().getHouses().keySet().stream()
                        .filter(key_in -> {
                            try {
                                PreparedStatement finalStatement = connection.prepareStatement("SELECT * FROM flats WHERE key = ?");
                                finalStatement.setInt(1, key_in);
                                ResultSet rs = finalStatement.executeQuery();
                                return !rs.next();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return true;
                            }
                        })
                        .collect(Collectors.toSet())
                        .forEach(getManager().getHouses()::remove);
                return "Команда успешно выполнена. ";
            }
        }catch (SQLException e) {
            return("В коллекции не найдено вашей недвижимости.");
        }
    }
}
