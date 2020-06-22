package Command;

import TCPServer.CollectionManager;
import java.util.ArrayList;

/**
 *  Очистить коллецию.
 */
public class Clear extends Command{
    private ArrayList<String> loginAndPassword;

    public Clear(CollectionManager manager, ArrayList<String> loginAndPassword) {
        super(manager);
        this.loginAndPassword = loginAndPassword;
        setDescription("Очистить коллецию.");
    }

    @Override
    public String execute(Object args) {
        return getManager().clear(loginAndPassword.get(0));
    }
}
