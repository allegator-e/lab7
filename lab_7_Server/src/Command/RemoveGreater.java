package Command;

import Object.*;
import TCPServer.CollectionManager;
import java.util.TreeMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * удалить из коллекции все элементы, превышающие заданный.
 */
public class RemoveGreater extends Command{

    public RemoveGreater(CollectionManager manager) {
        super(manager);
        setDescription("удалить из коллекции все элементы, превышающие заданный.");
    }

    @Override
    public String execute(Object args) {
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        Flat flat = (Flat) args;
        if (houses.size() != 0) {
            if (houses.keySet().stream().anyMatch(key -> houses.get(key).compareTo(flat) > 0)) {
                houses.keySet().stream().filter(key -> houses.get(key).compareTo(flat) > 0).collect(Collectors.toSet()).forEach(houses::remove);
                return "Команда успешно выполнена.";
            }
            return("В коллекции не найдено соответствующих элементов.");
        } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }
}
