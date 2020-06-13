package TCPServer;

import Command.*;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class Server {
    public static void main(String[] args) {
        ArrayList<String> history = new ArrayList<>();
        Logger LOGGER = Logger.getLogger(Server.class.getName());

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("pg");
        ds.setDatabaseName("studs");
        ds.setUser("s286536");
        ds.setPassword("wzf777");
        try (Connection connection = ds.getConnection()) {
            ExecutorService service_in = Executors.newCachedThreadPool();
            ExecutorService service_out = Executors.newCachedThreadPool();
            ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
            CollectionManager serverCollection = new CollectionManager(connection);

            Scanner commandReader = new Scanner(System.in);
            int port;
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
                service_in.shutdown();
                service_out.shutdown();
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LOGGER.log(Level.INFO, "Работа сервера завершена!");
            }));
            HashMap<String, Command> availableCommands = new HashMap<>();
            boolean access = false;
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
                    try {
                        SelectionKey key = keyIterator.next();
                        if (!key.equals(null) && key.isValid() && key.isAcceptable()) {
                            LOGGER.log(Level.INFO, "Установлено соединение");
                            SocketChannel client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        } else if (!key.equals(null) && key.isValid() && key.isReadable()) {
                            Future<Object> future = service_in.submit(new TCPServerReceiver(key));
                            //while(!future.isDone()) {}
                            ArrayList<Object> list_object = (ArrayList<Object>) future.get();
                            access = (Boolean) list_object.get(0);
                            ArrayList<String> login_and_password = (ArrayList<String>) list_object.get(1);
                            command = (String) list_object.get(2);
                            object = list_object.get(3);
                            if (!access) {
                                ServerAutorization serverAutorization = new ServerAutorization(connection, login_and_password, command);
                                access = serverAutorization.access();
                                str = serverAutorization.getAnswer();
                            } else {


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
                                str = forkJoinPool.invoke(forkJoinTask);
                                LOGGER.log(Level.FINE, "Команда обработана");
                            }
                        } else if (!key.equals(null) && key.isValid() && key.isWritable()) {
                            if (command.equals("execute_script") || availableCommands.containsKey(command))
                                history.add(command);
                            if (history.size() > 9)
                                history.remove(0);
                            LOGGER.log(Level.FINE, "Команда добавлена в историю. Результат выполнения отправлен клиенту");
                            service_out.execute(new TCPServerSender(access, str, key));
                            LOGGER.log(Level.INFO, "Окончание соединения.");
                        }
                        keyIterator.remove();
                    } catch (CancelledKeyException cke) {
                        // key has been cancelled we can ignore that.
                    }
                }
            }
        }catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Путь до файла xml нужно передать через аргумент командной строки.");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Возникла проблема во время работы программы. Все плохо... ");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}