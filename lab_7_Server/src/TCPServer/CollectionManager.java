package TCPServer;

import Object.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.time.*;
import java.util.*;

public class CollectionManager {
    private TreeMap<Integer, Flat> houses = new TreeMap<>();
    private Connection connection;
    private Date initDate;
    private Integer nowId;
    static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(CollectionManager.class.getName());
    }

    public CollectionManager(Connection connection)  {
        this.connection = connection;
        this.load();
        this.initDate = new Date();
    }

    public Integer getNowId(){
        return nowId++;
    }

    public TreeMap<Integer, Flat> getHouses(){
        return houses;
    }
    /**
     * Сериализует коллекцию в файл json.
     */
    /*
    public void save() {
        try  {
            Document doc = new Document();
            // создаем корневой элемент с пространством имен
            doc.setRootElement(new Element("Flats"));
            // формируем JDOM документ из объектов Student
            for (Integer key : houses.keySet()) {
                Element element = new Element("Flat");
                element.setAttribute("key", String.valueOf(key));
                element.addContent(new Element("id").setText( String.valueOf(houses.get(key).getId())));
                element.addContent(new Element("name").setText(houses.get(key).getName()));
                Element element_c = new Element("Coordinates");
                element_c.addContent(new Element("x").setText(String.valueOf(houses.get(key).getCoordinates().getX())));
                element_c.addContent(new Element("y").setText(String.valueOf(houses.get(key).getCoordinates().getY())));
                element.addContent(element_c);
                element.addContent(new Element("creationDate").setText(String.valueOf(houses.get(key).getCreationDate())));
                element.addContent(new Element("area").setText(String.valueOf(houses.get(key).getArea())));
                element.addContent(new Element("numberOfRooms").setText(String.valueOf(houses.get(key).getNumberOfRooms())));
                element.addContent(new Element("furnish").setText(String.valueOf(houses.get(key).getFurnish())));
                element.addContent(new Element("view").setText(String.valueOf(houses.get(key).getView())));
                element.addContent(new Element("transport").setText(String.valueOf(houses.get(key).getTransport())));
                Element element_d = new Element("House");
                element_d.addContent(new Element("name").setText(houses.get(key).getHouse().getName()));
                element_d.addContent(new Element("year").setText(String.valueOf(houses.get(key).getHouse().getYear())));
                element_d.addContent(new Element("numberOfFloors").setText(String.valueOf(houses.get(key).getHouse().getNumberOfFloors())));
                element_d.addContent(new Element("numberOfFlatsOnFloor").setText(String.valueOf(houses.get(key).getHouse().getNumberOfFlatsOnFloor())));
                element.addContent(element_d);
                doc.getRootElement().addContent(element);
            }
            if (!xmlCollection.canWrite())
                LOGGER.log(Level.WARNING, "Файл защищён от записи. Невозможно сохранить коллекцию.");
            else{
                // Документ JDOM сформирован и готов к записи в файл
                XMLOutputter xmlWriter = new XMLOutputter(Format.getPrettyFormat());
                // сохнаряем в файл
                xmlWriter.output(doc, new FileOutputStream(xmlCollection));
                LOGGER.log(Level.FINE, "Коллекция успешно сохранена в файл.");
            }
        } catch (IOException ex) {
           LOGGER.log(Level.SEVERE,"Коллекция не может быть записана в файл");
        }
    } */

    /**
     *  Десериализует коллекцию из файла json.
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