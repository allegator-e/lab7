package Command;

import TCPServer.CollectionManager;
import Object.*;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  вывести среднее значение поля numberOfRooms для всех элементов коллекции.
 */
public class AverageOfNumberOfRooms extends Command {
    public AverageOfNumberOfRooms(CollectionManager manager) {
        super(manager);
        setDescription("вывести среднее значение поля numberOfRooms для всех элементов коллекции.");
    }

    @Override
    public String execute(Object args) {
        if (getManager().getHouses().size() != 0)
            return "Среднее значение поля numberOfRooms для всех элементов коллекции: " + getManager().getHouses().values().stream()
                    .mapToInt(flat -> flat.getNumberOfRooms())
                    .sum() / getManager().getHouses().size();
        return "В коллекции отсутствуют элементы. Выполнение команды не возможно.";
    }
}
