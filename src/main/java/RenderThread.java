import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RenderThread implements Runnable {
    private Socket socket;

    RenderThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            String request = in.lines().collect(Collectors.joining("\n"));

            out.write("You passed " + request.length() + "chars\n");
            System.out.println("Passed " + request.length() + "chars");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
