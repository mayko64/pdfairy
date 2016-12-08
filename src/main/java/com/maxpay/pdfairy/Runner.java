package com.maxpay.pdfairy;

import org.apache.commons.cli.*;

import java.io.IOException;

public class Runner {
    public static void main(String[] args) {
        Options options = new Options()
                .addOption(
                        Option.builder()
                                .longOpt("host")
                                .argName("host")
                                .hasArg()
                                .desc("Host to startListenerThreads on")
                                .type(String.class)
                                .required()
                                .build())
                .addOption(
                        Option.builder()
                                .longOpt("port")
                                .argName("port")
                                .hasArg()
                                .desc("Port to startListenerThreads on")
                                .type(Integer.class)
                                .required()
                                .build())
                .addOption(
                        Option.builder()
                                .longOpt("threads")
                                .argName("threads")
                                .hasArg()
                                .desc("Number of workers to start")
                                .type(Integer.class)
                                .build()
                );

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            String host = cmd.getOptionValue("host");
            int port = Integer.parseInt(cmd.getOptionValue("port"));
            int threads = Integer.parseInt(cmd.getOptionValue("threads", "10"));

            new SocketListener(host, port).listen(threads);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            usage(options);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("pdfairy", options);
    }
}
