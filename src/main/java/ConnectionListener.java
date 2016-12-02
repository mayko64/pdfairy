import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

            threadPool.execute(() -> {
                try (
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
                        );
                        BufferedWriter out = new BufferedWriter(
                                new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8)
                        )
                ) {
                    String request = in.lines().collect(Collectors.joining("\n"));

                    out.write("You passed " + request.length() + "chars\n");
                    System.out.println("Passed " + request.length() + "chars");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
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
