package TCPServer;

import Command.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServerReceiver implements Runnable {
    private Logger LOGGER = Logger.getLogger(TCPServerReceiver.class.getName());
    private ArrayList<String> history;
    private CollectionManager serverCollection;
    private HashMap<String, Command> availableCommands = new HashMap<>();
    private Connection connection;
    private ForkJoinPool forkJoinPool;
    private SelectionKey key;
    private Object o;
    private ByteBuffer buffer = ByteBuffer.allocate(4096);

    public TCPServerReceiver(SelectionKey key, Connection connection, CollectionManager serverCollection, ArrayList<String> history, ForkJoinPool forkJoinPool) {
        this.key =key;
        this.connection = connection;
        this.history = history;
        this.serverCollection = serverCollection;
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            int count = socketChannel.read(buffer);
            if (count > -1) {
                byte[] bytes = buffer.array();
                ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(baos);
                o = ois.readObject();
                ois.close();
                baos.close();
                buffer.clear();
                treatment();
            }
            if (count == -1) {
                key.cancel();
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.INFO, "Работа сервера завершена!");
        }
    }

    private void treatment() {
        String str;
        int argument =0;
        ArrayList<Object> list_object = (ArrayList<Object>) o;
        boolean access = (Boolean) list_object.get(0);
        ArrayList<String> login_and_password = (ArrayList<String>) list_object.get(1);
        String command = (String) list_object.get(2);
        Object object = list_object.get(3);
        if (!access) {
            ServerAutorization serverAutorization = new ServerAutorization(connection, login_and_password, command);
            access = serverAutorization.access();
            str = serverAutorization.getAnswer();
        } else {
            ServerAutorization user = new ServerAutorization(connection, login_and_password, "login");
            if (access = user.access()) {
                LOGGER.log(Level.FINE, "Получена команда" + command);
                String[] parseCommand = command.trim().split(" ", 2);
                command = parseCommand[0];
                if (parseCommand.length == 2) {
                    argument = Integer.parseInt(parseCommand[1]);
                }
                availableCommands.put("help", new Help(serverCollection, availableCommands));
                availableCommands.put("insert", new Insert(serverCollection, argument, connection, login_and_password));
                availableCommands.put("info", new Info(serverCollection));
                availableCommands.put("clear", new Clear(serverCollection, connection, login_and_password));
                availableCommands.put("show", new Show(serverCollection));
                availableCommands.put("update_id", new Update(serverCollection, argument, connection, login_and_password));
                availableCommands.put("remove_greater", new RemoveGreater(serverCollection, connection, login_and_password));
                availableCommands.put("history", new History(serverCollection));
                availableCommands.put("remove_key", new RemoveKey(serverCollection, connection, login_and_password));
                availableCommands.put("remove_greater_key", new RemoveGreaterKey(serverCollection, connection, login_and_password));
                availableCommands.put("average_of_number_of_rooms", new AverageOfNumberOfRooms(serverCollection));
                availableCommands.put("group_counting_by_creation_date", new GroupCountingByCreationDate(serverCollection));
                availableCommands.put("count_by_transport", new CountByTransport(serverCollection));
                Command errorCommand = new Command(null) {
                    @Override
                    public String execute(Object args) {
                        if (parseCommand[0].equals("execute_script"))
                            return "Обработка скрипта запущена.";
                        return "Неверная команда.";
                    }
                };
                if (parseCommand[0].equals("history")) {
                    object = history;
                }
                String finalCommand = command;
                Object finalObject = object;
                ForkJoinTask<String> forkJoinTask = forkJoinPool.submit(() -> availableCommands.getOrDefault(finalCommand, errorCommand).execute(finalObject));
                str = forkJoinTask.join();
                LOGGER.log(Level.FINE, "Команда обработана");
                synchronized (history) {
                    if (command.equals("execute_script") || availableCommands.containsKey(command))
                        history.add(command);
                    if (history.size() > 9)
                        history.remove(0);
                }
                LOGGER.log(Level.FINE, "Команда добавлена в историю. Результат выполнения отправлен клиенту");
            } else {
                str = "Эй, ты кто ваще такой? Как ты смог обойти вход?";
                LOGGER.log(Level.INFO, "Кто-то пытался обойти систему...");
            }
        }
        ArrayList<Object> attach = new ArrayList<>();
        attach.add(access);
        attach.add(str);
        key.attach(attach);
    }
}