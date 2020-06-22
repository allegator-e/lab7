package TCPServer;

import org.postgresql.ds.PGSimpleDataSource;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class Server {
    public static void main(String[] args) {
        final ArrayList<String> history = new ArrayList<>();
        Logger LOGGER = Logger.getLogger(Server.class.getName());
        Properties property = new Properties();
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            property.load(fis);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Проблема при получении данных для подключение к БД. Разберитесь с config.properties ");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(property.getProperty("db.host"));
        ds.setDatabaseName(property.getProperty("db.name"));
        ds.setUser(property.getProperty("db.login"));
        ds.setPassword(property.getProperty("db.password"));
        try (Connection connection = ds.getConnection()) {
            ExecutorService serviceIn = Executors.newCachedThreadPool();
            ExecutorService serviceOut = Executors.newCachedThreadPool();
            InteractionBD interactionBD = new InteractionBD(connection);
            CollectionManager serverCollection = new CollectionManager(interactionBD);
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
                serviceIn.shutdown();
                serviceOut.shutdown();
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LOGGER.log(Level.INFO, "Работа сервера завершена!");
            }));
            while (selector.isOpen()) {
                int count = selector.select();
                if (count == 0) {
                    continue;
                }
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    try {
                        SelectionKey key = keyIterator.next();
                        if (key.isValid() && key.isAcceptable()) {
                            LOGGER.log(Level.INFO, "Установлено соединение");
                            SocketChannel client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        } else if (key.isValid() && key.isReadable()) {
                            key.interestOps(SelectionKey.OP_WRITE);
                            serviceIn.submit(new TCPServerReceiver(key, interactionBD, serverCollection, history, forkJoinPool));

                        } else if (key.isValid() && key.isWritable()) {
                            if (!(key.attachment() == null)) {
                                ArrayList<Object> attach = (ArrayList<Object>) key.attachment();
                                serviceOut.execute(new TCPServerSender((Boolean) attach.get(0), (String) attach.get(1), key));
                                LOGGER.log(Level.INFO, "Окончание соединения.");
                                key.cancel();
                            }
                        }
                        keyIterator.remove();
                    } catch (CancelledKeyException e) {
                        // key has been cancelled we can ignore that.
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Проблемы с подключением к БД. Проверьте правильность введенных данных ");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Возникла проблема во время работы программы. Все плохо... ");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}