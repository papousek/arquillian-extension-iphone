/**
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.ios.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="jpapouse@redhat.com">Jan Papousek</a>
 */
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
        return execute(null, commands);
    }

    public static List<String> execute(File workingDirectory, String... commands) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(commands).redirectErrorStream(true);
        if (workingDirectory != null) {
            pb.directory(workingDirectory);
        }
        final Process process = pb.start();
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
