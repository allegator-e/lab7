package TCPServer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.logging.*;

public class TCPServerSender implements Runnable{
    private String str;
    private SelectionKey key;
    private boolean access;
    private Logger LOGGER;
    {
        LOGGER = Logger.getLogger(TCPServerSender.class.getName());
    }
    public TCPServerSender(boolean access, String str, SelectionKey key) {
        this.str =str;
        this.key = key;
        this.access = access;
    }

    /**
    *Отправка данных клиенту
     */
    @Override
    public void run() {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            ArrayList<Object> objects = new ArrayList<>();
            objects.add(access);
            objects.add(str);
            oos.writeObject(objects);
            ByteBuffer buffer = ByteBuffer.wrap(bos.toByteArray());
            socketChannel.write(buffer);
            oos.close();
            bos.close();
            buffer.clear();
        }catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Ошибка передачи данных" );
        }
    }
}
