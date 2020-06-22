package Command;

import TCPServer.CollectionManager;
import java.util.ArrayList;

/**
 * удалить из коллекции все элементы, ключ которых превышает заданный.
 */
public class RemoveGreaterKey extends Command {
    private ArrayList<String> loginAndPassword;

    public RemoveGreaterKey(CollectionManager manager, ArrayList<String> loginAndPassword) {
        super(manager);
        this.loginAndPassword = loginAndPassword;
        setDescription("удалить из коллекции все элементы, ключ которых превышает заданный.");
    }

    @Override
    public String execute(Object args) {
        return getManager().removeGreaterKey((Integer)args, loginAndPassword.get(0));
    }
}
