package TCPClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class TCPReceiver {
    private Socket socket;
    public TCPReceiver(Socket socket){
        this.socket = socket;
    }
    /**
     * Класс для получения данных с сервера
     */
    public boolean receiver() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ArrayList<Object> access_and_stroka = (ArrayList<Object>) ois.readObject();
            ois.close();

            System.out.println((String) access_and_stroka.get(1));
            return (Boolean) access_and_stroka.get(0);
        }catch (IOException|ClassNotFoundException e) {
            System.out.println("В процессе получения данных с сервера возникла ошибка.");
            return false;
        }
    }
}
