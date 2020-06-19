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
 * удалить элемент из коллекции по его ключу.
 */
public class RemoveKey extends Command {
    private ArrayList<String> login_and_password;
    private Connection connection;

    public RemoveKey(CollectionManager manager, Connection connection, ArrayList<String> login_and_password) {
        super(manager);
        this.login_and_password = login_and_password;
        this.connection = connection;
        setDescription("удалить элемент из коллекции по его ключу.");
    }

    @Override
    public String execute(Object args) {

        Integer key = (Integer)args;
        synchronized (getManager().getHouses()) {
            if (getManager().getHouses().size() != 0) {
                if (getManager().getHouses().keySet().stream().anyMatch(key_in_collection -> key_in_collection.equals(key))) {
                    try {
                        PreparedStatement get_UserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
                        get_UserId.setString(1, login_and_password.get(0));
                        ResultSet resultSet = get_UserId.executeQuery();
                        resultSet.next();
                        int user_id = resultSet.getInt("id");
                        PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM flats WHERE key = ?");
                        statement.setInt(1, key);
                        resultSet = statement.executeQuery();
                        resultSet.next();
                        if (user_id == resultSet.getInt("user_id")) {
                            statement = connection.prepareStatement("DELETE FROM flats WHERE key = ?");
                            statement.setInt(1, key);
                            statement.execute();
                            getManager().getHouses().keySet().stream()
                                    .filter(key_in_collection -> key_in_collection.equals(key))
                                    .collect(Collectors.toSet())
                                    .forEach(getManager().getHouses()::remove);
                            return "Команда успешно выполнена.";
                        } else return "Элемент не принадлежит вам! Фу как не культурно изменять объекты других!!";
                    } catch (SQLException e) {
                        return ("В коллекции не найдено ваших избушек с такими ключами.");
                    }
                }
                return ("В коллекции не найдено ваших избушек с такими ключами.");
            } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
        }
    }
}
