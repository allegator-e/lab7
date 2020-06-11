package Command;

import TCPServer.CollectionManager;

/**
 * Класс, от которого наследуются классы всех команд
 */

public abstract class Command {
    private CollectionManager manager;
    private String description;
    public Command(CollectionManager manager)
    {
        this.manager=manager;
    }
    public String execute(Object args){
        return "Пропущен аргумент.";
    }
    public void setDescription(String description)
    {
        this.description=description;
    }
    public String getDescription()
    {
        return description;
    }
    public CollectionManager getManager()
    {
        return manager;
    }
    public void setManager(CollectionManager manager)
    {
        this.manager=manager;
    }
}
