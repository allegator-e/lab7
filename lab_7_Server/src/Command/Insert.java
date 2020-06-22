package Command;

import TCPServer.CollectionManager;
import Object.*;
import java.util.ArrayList;

/**
 * добавить новый элемент с заданным ключом.
 */
public class Insert extends Command {
    private Integer key;
    private ArrayList<String> loginAndPassword;
    public Insert(CollectionManager manager, Integer key, ArrayList<String> loginAndPassword) {
        super(manager);
        this.key = key;
        this.loginAndPassword = loginAndPassword;
        setDescription("добавить новый элемент с заданным ключом.");
    }

    @Override
    public String execute(Object args) {
        return getManager().insert(key, (Flat) args, loginAndPassword.get(0));
    }
}
