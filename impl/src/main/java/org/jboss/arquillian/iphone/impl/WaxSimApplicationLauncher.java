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
package org.jboss.arquillian.iphone.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.iphone.api.Application;
import org.jboss.arquillian.iphone.api.ApplicationLauncher;
import org.jboss.arquillian.iphone.util.ProcessExecutor;

/**
 * @author <a href="jpapouse@redhat.com">Jan Papousek</a>
 */
public class WaxSimApplicationLauncher implements ApplicationLauncher {

    private static final String DEFAULT_COMMAND = "waxsim";

    private final String command;
    private final IPhoneConfiguration configuration;

    public WaxSimApplicationLauncher(IPhoneConfiguration configuration) {
        this.command = DEFAULT_COMMAND;
        this.configuration = configuration;
    }

    public WaxSimApplicationLauncher(IPhoneConfiguration configuration, File binary) {
        if (!binary.exists()) {
            throw new IllegalArgumentException("<" + binary.getAbsolutePath() + "> doesn't exist.");
        }
        this.command = binary.getAbsolutePath();
        this.configuration = configuration;
    }

    @Override
    public void launch(Application application) throws IOException {
        if (!application.getLocation().exists()) {
            throw new IllegalArgumentException("<" + application.getLocation().getAbsolutePath() + "> doesn't exist.");
        }
        List<String> commands = new ArrayList<String>();
        commands.add(command);
        if (configuration.getSdk() != null) {
            commands.add("-s");
            commands.add(configuration.getSdk());
        }
        if (configuration.getVideoFile() != null) {
            commands.add("-v");
            commands.add(configuration.getVideoFile().getAbsolutePath());
        }
        commands.add(application.getLocation().getAbsolutePath());
        String[] commandsArray = new String[commands.size()];

        ProcessExecutor.spawn(commands.toArray(commandsArray));
    }

}
