package Command;

import Object.Flat;
import TCPServer.CollectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * удалить из коллекции все элементы, ключ которых превышает заданный.
 */
public class RemoveGreaterKey extends Command {
    private ArrayList<String> login_and_password;
    private Connection connection;

    public RemoveGreaterKey(CollectionManager manager, Connection connection, java.util.ArrayList<String> login_and_password) {
        super(manager);
        this.login_and_password = login_and_password;
        this.connection = connection;
        setDescription("удалить из коллекции все элементы, ключ которых превышает заданный.");
    }

    @Override
    public String execute(Object args) {
        Integer key = (Integer)args;
        synchronized (getManager().getHouses()) {
            if (getManager().getHouses().size() != 0) {
                if (getManager().getHouses().keySet().stream().anyMatch(key_in_collection -> key_in_collection.compareTo(key) > 0)) {
                    try {
                        PreparedStatement get_UserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
                        get_UserId.setString(1, login_and_password.get(0));
                        ResultSet resultSet = get_UserId.executeQuery();
                        resultSet.next();
                        int user_id = resultSet.getInt("id");
                        PreparedStatement statement = connection.prepareStatement("DELETE FROM flats WHERE key > ? AND user_id = ?");
                        statement.setInt(1, key);
                        statement.setInt(2, user_id);
                        statement.execute();
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
                        return "Команда успешно выполнена.";
                    } catch (SQLException e) {
                        return ("В коллекции не найдено ваших хором с этими ключами.");
                    }
                }
                return ("В коллекции не найдено ваших хором с этими ключами.");
            } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
        }
    }
}
