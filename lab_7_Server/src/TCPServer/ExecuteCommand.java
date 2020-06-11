package TCPServer;

import Command.*;

import java.util.HashMap;
import java.util.concurrent.RecursiveTask;

public class ExecuteCommand extends RecursiveTask<String> {
    String command;
    Object object;
    HashMap<String, Command> availableCommands;
    ExecuteCommand(String command, Object object, HashMap<String, Command> availableCommands){
        this.command = command;
        this.object = object;
        this.availableCommands = availableCommands;
    }
    Command errorCommand = new Command(null) {
        @Override
        public String execute(Object args) {
            if (command.equals("execute_script"))
                return "Обработка скрипта запущена.";
            return "Неверная команда.";
        }
    };
    @Override
    protected String compute() {
        return availableCommands.getOrDefault(command, errorCommand).execute(object);
    }
}
