package Command;

import TCPServer.CollectionManager;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import Object.*;

/**
 * вывести в стандартный поток вывода все элементы коллекции в строковом представлении.
 */
public class Show extends Command {
    public Show(CollectionManager manager) {
        super(manager);
        setDescription("вывести в стандартный поток вывода все элементы коллекции в строковом представлении.");
    }

    @Override
    public synchronized String execute(Object args) {
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        String s = "";
        if (houses.size() != 0) {
            List<Map.Entry<Integer, Flat>> list = houses.entrySet().parallelStream()
                    .sorted(Comparator.comparing(element -> (element.getValue().getName())))
                    .collect(Collectors.toList());
            return   list.parallelStream()
                    .map(element -> "key: " + element.getKey() + ", flat: " + element.getValue())
                    .collect(Collectors.joining("\n\n"));
        }
        else return "В коллекции отсутствуют элементы. Выполнение команды невозможно.";
    }
}
