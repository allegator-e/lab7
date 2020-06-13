package TCPServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

public class TCPServerReceiver implements Callable<Object> {
    private SelectionKey key;
    private Object o;
    private ByteBuffer buffer = ByteBuffer.allocate(4096);

    public TCPServerReceiver(SelectionKey key) {
        this.key =key;
    };

    @Override
    public Object call() throws IOException, ClassNotFoundException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int count = socketChannel.read(buffer);
        if (count > -1) {
            byte[] bytes = buffer.array();
            ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(baos);
            o = ois.readObject();
            ois.close();
            baos.close();
            buffer.clear();
            key.interestOps(SelectionKey.OP_WRITE);
        }
        if (count == -1) {
            key.cancel();
        }
        return o;
    }
}