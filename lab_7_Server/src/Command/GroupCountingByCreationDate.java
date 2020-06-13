package Command;

import Object.*;
import TCPServer.CollectionManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * вывести количество элементов, значение поля transport которых равно заданному.
 */
public class GroupCountingByCreationDate extends Command {
    public GroupCountingByCreationDate(CollectionManager manager) {
        super(manager);
        setDescription("сгруппировать элементы коллекции по значению поля creationDate, вывести количество элементов в каждой группе.");
    }

    @Override
    public synchronized String execute(Object args) {
        TreeMap<Integer, Flat> houses = getManager().getHouses();
        if (houses.size() != 0) {
            Map<LocalDateTime, Long> creationDates = houses.values().parallelStream()
                    .collect(Collectors.groupingBy(Flat::getCreationDate, Collectors.counting()));
            return creationDates.keySet().parallelStream()
                    .map(date -> date + ": " + creationDates.get(date))
                    .collect(Collectors.joining("\n"));
        } return "В коллекции отсутствуют элементы. Выполнение команды не возможно.";
    }
}
