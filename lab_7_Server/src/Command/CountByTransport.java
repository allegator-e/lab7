package Command;

import Object.*;
import TCPServer.CollectionManager;

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
        return getManager().countByTransport((Transport) args);
    }
}
