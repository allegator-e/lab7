package Command;

import TCPServer.CollectionManager;

/**
 * вывести количество элементов, значение поля transport которых равно заданному.
 */
public class GroupCountingByCreationDate extends Command {
    public GroupCountingByCreationDate(CollectionManager manager) {
        super(manager);
        setDescription("сгруппировать элементы коллекции по значению поля creationDate, вывести количество элементов в каждой группе.");
    }

    @Override
    public String execute(Object args) {
        return getManager().groupCountingByCreationDate();
    }
}
