package Object;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class NewFlat implements Serializable {
    private Scanner reader = new Scanner(System.in);
    public String readString(String name) {
        System.out.print("Введите " + name +": ");
        return reader.nextLine();
    }

    public String readStringNotNull(String name) {
        System.out.print("Введите " + name +": ");
        String n = reader.nextLine();
        if (n.equals("")) {
            System.out.println("Поле не может быть null или пустой строкой ");
            return readStringNotNull(name);
        } else return n;
    }

    public Number readNumber(String name,String format) {
        String n = readStringNotNull(name);
        try {
            switch (format) {
                case "Float":
                    return Float.parseFloat(n);
                case "Integer":
                    return Integer.parseInt(n);
                case "Long":
                    return Long.parseLong(n);
                default:
                    return null;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Аргумент не является значением типа " + format);
            return readNumber(name,format);
        }
    }

    public Enum readEnam(String name,String type) {
        String n = readString(name);
        n = n.toUpperCase();
        try {
            switch (type) {
                case "Furnish":
                    if (n.equals("")) return null;
                    return Furnish.valueOf(n);
                case "View":
                    if (n.equals("")) return null;
                    return View.valueOf(n);
                case "Transport":
                    return Transport.valueOf(n);
                default:
                    return null;
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Значение поля неверное");
            return readEnam(name,type);
        }
    }

    /**
     * Получает значения элемента в коллекции
     */
    public Flat newFlat() {
        Scanner reader = new Scanner(System.in);
        String name = readStringNotNull("name");
        System.out.println("Введите coordinates: ");
        float x = (Float) readNumber("x","Float");
        while (x <= -227) {
            System.out.println("Значение поля должно быть больше -227");
            x = (Float) readNumber("x","Float");
        }
        Long y = (Long) readNumber("y","Long");
        while (y > 769) {
            System.out.println("Значение поля должно быть меньше 769");
            y = (Long) readNumber("y", "Long");
        }
        long area = (Long) readNumber("area","Long");
        while (area < 0) {
            System.out.println("Значение поля должно быть больше 0");
            area = (Long) readNumber("area", "Long");
        }
        Integer numberOfRooms = (Integer) readNumber("numberOfRooms","Integer");
        while (numberOfRooms < 0) {
            System.out.println("Значение поля должно быть больше 0");
            numberOfRooms = (Integer) readNumber("numberOfRooms","Integer");
        }
        Furnish furnish = (Furnish) readEnam("Furnish (DESIGNER, FINE, LITTLE, BAD, NONE, null)","Furnish");
        View view = (View) readEnam("View (PARK, STREET, BAD, null)","View");
        Transport transport = (Transport) readEnam("Transport (ENOUGH, NORMAL, FEW, LITTLE, NONE)","Transport");
        System.out.println("Введите House: ");
        String nameHouse = readStringNotNull("name");
        int year = (Integer) readNumber("year","Integer");
        while (year < 0) {
            System.out.println("Значение поля должно быть больше 0");
            year = (Integer) readNumber("year","Integer");
        }
        int numberOfFloors = (Integer) readNumber("numberOfFloors","Integer");
        while (numberOfFloors < 0) {
            System.out.println("Значение поля должно быть больше 0");
            numberOfFloors = (Integer) readNumber("numberOfFloors","Integer");
        }
        long numberOfFlatsOnFloor = (Integer) readNumber("numberOfFlatsOnFloor","Integer");
        while (numberOfFlatsOnFloor < 0) {
            System.out.println("Значение поля должно быть больше 0");
            numberOfFlatsOnFloor = (Integer) readNumber("numberOfFlatsOnFloor","Integer");
        }
        int id = 0;
        LocalDateTime creationDate;
        LocalDate d = LocalDate.now();
        LocalTime t = LocalTime.now();
        creationDate = LocalDateTime.of(d,t);
        System.out.println("Все значения элемента успешно получены");
        return new Flat(id, name, new Coordinates(x, y), creationDate, area, numberOfRooms, furnish, view, transport, new House(nameHouse, year, numberOfFloors, numberOfFlatsOnFloor));
    }

}
