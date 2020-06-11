package TCPServer;

import Command.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) {
        ArrayList<String> history = new ArrayList<>();
        Logger LOGGER = Logger.getLogger(Server.class.getName());
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            CollectionManager serverCollection = new CollectionManager(args[0]);
            Scanner commandReader = new Scanner(System.in);
            Integer port = 0;
            while (true) {
                try {
                    System.out.print("Введите порт:");
                    port = Integer.parseInt(commandReader.nextLine());
                    if (port <= 65535 && port >= 1) break;
                } catch (NumberFormatException e) {
                    System.out.println("Порт должен принимать целочисленные значения от 1 до 65535.");
                }
            }
            Selector selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                serverCollection.save();
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LOGGER.log(Level.INFO, "Работа сервера завершена!");
            }));
            HashMap<String, Command> availableCommands = new HashMap<>();
            String command = "";
            String str = "";
            int argument = 0;
            Object object;
            while (selector.isOpen()) {
                int count = selector.select();
                if (count == 0) {
                    continue;
                }
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        LOGGER.log(Level.INFO, "Установлено соединение");
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        Future<Object> future = executorService.submit(new TCPServerReceiver(key));
                        ArrayList<Object> list_object = (ArrayList<Object>) future.get();
                        command = (String) list_object.get(0);
                        LOGGER.log(Level.FINE, "Получена команда" + command);
                        object = list_object.get(1);
                        String[] parseCommand = command.trim().split(" ", 2);
                        command = parseCommand[0];
                        if (parseCommand.length == 2) {
                            argument = Integer.parseInt(parseCommand[1]);
                        }
                        availableCommands.put("help", new Help(serverCollection, availableCommands));
                        availableCommands.put("insert", new Insert(serverCollection, argument));
                        availableCommands.put("info", new Info(serverCollection));
                        availableCommands.put("clear", new Clear(serverCollection));
                        availableCommands.put("show", new Show(serverCollection));
                        availableCommands.put("update_id", new Update(serverCollection, argument));
                        availableCommands.put("remove_greater", new RemoveGreater(serverCollection));
                        availableCommands.put("history", new History(serverCollection));
                        availableCommands.put("remove_key", new RemoveKey(serverCollection));
                        availableCommands.put("remove_greater_key", new RemoveGreaterKey(serverCollection));
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
                        str = new ForkJoinPool().invoke(new ExecuteCommand(command, object, availableCommands));
                        LOGGER.log(Level.FINE, "Команда обработана");
                    } else if (key.isWritable()) {
                        executorService.execute(new TCPServerSender(str, key));
                        if (command.equals("execute_script") || availableCommands.containsKey(command))
                            history.add(command);
                        if (history.size() > 9)
                            history.remove(0);
                        LOGGER.log(Level.FINE, "Команда добавлена в историю. Результат выполнения отправлен клиенту");
                        LOGGER.log(Level.INFO, "Окончание соединения.");
                    }
                    keyIterator.remove();
                }
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Путь до файла xml нужно передать через аргумент командной строки.");
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Возникла проблема во время работы программы. Все плохо... ");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}