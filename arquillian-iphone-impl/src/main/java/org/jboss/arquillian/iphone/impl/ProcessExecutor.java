package org.jboss.arquillian.iphone.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProcessExecutor {

    public static Process spawn(String... commands) throws IOException {
        final Process process = new ProcessBuilder(commands).start();
        new Thread(new ConsoleConsumer(process, System.out)).start();
        Thread shutdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (process != null) {
                    process.destroy();
                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return process;
    }

    public static List<String> execute(String... commands) throws IOException {
        final Process process = new ProcessBuilder(commands).redirectErrorStream(true).start();
        ConsoleConsumer consumer = new ConsoleConsumer(process);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
        try {
            process.waitFor();
            consumerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return consumer.getOutputList();
    }

    private static class ConsoleConsumer implements Runnable {

        private final Process process;
        private final PrintStream log;
        private final List<String> outputList = new ArrayList<String>();
        private static final String NL = System.getProperty("line.separator");

        public ConsoleConsumer(Process process) {
            this.process = process;
            this.log = null;
        }

        public ConsoleConsumer(Process process, PrintStream log) {
            this.process = process;
            this.log = log;
        }

        public List<String> getOutputList() {
            return outputList;
        }

        public void run() {
            final InputStream stream = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            int i;
            StringBuilder line = new StringBuilder();
            try {
                while ((i = reader.read()) != -1) {
                    char c = (char) i;
                    // add the character
                    line.append(c);
                    if (log != null) {
                        log.print(c);
                    }
                    // save output
                    if (line.indexOf(NL) != -1) {
                        outputList.add(line.toString());
                        line = new StringBuilder();
                    }
                }
                if (line.length() > 1) {
                    outputList.add(line.toString());
                }
            } catch (IOException ignored) {
            }
        }
    }
}
