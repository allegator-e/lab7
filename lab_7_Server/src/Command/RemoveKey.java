package Command;

import TCPServer.CollectionManager;
import java.util.ArrayList;

/**
 * удалить элемент из коллекции по его ключу.
 */
public class RemoveKey extends Command {
    private ArrayList<String> loginAndPassword;

    public RemoveKey(CollectionManager manager, ArrayList<String> loginAndPassword) {
        super(manager);
        this.loginAndPassword = loginAndPassword;
        setDescription("удалить элемент из коллекции по его ключу.");
    }

    @Override
    public String execute(Object args) {
        return getManager().removeKey((Integer)args, loginAndPassword.get(0));
    }
}
