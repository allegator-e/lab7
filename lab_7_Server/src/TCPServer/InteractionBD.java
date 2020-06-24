package TCPServer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import Object.*;

public class InteractionBD {
    private TreeMap<Integer, Flat> houses = new TreeMap<>();
    private Logger LOGGER = Logger.getLogger(InteractionBD.class.getName());
    private final Connection connection;
    private MessageDigest md = null;
    private String pepper = "*63&^mVLC(#";

    public InteractionBD(Connection connection) {
        this.connection = connection;
    }

    {
        try {
            md = MessageDigest.getInstance("SHA-224");
        } catch (NoSuchAlgorithmException e) {
            //Nothing
        }
    }

    /**
     * Получение коллекции из БД
     *
     */

     public TreeMap<Integer, Flat> load() {
        try {
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
                String furnishS = resultSet.getString("furnish");
                if (!furnishS.equals("null")) furnish = Furnish.valueOf(furnishS);
                View view = null;
                String viewS = resultSet.getString("view");
                if (!viewS.equals("null")) view = View.valueOf(viewS);
                Transport transport = Transport.valueOf(resultSet.getString("transport"));
                String nameHouse = resultSet.getString("House_name");
                int year = resultSet.getInt("House_year");
                int numberOfFloors = resultSet.getInt("House_numberOfFloors");
                long numberOfFlatsOnFloor = resultSet.getInt("House_numberOfFlatsOnFloor");
                houses.put(key, new Flat(id, name, new Coordinates(x, y), creationDate, area, numberOfRooms, furnish, view, transport, new House(nameHouse, year, numberOfFloors, numberOfFlatsOnFloor)));
            }
            resultSet.close();
            return houses;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Коллекция не может быть загружена. Файл некорректен");
            System.exit(1);
        }
        return null;
    }

    /**
     * Выполнения комманд с БД
     */
    public int selectYourId(String login) throws SQLException {
        PreparedStatement getUserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
        getUserId.setString(1, login);
        ResultSet resultSet = getUserId.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");
    }

    public ArrayList<Integer> selectYourKeys(String login) throws SQLException {
        int userId = selectYourId(login);
        PreparedStatement statement = connection.prepareStatement("SELECT key FROM flats WHERE user_id = ?");
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        ArrayList<Integer> keys = new ArrayList<>();
        while (resultSet.next()) {
            keys.add(resultSet.getInt("key"));
        }
        return keys;
    }

    public void removeKeys(Set<Integer> keys) throws SQLException {
        for (Integer key : keys) {
            removeKey(key);
        }
    }

    public void removeKey(Integer key) throws SQLException {
        synchronized (connection) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM flats WHERE key = ?");
            statement.setInt(1, key);
            statement.execute();
        }
    }

    public ArrayList<Integer> clear(String login) throws SQLException {
        int userId = selectYourId(login);
        ArrayList<Integer> keys = new ArrayList<>(selectYourKeys(login));
        synchronized (connection) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM flats WHERE user_id = ?");
            statement.setInt(1, userId);
            statement.execute();
        }
        return keys;
    }

    public void insert(Integer key, Flat flat, String login) throws SQLException {
        PreparedStatement getUserId = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
        getUserId.setString(1, login);
        ResultSet resultSet = getUserId.executeQuery();
        resultSet.next();
        synchronized (connection) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO flats VALUES(?, nextval('sequence_id'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, key);
            statement.setString(2, flat.getName());
            statement.setFloat(3, flat.getCoordinates().getX());
            statement.setInt(4, Math.toIntExact(flat.getCoordinates().getY()));
            statement.setTimestamp(5, Timestamp.valueOf(flat.getCreationDate()));
            statement.setInt(6, (int) flat.getArea());
            statement.setInt(7, flat.getNumberOfRooms());
            statement.setString(8, String.valueOf(flat.getFurnish()));
            statement.setString(9, String.valueOf(flat.getView()));
            statement.setString(10, String.valueOf(flat.getTransport()));
            statement.setString(11, flat.getHouse().getName());
            statement.setInt(12, flat.getHouse().getYear());
            statement.setInt(13, flat.getHouse().getNumberOfFloors());
            statement.setInt(14, (int) flat.getHouse().getNumberOfFlatsOnFloor());
            statement.setInt(15, resultSet.getInt("id"));
            statement.execute();
            statement = connection.prepareStatement("SELECT id FROM flats WHERE key = ?");
            statement.setInt(1, key);
            resultSet.close();
            resultSet = statement.executeQuery();
            resultSet.next();
            flat.setId(resultSet.getInt("id"));
            resultSet.close();
        }
    }

    public void update(Integer id, Flat flat, String login) throws SQLException {
        int userId = selectYourId(login);
        synchronized (connection) {
            PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM flats WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            System.out.println(" ");
            if (userId == resultSet.getInt("user_id")) {
                System.out.println("2...");
                statement = connection.prepareStatement("UPDATE flats SET name = ?, Coordinates_x = ?, Coordinates_y = ?, area = ?, numberOfRooms = ?, furnish = ?, view = ?, transport = ?, House_name = ?, House_year = ?, House_numberOfFloors = ?, House_numberOfFlatsOnFloor = ? WHERE id = ?");
                statement.setString(1, flat.getName());
                statement.setFloat(2, flat.getCoordinates().getX());
                statement.setInt(3, Math.toIntExact(flat.getCoordinates().getY()));
                statement.setInt(4, (int) flat.getArea());
                statement.setInt(5, flat.getNumberOfRooms());
                statement.setString(6, String.valueOf(flat.getFurnish()));
                statement.setString(7, String.valueOf(flat.getView()));
                statement.setString(8, String.valueOf(flat.getTransport()));
                statement.setString(9, flat.getHouse().getName());
                statement.setInt(10, flat.getHouse().getYear());
                statement.setInt(11, flat.getHouse().getNumberOfFloors());
                statement.setInt(12, (int) flat.getHouse().getNumberOfFlatsOnFloor());
                statement.setInt(13, id);
                statement.execute();
            }
        }
    }

    /**
     *регистрация
     */

    public String registration(String login, String password) throws SQLException {
        byte[] array = new byte[10];
        new Random().nextBytes(array);
        String salt = new String(array, StandardCharsets.UTF_8);
        byte[] hash = md.digest((pepper + password + salt).getBytes());
        String passwordPlus = new String(hash, StandardCharsets.UTF_8);
        synchronized (connection) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                statement = connection.prepareStatement("INSERT INTO users VALUES(nextval('sequence_user_id'), ?, ?, ?)");
                statement.setString(1, login);
                statement.setString(2, passwordPlus);
                statement.setString(3, salt);
                statement.execute();
                return "Вы успешно зарегистрировались!";
            } else return "Пользователь с таким логином уже существует. Введите данные снова:";
        }
    }

    /**
     *вход
     */

    public boolean enter(String login, String password) throws SQLException {
        synchronized (connection) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            byte[] hash = md.digest((pepper + password + resultSet.getString("salt")).getBytes());
            String password_plus = new String(hash, StandardCharsets.UTF_8);
            return password_plus.equals(resultSet.getString("password"));
        }
    }
}
