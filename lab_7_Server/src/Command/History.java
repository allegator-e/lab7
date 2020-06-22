package Command;

import TCPServer.CollectionManager;
import java.util.ArrayList;

/**
 * вывести последние 9 команд (без их аргументов).
 */

public class History extends Command {
    public History(CollectionManager manager) {
        super(manager);
        setDescription("вывести последние 9 команд (без их аргументов).");
    }

    @Override
    public String execute(Object args) {
        ArrayList<String> history = (ArrayList<String>) args;
        if(!history.isEmpty())
                return String.join("\n", history);
        return "";
    }
}
