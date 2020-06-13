package Command;

import Object.*;
import TCPServer.CollectionManager;
import java.util.TreeMap;

/**
 * вывести количество элементов, значение поля transport которых равно заданному.
 */
public class CountByTransport extends Command {
    public CountByTransport(CollectionManager manager) {
        super(manager);
        setDescription("вывести количество элементов, значение поля transport которых равно заданному.");
    }

    @Override
    public synchronized String execute(Object args) {
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        Transport transport = (Transport) args;
        if (houses.size() != 0) {
            return "Количество элементов, значение поля transport которых равно " + transport + ": " +
                    houses.values().parallelStream()
                    .filter(flat -> flat.getTransport().equals(transport))
                    .count();
            }
        return "В коллекции отсутствуют элементы. Выполнение команды не возможно.";
    }
}
