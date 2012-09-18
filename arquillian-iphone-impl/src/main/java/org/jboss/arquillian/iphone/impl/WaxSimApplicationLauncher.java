package org.jboss.arquillian.iphone.impl;

import java.io.File;
import java.io.IOException;
import org.jboss.arquillian.iphone.api.Application;
import org.jboss.arquillian.iphone.spi.ApplicationLauncher;

public class WaxSimApplicationLauncher implements ApplicationLauncher {

    private static final String DEFAULT_COMMAND = "waxsim";

    private final String command;

    public WaxSimApplicationLauncher() {
        this.command = DEFAULT_COMMAND;
    }

    public WaxSimApplicationLauncher(File binary) {
        this.command = binary.getAbsolutePath();
    }

    public void launch(Application application) throws IOException {
        ProcessExecutor.spawn(command, application.getLocation().getAbsolutePath());
    }

    public int precedence() {
        return 10;
    }

}
