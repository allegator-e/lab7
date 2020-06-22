package Command;

import Object.*;
import TCPServer.CollectionManager;
import java.util.ArrayList;

/**
 * удалить из коллекции все элементы, превышающие заданный.
 */
public class RemoveGreater extends Command{

    private ArrayList<String> loginAndPassword;

    public RemoveGreater(CollectionManager manager, ArrayList<String> loginAndPassword) {
        super(manager);
        this.loginAndPassword = loginAndPassword;
        setDescription("удалить из коллекции все элементы, превышающие заданный.");
    }

    @Override
    public String execute(Object args) {
        return getManager().removeGreater((Flat) args, loginAndPassword.get(0));
    }
}
