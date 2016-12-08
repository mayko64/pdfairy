package com.maxpay.pdfairy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

class SocketListener implements ConnectionListener {
    private volatile boolean isStopped = false;

    private String host;
    private int port;

    private ObjectMapper mapper;

    SocketListener(String host, int port) {
        this.host = host;
        this.port = port;

        this.mapper = new ObjectMapper();
    }

    /**
     * Listen for incoming requests
     *
     * @param threads Number of threads to start
     * @throws IOException on input/output error
     */
    public void listen(int threads) throws IOException {
        InetAddress bindAddr = InetAddress.getByName(host);
        ServerSocket serverSocket = new ServerSocket(port, threads, bindAddr);

        registerShutdownHook();
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);

        while (!isStopped()) {
            Socket socket;

            try {
                socket = serverSocket.accept();
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
                                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                        );
                        BufferedWriter out = new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                        )
                ) {
                    String requestBody = in.lines().collect(Collectors.joining("\n"));

                    Request request = mapper.readValue(requestBody, Request.class);

                    PdfWizard pdfWizard = new PdfWizard();
                    pdfWizard.render(
                            new File(request.getXml()),
                            new File(request.getXsl()),
                            new File(request.getPdf())
                    );

                    String response = "PDF is written to " + request.getPdf();
                    out.write(response + "\n");
                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        threadPool.shutdown();
        serverSocket.close();

        System.out.println("Server Stopped 2");
    }

    private static class Request {
        @JsonProperty("xml")
        @Getter
        private String xml;

        @JsonProperty("xsl")
        @Getter
        private String xsl;

        @JsonProperty("pdf")
        @Getter
        private String pdf;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isStopped = true;
            Thread.currentThread().interrupt();
        }));
    }

    private boolean isStopped() {
        return isStopped;
    }
}
