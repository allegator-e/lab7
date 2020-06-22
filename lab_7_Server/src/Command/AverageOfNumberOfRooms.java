package Command;

import TCPServer.CollectionManager;

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
        return getManager().averageOfNumberOfRooms();
    }
}
