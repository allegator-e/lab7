package TCPServer;

import Object.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.*;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final TreeMap<Integer, Flat> houses = new TreeMap<>();
    private Date initDate;
    private InteractionBD interactionBD;
    private Logger LOGGER;
    {
        LOGGER = Logger.getLogger(CollectionManager.class.getName());
    }

    public CollectionManager(InteractionBD interactionBD) {
        this.interactionBD = interactionBD;
        this.initDate = new Date();
        this.load();
    }

    /**
     *  Полуение элементов коллекции из БД в локальную коллекцию.
     */

    public void load() {
        int beginSize = houses.size();
        LOGGER.log(Level.INFO, "Идёт загрузка коллекции ");
        //TreeMap<Integer, Flat> houses1 = interactionBD.load();
        //System.out.println(houses1.size());
        houses.putAll(interactionBD.load());
        LOGGER.log(Level.INFO, "Коллекция успешно загружена. Добавлено " + (houses.size() - beginSize) + " элементов.");
    }

    /**
     * Методы для выполнеия команд
     */

    public String averageOfNumberOfRooms() {
        synchronized (houses) {
            if (houses.size() != 0)
                return "Среднее значение поля numberOfRooms для всех элементов коллекции: " + houses.values().stream()
                        .mapToInt(flat -> flat.getNumberOfRooms())
                        .average().getAsDouble();
            return "В коллекции отсутствуют элементы. Выполнение команды не возможно.";
        }
    }

    public String clear(String login) {
        synchronized (houses) {
            try {
                ArrayList<Integer> keys = interactionBD.clear(login);
                houses.keySet().stream()
                        .filter(keys::contains)
                        .collect(Collectors.toSet())
                        .forEach(houses::remove);
                return "Команда успешно выполнена. ";
            } catch (SQLException e) {
                return ("В коллекции не найдено вашей недвижимости.");
            }
        }
    }

    public String countByTransport(Transport transport) {
        synchronized (houses) {
            if (houses.size() != 0)
                return "Количество элементов, значение поля transport которых равно " + transport + ": " +
                        houses.values().stream()
                                .filter(flat -> flat.getTransport().equals(transport))
                                .count();
            return "В коллекции отсутствуют элементы. Выполнение команды не возможно.";
        }
    }

    public String groupCountingByCreationDate() {
        synchronized (houses) {
            if (houses.size() != 0) {
                Map<LocalDateTime, Long> creationDates = houses.values().stream()
                        .collect(Collectors.groupingBy(Flat::getCreationDate, Collectors.counting()));
                return creationDates.keySet().stream()
                        .map(date -> date + ": " + creationDates.get(date))
                        .collect(Collectors.joining("\n"));
            }
            return "В коллекции отсутствуют элементы. Выполнение команды не возможно.";
        }
    }

    public String insert(Integer key, Flat flat, String login) {
        synchronized (houses) {
            try {
                if (houses.containsKey(key))
                    return "Вы зачем такой ключ написали? Такой уже есть в коллекции...";
                interactionBD.insert(key, flat, login);
                houses.put(key, flat);
                return "Элемент добавлен.";
            } catch (SQLException e) {
                e.printStackTrace();
                return "Чё-то не получилось, чё-то не считалось... Сорян, ну чё";
            }
        }
    }

    public String removeGreater(Flat flat, String login) {
        synchronized (houses) {
            if (houses.size() != 0) {
                try {
                    ArrayList<Integer> keys = new ArrayList<>(interactionBD.selectYourKeys(login));
                    if (keys.stream().anyMatch(key -> houses.get(key).compareTo(flat) > 0)) {
                        keys.stream()
                                .filter(key -> houses.get(key).compareTo(flat) > 0)
                                .peek(key -> {
                                    try {
                                        interactionBD.removeKey(key);
                                    } catch (SQLException ignored) {
                                    }
                                })
                                .collect(Collectors.toSet())
                                .forEach(houses::remove);
                        return "Команда успешно выполнена.";
                    }
                } catch (SQLException e) {
                    return ("В коллекции не найдено усадьб с соответствующими значениями.");
                }
                return ("В коллекции не найдено усадьб с с соответствующими значениями.");
            } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
        }
    }

    public String removeGreaterKey(Integer key, String login) {
        synchronized (houses) {
            if (houses.size() != 0) {
                try {
                    ArrayList<Integer> keys = new ArrayList<>(interactionBD.selectYourKeys(login));
                    if (keys.stream().anyMatch(keyInCollection -> keyInCollection.compareTo(key) > 0)) {
                        keys.stream()
                                .filter(keyInCollection -> keyInCollection.compareTo(key) > 0)
                                .peek(keyInCollection -> {
                                    try {
                                        interactionBD.removeKey(keyInCollection);
                                    } catch (SQLException ignored) {
                                    }
                                })
                                .collect(Collectors.toSet())
                                .forEach(houses::remove);
                        return "Команда успешно выполнена.";
                    }
                } catch (SQLException e) {
                    return ("В коллекции не найдено ваших хором с этими ключами.");
                }
                return ("В коллекции не найдено ваших хором с этими ключами.");
            } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
        }
    }

    public String removeKey(Integer key, String login) {
        synchronized (houses) {
            if (houses.size() != 0) {
                try {
                    if (houses.containsKey(key)) {
                        ArrayList<Integer> keys = new ArrayList<>(interactionBD.selectYourKeys(login));
                        if (keys.contains(key)) {
                            interactionBD.removeKey(key);
                            houses.remove(key);
                            return "Элемент успешно удален.";
                        } else return "Элемент не принадлежит вам! Фу как не культурно изменять объекты других!!";
                    }
                    return ("В коллекции не найдено ваших избушек с такими ключами.");
                } catch (SQLException e) {
                    return "Упс...";
                }
            } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
        }
    }

    public String show(){
        synchronized (houses) {
            if (houses.size() != 0) {
                List<Map.Entry<Integer, Flat>> list = houses.entrySet().stream()
                        .sorted(Comparator.comparing(element -> (element.getValue().getName())))
                        .collect(Collectors.toList());
                return list.stream()
                        .map(element -> "key: " + element.getKey() + ", flat: " + element.getValue())
                        .collect(Collectors.joining("\n\n"));
            } else return "В коллекции отсутствуют элементы. Выполнение команды невозможно.";
        }
    }

    public String update(Integer id, Flat flat, String login) {
        synchronized (houses) {
            if (houses.size() != 0) {
                if (houses.keySet().stream().anyMatch(key -> houses.get(key).getId().equals(id))) {
                    try {
                        ArrayList<Integer> keys = new ArrayList<>(interactionBD.selectYourKeys(login));
                        if (keys.stream().anyMatch(key -> houses.get(key).getId().equals(id))) {
                            try {
                                interactionBD.update(id, flat, login);
                                flat.setId(id);
                                houses.keySet().stream()
                                        .filter(key -> houses.get(key).getId().equals(id))
                                        .forEach(key -> houses.replace(key, flat));
                                return "Элемент коллекции успешно обновлен.";
                            } catch (SQLException e) {
                                System.out.println("1");
                                e.printStackTrace();
                            }
                        } else return "Фу, как не культурно менять чужие элементы!";
                    }catch(SQLException e){
                        System.out.println(2);
                        e.printStackTrace();
                    }
                    return null;
                } else return ("Элеметов с таким ай-ди в коллекции нет.");
            } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
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