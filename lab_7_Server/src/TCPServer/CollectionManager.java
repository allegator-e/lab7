package TCPServer;

import Object.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.time.*;
import java.util.*;

public class CollectionManager {
    private TreeMap<Integer, Flat> houses = new TreeMap<>();
    private String collectionPath;
    private File xmlCollection;
    private Date initDate;
    private Integer nowId;
    static Logger LOGGER;
    private static java.util.logging.LogManager LogManager;
    static {
        LOGGER = Logger.getLogger(CollectionManager.class.getName());
    }

    public CollectionManager(String collectionPath)  {
        File file = new File(collectionPath);
        if (file.exists()) {
            this.xmlCollection = file;
            this.collectionPath = collectionPath;
        } else {
            LOGGER.log(Level.SEVERE, "Файл по указанному пути не существует.");
            System.exit(1);
        }
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
    }

    /**
     *  Десериализует коллекцию из файла json.
     */
    public void load() {
        int beginSize = houses.size();
        if (!xmlCollection.exists()) {
            LOGGER.log(Level.SEVERE, "Файла по указанному пути не существует.");
            System.exit(1);
        } else if (!xmlCollection.canRead() || !xmlCollection.canWrite()) {
            LOGGER.log(Level.SEVERE, "Файл защищён от чтения и/или записи. Для работы программы нужны оба разрешения.");
            System.exit(1);
        } else {
            if (xmlCollection.length() == 0) {
                LOGGER.log(Level.SEVERE, "Файл пуст.");
                System.exit(1);
            }
            LOGGER.log(Level.INFO, "Идёт загрузка коллекции " + xmlCollection.getAbsolutePath());
            // мы можем создать экземпляр JDOM Document из классов DOM, SAX и STAX Builder
            try {
                org.jdom2.Document jdomDocument = createJDOMusingSAXParser(collectionPath);
                Element root = jdomDocument.getRootElement();
                // получаем список всех элементов
                List<Element> labWorkListElements = root.getChildren("Flat");
                // список объектов Student, в которых будем хранить
                // считанные данные по каждому элементу
                Integer maxId = 0;
                for (Element lab : labWorkListElements) {
                    Integer key = Integer.parseInt(lab.getAttributeValue("key"));
                    Integer id = Integer.parseInt(lab.getChildText("id"));
                    if (id > maxId) maxId = id;
                    String name = lab.getChildText("name");
                    List<Element> lab_c = lab.getChildren("Coordinates");
                    float x = Float.parseFloat(lab_c.get(0).getChildText("x"));
                    Long y = Long.parseLong(lab_c.get(0).getChildText("y"));
                    LocalDateTime creationDate = LocalDateTime.parse(lab.getChildText("creationDate"));
                    long area = Long.parseLong(lab.getChildText("area"));
                    Integer numberOfRooms = Integer.parseInt(lab.getChildText("numberOfRooms"));
                    Furnish furnish = null;
                    String furnish_s = lab.getChildText("furnish");
                    if (!furnish_s.equals("null")) furnish = Furnish.valueOf(furnish_s);
                    View view = null;
                    String view_s = lab.getChildText("view");
                    if (!view_s.equals("null")) view = View.valueOf(view_s);
                    Transport transport = Transport.valueOf(lab.getChildText("transport"));
                    List<Element> lab_d = lab.getChildren("House");
                    String nameHouse = lab_d.get(0).getChildText("name");
                    int year = Integer.parseInt(lab_d.get(0).getChildText("year"));
                    int numberOfFloors = Integer.parseInt(lab_d.get(0).getChildText("numberOfFloors"));
                    long numberOfFlatsOnFloor = Long.parseLong(lab_d.get(0).getChildText("numberOfFlatsOnFloor"));
                    houses.put(key, new Flat(id, name, new Coordinates(x, y), creationDate, area, numberOfRooms, furnish, view, transport, new House(nameHouse, year, numberOfFloors, numberOfFlatsOnFloor)));
                }
                LOGGER.log(Level.INFO, "Коллекция успешно загружена. Добавлено " + (houses.size() - beginSize) + " элементов.");
                nowId = maxId;
            } catch (IOException | JDOMException ex) {
                LOGGER.log(Level.SEVERE, "Коллекция не может быть загружена. Файл некорректен");
                System.exit(1);
            }
        }
    }
    private static org.jdom2.Document createJDOMusingSAXParser(String fileName)
            throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new File(fileName));
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
                xmlCollection.equals(manager.xmlCollection) &&
                initDate.equals(manager.initDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houses, initDate);
    }
}