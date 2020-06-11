package Command;

import Object.Flat;
import TCPServer.CollectionManager;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * обновить значение элемента коллекции, id которого равен заданному.
 */
public class Update extends Command{
    private Integer id;
    public Update(CollectionManager manager, Integer id) {
        super(manager);
        this.id = id;
        setDescription("обновить значение элемента коллекции, id которого равен заданному.");
    }

    @Override
    public String execute(Object args) {
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        Flat flat = (Flat) args;
        if (houses.size() != 0) {
                if (houses.keySet().stream().anyMatch(key -> houses.get(key).getId().equals(id))) {
                    houses.keySet().stream()
                            .filter(key -> houses.get(key).getId().equals(id))
                            .forEach(key -> houses.replace(key, flat));
                    return "Элемент коллекции успешно обновлен.";
                }
                return("В коллекции не найдено элемента с указанным id.");
        } else return ("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }
}