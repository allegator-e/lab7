package TCPServer;

import Object.*;
import java.sql.*;
import java.util.Date;
import java.util.logging.*;
import java.time.*;
import java.util.*;

public class CollectionManager {
    private final TreeMap<Integer, Flat> houses = new TreeMap<>();
    private Connection connection;
    private Date initDate;
    static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(CollectionManager.class.getName());
    }

    public CollectionManager(Connection connection)  {
        this.connection = connection;
        this.load();
        this.initDate = new Date();
    }

    public TreeMap<Integer, Flat> getHouses(){
        return houses;
    }


    /**
     *  Полуение элементов коллекции из БД в локальную коллекцию.
     */
    public void load() {
        int beginSize = houses.size();
        LOGGER.log(Level.INFO, "Идёт загрузка коллекции ");
        try  {
            //System.out.println(stat.execute("DELETE FROM users *"));
            //System.out.println(stat.execute("CREATE SEQUENCE sequence_id"));
            //System.out.println(stat.execute("DROP TABLE flats"));
            //System.out.println(stat.execute("CREATE TABLE flats (key INT UNIQUE , id INT PRIMARY KEY, name VARCHAR(256) NOT NULL, Coordinates_x FLOAT, Coordinates_y INT, creationDate TIMESTAMP, numberOfRooms INT, furnish VARCHAR(10), view VARCHAR(10), transport VARCHAR(10) NOT NULL, House_name VARCHAR(256) NOT NULL, House_year INT, House_numberOfFloors INT, House_numberOfFlatsOnFloor INT, user_id INT REFERENCES users (id))"));
            //System.out.println(stat.execute("INSERT INTO users VALUES(nextval('sequence_user_id'), 'Luna', '1234', 'ophtj')"));
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM flats");
            while (resultSet.next()) {
                Integer key = resultSet.getInt("key");
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                float x = resultSet.getFloat("Coordinates_x");
                long y = resultSet.getInt("Coordinates_y");
                LocalDateTime creationDate = resultSet.getTimestamp("creationDate").toLocalDateTime();
                long area = resultSet.getInt("area");
                Integer numberOfRooms = resultSet.getInt("numberOfRooms");
                Furnish furnish = null;
                String furnish_s = resultSet.getString("furnish");
                if (!furnish_s.equals("null")) furnish = Furnish.valueOf(furnish_s);
                View view = null;
                String view_s = resultSet.getString("view");
                if (!view_s.equals("null")) view = View.valueOf(view_s);
                Transport transport = Transport.valueOf(resultSet.getString("transport"));
                String nameHouse = resultSet.getString("House_name");
                int year = resultSet.getInt("House_year");
                int numberOfFloors = resultSet.getInt("House_numberOfFloors");
                long numberOfFlatsOnFloor = resultSet.getInt("House_numberOfFlatsOnFloor");
                houses.put(key, new Flat(id, name, new Coordinates(x, y), creationDate, area, numberOfRooms, furnish, view, transport, new House(nameHouse, year, numberOfFloors, numberOfFlatsOnFloor)));
            }
            resultSet.close();
            LOGGER.log(Level.INFO, "Коллекция успешно загружена. Добавлено " + (houses.size() - beginSize) + " элементов.");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Коллекция не может быть загружена. Файл некорректен");
            System.exit(1);
        }
    }

    /**
     * Выводит информацию о коллекции.
     */
    @Override
    public String toString() {
        return "Тип коллекции: " + houses.getClass() +
                "\nДата инициализации: " + initDate +
                "\nКоличество элементов: " + houses.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionManager)) return false;
        CollectionManager manager = (CollectionManager) o;
        return houses.equals(manager.houses) &&
                initDate.equals(manager.initDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houses, initDate);
    }
}