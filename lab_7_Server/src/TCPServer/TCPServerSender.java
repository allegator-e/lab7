package TCPServer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServerSender implements Runnable{
    private String str;
    private final SelectionKey key;
    static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(TCPServerSender.class.getName());
    }
    public TCPServerSender(String str, SelectionKey key) {
        this.str =str;
        this.key = key;
    };
    /**
    *Отправка данных клиенту
     */
    @Override
    public void run() {
        try {
            synchronized (key) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(str);
                ByteBuffer buffer = ByteBuffer.wrap(bos.toByteArray());
                socketChannel.write(buffer);
                oos.close();
                bos.close();
                buffer.clear();
                key.cancel();
            }
        }catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка передачи данных" );
        }
    }
}
