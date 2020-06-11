package TCPClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class TCPReceiver {
    private Socket socket;
    public TCPReceiver(Socket socket){
        this.socket = socket;
    }
    /**
     * Класс для получения данных с сервера
     */
    public void receiver() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String stroka = (String) ois.readObject();
            ois.close();
            System.out.println(stroka);
        }catch (IOException|ClassNotFoundException e) {
            System.out.println("В процессе получения данных с сервера возникла ошибка.");
        }
    }
}
