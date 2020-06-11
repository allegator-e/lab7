package Command;

import TCPServer.CollectionManager;

/**
 *  Очистить коллецию.
 */
public class Clear extends Command{
    public Clear(CollectionManager manager) {
        super(manager);
        setDescription("Очистить коллецию.");
    }

    @Override
    public String execute(Object args) {
        getManager().getHouses().clear();
        return "Коллекция пуста.";
    }
}
