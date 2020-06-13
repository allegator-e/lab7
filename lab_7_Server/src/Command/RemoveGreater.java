package Command;

import Object.*;
import TCPServer.CollectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * удалить из коллекции все элементы, превышающие заданный.
 */
public class RemoveGreater extends Command{

    private ArrayList<String> login_and_password;
    private Connection connection;

    public RemoveGreater(CollectionManager manager, Connection connection, java.util.ArrayList<String> login_and_password) {
        super(manager);
        this.login_and_password = login_and_password;
        this.connection = connection;
        setDescription("удалить из коллекции все элементы, превышающие заданный.");
    }

    @Override
    public synchronized String execute(Object args) {
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        Flat flat = (Flat) args;
        if (houses.size() != 0) {
            if (houses.keySet().parallelStream().anyMatch(key -> houses.get(key).compareTo(flat) > 0)) {
                try {
                    PreparedStatement get_UserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
                    get_UserId.setString(1, login_and_password.get(0));
                    ResultSet resultSet = get_UserId.executeQuery();
                    resultSet.next();
                    int user_id = resultSet.getInt("id");


                houses.keySet().parallelStream()
                        .filter(key -> houses.get(key).compareTo(flat) > 0)
                        .peek(key -> {
                            try {
                                PreparedStatement statement1 = connection.prepareStatement("DELETE FROM flats WHERE key = ? AND user_id = ?");
                                statement1.setInt(1,key);
                                statement1.setInt(2, user_id);
                                statement1.execute();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        })
                        .filter(key -> {
                            try {
                                PreparedStatement finalStatement = connection.prepareStatement("SELECT * FROM flats WHERE key = ?");
                                finalStatement.setInt(1,key);
                                ResultSet rs = finalStatement.executeQuery();
                                return !rs.next();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return true;
                            }
                        })
                        .collect(Collectors.toSet())
                        .forEach(houses::remove);
                return "Команда успешно выполнена.";
                }catch (SQLException e) {
                    e.printStackTrace();
                    return("В коллекции не найдено ваших усадьб с этими ключами.");
                }
            }
            return("В коллекции не найдено ваших усадьб с этими ключами.");
        } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }
}
