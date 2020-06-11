package Command;

import Object.Flat;
import TCPServer.CollectionManager;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * удалить элемент из коллекции по его ключу.
 */
public class RemoveKey extends Command {
    public RemoveKey (CollectionManager manager) {
        super(manager);
        setDescription("удалить элемент из коллекции по его ключу.");
    }

    @Override
    public String execute(Object args) {
        Integer key = (Integer)args;
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        if (houses.size() != 0) {
            if (houses.keySet().stream().anyMatch(key_in_collection -> key_in_collection.equals(key))) {
                houses.keySet().stream().filter(key_in_collection -> key_in_collection.equals(key)).collect(Collectors.toSet()).forEach(houses::remove);
                return "Команда успешно выполнена.";
            }
            return("В коллекции не найдено элементов с соответствующими ключами.");
        } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }
}
