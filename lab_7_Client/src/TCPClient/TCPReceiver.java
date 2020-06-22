package TCPClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
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
            ArrayList<Object> accessAndStroka = (ArrayList<Object>) ois.readObject();
            ois.close();

            System.out.println((String) accessAndStroka.get(1));
            return (Boolean) accessAndStroka.get(0);
        }catch (IOException|ClassNotFoundException e) {
            System.out.println("В процессе получения данных с сервера возникла ошибка.");
            return false;
        }
    }
}
