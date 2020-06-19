package Command;

import TCPServer.CollectionManager;
import Object.*;

import java.sql.*;
import java.util.ArrayList;

/**
 * добавить новый элемент с заданным ключом.
 */
public class Insert extends Command {
    private Integer key;
    ArrayList<String> login_and_password;
    Connection connection;
    public Insert(CollectionManager manager, Integer key, Connection connection, ArrayList<String> login_and_password) {
        super(manager);
        this.key = key;
        this.login_and_password = login_and_password;
        this.connection = connection;
        setDescription("добавить новый элемент с заданным ключом.");
    }

    @Override
    public String execute(Object args) {
        try {
            Flat flat = (Flat) args;
            if (getManager().getHouses().containsKey(key))
                return "Вы зачем такой ключ написали? Такой уже есть в коллекции...";
            PreparedStatement get_UserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
            get_UserId.setString(1, login_and_password.get(0));
            ResultSet resultSet = get_UserId.executeQuery();
            resultSet.next();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO flats VALUES(?, nextval('sequence_id'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, key);
            statement.setString(2,flat.getName());
            statement.setFloat(3, flat.getCoordinates().getX());
            statement.setInt(4, Math.toIntExact(flat.getCoordinates().getY()));
            statement.setTimestamp(5, Timestamp.valueOf(flat.getCreationDate()));
            statement.setInt(6, (int) flat.getArea());
            statement.setInt(7,flat.getNumberOfRooms());
            statement.setString(8,String.valueOf(flat.getFurnish()));
            statement.setString(9,String.valueOf(flat.getView()));
            statement.setString(10,String.valueOf(flat.getTransport()));
            statement.setString(11,flat.getHouse().getName());
            statement.setInt(12,flat.getHouse().getYear());
            statement.setInt(13,flat.getHouse().getNumberOfFloors());
            statement.setInt(14, (int) flat.getHouse().getNumberOfFlatsOnFloor());
            statement.setInt(15, resultSet.getInt("id"));
            statement.execute();
            statement = connection.prepareStatement("SELECT id FROM flats WHERE key = ?");
            statement.setInt(1, key);
            resultSet.close();
            resultSet = statement.executeQuery();
            resultSet.next();
            flat.setId(resultSet.getInt("id"));
            getManager().getHouses().put(key, flat);
            resultSet.close();
            return "Элемент добавлен.";
        } catch (SQLException e) {
                e.printStackTrace();
                return "Чё-то не получилось, чё-то не считалось... Сорян, ну чё";
        }
    }
}
