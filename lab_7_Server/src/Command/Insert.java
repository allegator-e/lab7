package Command;

import TCPServer.CollectionManager;
import Object.*;

/**
 * добавить новый элемент с заданным ключом.
 */
public class Insert extends Command {
    private Integer key;
    public Insert(CollectionManager manager, Integer key) {
        super(manager);
        this.key = key;
        setDescription("добавить новый элемент с заданным ключом.");
    }

    @Override
    public String execute(Object args) {
        Flat flat = (Flat) args;
        if(getManager().getHouses().containsKey(key))
            return "Вы зачем такой ключ написали? Такой уже есть в коллекции...";
        flat.setId(getManager().getNowId());
        getManager().getHouses().put(key, flat);
        return "Элемент добавлен.";
    }
}
