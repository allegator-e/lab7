package TCPClient;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.UnresolvedAddressException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для подключения к серверу
 */
public class TCPClientConnect {

    public static void main(String[] args) throws IOException {
        Scanner commandReader = new Scanner(System.in);
        System.out.print("Введите hostname:");
        try {
            String hostName = commandReader.nextLine();
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
            Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Работа программы завершена!")));
            while (true) {
                try {
                    Thread.sleep(500);
                    TCPSender sender = new TCPSender(hostName, port);
                    String[] command;
                    System.out.print(">>");
                  //  command = commandReader.nextLine().trim().split(" ");
                    command = new String[]{"show"};
                    if (command[0].equals("exit")) {
                        System.exit(1);
                    } else {
                        sender.checker(command);
                    }
                } catch (IOException e) {
                    System.out.println("Подключение к серверу невозможно");
                } catch (InputMismatchException e) {
                    System.out.println("Команда введена некорректно. Введите help.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (UnresolvedAddressException e){
            System.out.println("Ошибка инициализации хоста.");
        }catch (NoSuchElementException e){
            System.out.println("Ну блин, только начали же, куда вы уходите?");
        }
    }
}

