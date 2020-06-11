package Command;

import TCPServer.CollectionManager;

/**
 * вывод информации о коллекции в стандартный поток вывода (тип, дата инициализации, количество элементов и т.д.)
 */
public class Info extends Command{
    public Info(CollectionManager manager)
    {
        super(manager);
        setDescription("вывод информации о коллекции в стандартный поток вывода (тип, дата инициализации, количество элементов и т.д.)");
    }

    @Override
    public String execute(Object args) {
        return "Данная лабараторная работа сделана представителями группы Р3131 Курашовым Олегом и Савон Галиной\n" + getManager().toString();
    }
}
