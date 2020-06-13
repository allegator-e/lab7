package Command;

import Object.Flat;
import TCPServer.CollectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * обновить значение элемента коллекции, id которого равен заданному.
 */
public class Update extends Command{
    private Integer id;
    ArrayList<String> login_and_password;
    Connection connection;
    public Update(CollectionManager manager, Integer id, Connection connection, ArrayList<String> login_and_password) {
        super(manager);
        this.id = id;
        this.login_and_password = login_and_password;
        this.connection = connection;
        setDescription("обновить значение элемента коллекции, id которого равен заданному.");
    }

    @Override
    public synchronized String execute(Object args) {
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        Flat flat = (Flat) args;
        if (houses.size() != 0) {
                if (houses.keySet().parallelStream().anyMatch(key -> houses.get(key).getId().equals(id))) {
                    try {
                        PreparedStatement get_UserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
                        get_UserId.setString(1, login_and_password.get(0));
                        ResultSet resultSet = get_UserId.executeQuery();
                        resultSet.next();
                        int user_id = resultSet.getInt("id");
                        PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM flats WHERE id = ?");
                        statement.setInt(1, id);
                        resultSet = statement.executeQuery();
                        resultSet.next();
                        if (user_id == resultSet.getInt("user_id")) {
                            statement = connection.prepareStatement("UPDATE flats SET name = ?, Coordinates_x = ?, Coordinates_y = ?, area = ?, numberOfRooms = ?, furnish = ?, view = ?, transport = ?, House_name = ?, House_year = ?, House_numberOfFloors = ?, House_numberOfFlatsOnFloor = ? WHERE id = ?");
                            statement.setString(1, flat.getName());
                            statement.setFloat(2,flat.getCoordinates().getX());
                            statement.setInt(3, Math.toIntExact(flat.getCoordinates().getY()));
                            statement.setInt(4, (int) flat.getArea());
                            statement.setInt(5,flat.getNumberOfRooms());
                            statement.setString(6,String.valueOf(flat.getFurnish()));
                            statement.setString(7,String.valueOf(flat.getView()));
                            statement.setString(8,String.valueOf(flat.getTransport()));
                            statement.setString(9,flat.getHouse().getName());
                            statement.setInt(10,flat.getHouse().getYear());
                            statement.setInt(11,flat.getHouse().getNumberOfFloors());
                            statement.setInt(12, (int) flat.getHouse().getNumberOfFlatsOnFloor());
                            statement.setInt(13,id);
                            statement.execute();
                            flat.setId(id);
                            houses.keySet().parallelStream()
                                    .filter(key -> houses.get(key).getId().equals(id))
                                    .forEach(key -> houses.replace(key, flat));

                            return "Элемент коллекции успешно обновлен.";
                        } else return "Элемент не принадлежит вам! Фу как не культурно изменять объекты других!!";
                    }catch (SQLException e) {
                        return("В коллекции не найдено элемента с указанным id.");
                    }
                }
                return("В коллекции не найдено вашей хаты с таким ай-ди.");
        } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }
}