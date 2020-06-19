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
    public String execute(Object args) {
        Transport transport = (Transport) args;
        if (getManager().getHouses().size() != 0)
            return "Количество элементов, значение поля transport которых равно " + transport + ": " +
                    getManager().getHouses().values().stream()
                    .filter(flat -> flat.getTransport().equals(transport))
                    .count();
        return "В коллекции отсутствуют элементы. Выполнение команды не возможно.";
    }
}
