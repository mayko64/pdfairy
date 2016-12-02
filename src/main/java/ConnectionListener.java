import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConnectionListener {
    private boolean isStopped = false;

    void listen(int threads, String host, int port) throws IOException {
        InetAddress bindAddr = InetAddress.getByName(host);
        ServerSocket serverSocket = new ServerSocket(port, threads, bindAddr);

        registerShutdownHook();
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);

        while (!isStopped()) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped 1");
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            threadPool.execute(new RenderThread(clientSocket));
        }

        threadPool.shutdown();
        serverSocket.close();

        System.out.println("Server Stopped 2");
    }

    private synchronized void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isStopped = true;
//            Thread.currentThread().interrupt();
        }));
    }

    private synchronized boolean isStopped() {
        return isStopped;
    }
}
