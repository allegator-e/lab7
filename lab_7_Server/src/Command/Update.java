package Command;

import Object.Flat;
import TCPServer.CollectionManager;
import java.util.ArrayList;

/**
 * обновить значение элемента коллекции, id которого равен заданному.
 */
public class Update extends Command{
    private Integer id;
    private ArrayList<String> loginAndPassword;
    public Update(CollectionManager manager, Integer id, ArrayList<String> loginAndPassword) {
        super(manager);
        this.id = id;
        this.loginAndPassword = loginAndPassword;
        setDescription("обновить значение элемента коллекции, id которого равен заданному.");
    }

    @Override
    public String execute(Object args) {
        return getManager().update(id, (Flat) args, loginAndPassword.get(0));
    }
}