package TCPClient;

import Object.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Класс для отправки данных на сервер.
 */

public class TCPSender {

    private Flat flat;
    private NewFlat newFlat = new NewFlat();
    private int key;
    private String script;
    private Transport transport;
    private Socket socket;
    private String hostname;
    private boolean access;
    private ArrayList<String> loginAndPassword;
    private int port;
    private ArrayList<File> scripts = new ArrayList<>();

    public TCPSender(String hostname, int port, boolean access, ArrayList<String> loginAndPassword) {
        this.hostname = hostname;
        this.port = port;
        this.access = access;
        this.loginAndPassword = loginAndPassword;
    }

    public boolean isAccess() {
        return access;
    }

    public void checker(String[] command) throws IOException {
        switch (command[0]) {
            case "insert":
            case "update_id":
                if (command.length == 2) {
                    try {
                        key = Integer.parseInt(command[1]);
                        flat = newFlat.newFlat();
                        sender(command[0] + " " + command[1], flat);
                    } catch (NumberFormatException ex) {
                        System.out.println("Аргумент не является значением типа Integer");
                    }
                } else System.out.println("Комманда некорректна.");
                break;
            case "remove_greater":
                flat = newFlat.newFlat();
                sender(command[0], flat);
                break;
            case "remove_greater_key":
            case "remove_key":
                if (command.length == 2) {
                    try {
                        key = Integer.parseInt(command[1]);
                        sender(command[0], key);
                    } catch (NumberFormatException ex) {
                        System.out.println("Аргумент не является значением типа Integer");
                    }
                } else System.out.println("Комманда некорректна.");
                break;
            case "count_by_transport":
                if (command.length == 2) {
                    try {
                        command[1] = command[1].toUpperCase();
                        transport = Transport.valueOf(command[1]);
                        sender(command[0], transport);
                    } catch (IllegalArgumentException | NullPointerException ex) {
                        System.out.println("Значение поля Transport некорректно. Возможные значения: FEW, NONE, LITTLE, NORMAL, ENOUGH.");
                    }
                } else System.out.println("Комманда некорректна.");
                break;
            case "execute_script":
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
                        }
                    }
                } else System.out.println("Команда введена некорректно.");
                break;
            default:
                sender(command[0], "mew");
                break;
        }
    }

    private void sender(Object object, Object argument){
        try {
            ArrayList <Object> listObject = new ArrayList<>();
            listObject.add(access);
            listObject.add(loginAndPassword);
            listObject.add(object);
            listObject.add(argument);
            socket = new Socket(hostname, port);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(listObject);
            byte[] outcoming = baos.toByteArray();
            socket.getOutputStream().write(outcoming);
            oos.close();
            baos.close();
            TCPReceiver receiver = new TCPReceiver(socket);
            access = receiver.receiver();
            socket.close();
        }catch (IOException e){
            System.out.println("Проблемы с передачей на сервер...");
        }
    }
}
