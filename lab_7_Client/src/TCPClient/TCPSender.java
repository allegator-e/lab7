package TCPClient;

import Object.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Класс для отправки данных на сервер.
 */

public class TCPSender {

    private Flat flat;
    private NewFlat new_flat = new NewFlat();
    private int key;
    private String script;
    private Transport transport;
    private Socket socket;
    private String hostname;
    private int port;
    private ArrayList<File> scripts = new ArrayList<>();

    public TCPSender(String hostname, int port) {
        this.hostname = hostname; this.port = port;
    }

    public boolean checker(String[] command) throws IOException {
        if (command[0].equals("insert") || command[0].equals("update_id")) {
            if (command.length == 2) {
                try {
                    key = Integer.parseInt(command[1]);
                    flat = new_flat.newFlat();
                    sender(command[0] + " " + command[1], flat);
                    return true;
                } catch (NumberFormatException ex) {
                    System.out.println("Аргумент не является значением типа Integer");
                }
            } else System.out.println("Комманда некорректна.");
        } else if (command[0].equals("remove_greater")) {
            flat = new_flat.newFlat();
            sender(command[0], flat);
            return true;
        } else if (command[0].equals("remove_greater_key") || command[0].equals("remove_key")) {
            if (command.length == 2) {
                try {
                    key = Integer.parseInt(command[1]);
                    sender(command[0], key);
                    return true;
                } catch (NumberFormatException ex) {
                    System.out.println("Аргумент не является значением типа Integer");
                }
            } else System.out.println("Комманда некорректна.");
        } else if (command[0].equals("count_by_transport")) {
            if (command.length == 2) {
                try {
                    command[1] = command[1].toUpperCase();
                    transport = Transport.valueOf(command[1]);
                    sender(command[0], transport);
                    return true;
                } catch (IllegalArgumentException | NullPointerException ex) {
                    System.out.println("Значение поля Transport некорректно. Возможные значения: FEW, NONE, LITTLE, NORMAL, ENOUGH.");
                }
            } else System.out.println("Комманда некорректна.");
        } else if (command[0].equals("execute_script")) {
            if (command.length == 2) {
                if (!command[1].equals("")) {
                    File file1 = new File(command[1]);
                    if (!file1.exists())
                        System.out.println("Файла с таким названием не существует.");
                    else if (!file1.canRead())
                        System.out.println("Файл защищён от чтения. Невозможно выполнить скрипт.");
                    else if (scripts.contains(file1)) {
                        System.out.println("Могло произойти зацикливание при исполнении скрипта: " + command[1] + "\nКоманда не будет выполнена. Переход к следующей команде");
                    } else {
                        script = command[1];
                        sender(command[0], "mew");
                        scripts.add(file1);
                        try (BufferedReader commandReader = new BufferedReader(new FileReader(file1))) {
                            String line = commandReader.readLine();
                            while (line != null) {
                                checker(line.split(" "));
                                line = commandReader.readLine();
                            }
                        } catch (IOException ex) {
                            System.out.println("Невозможно считать скрипт");
                        }
                        scripts.remove(scripts.size() - 1);
                        return false;
                    }
                }
            } else System.out.println("Команда введена некорректно.");
        }
        else {
            sender(command[0], "mew");
            return true;
        }
        return false;
    }
    private void sender(Object object, Object argument){
        try {
            ArrayList <Object> list_object = new ArrayList<>();
            list_object.add(object);
            list_object.add(argument);
            socket = new Socket(hostname, port);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(list_object);
            byte[] outcoming = baos.toByteArray();
            socket.getOutputStream().write(outcoming);
            oos.close();
            baos.close();
            TCPReceiver receiver = new TCPReceiver(socket);
            receiver.receiver();
            socket.close();
        }catch (IOException e){
            System.out.println("Проблемы с передачей на сервер...");
        }
    }
}
