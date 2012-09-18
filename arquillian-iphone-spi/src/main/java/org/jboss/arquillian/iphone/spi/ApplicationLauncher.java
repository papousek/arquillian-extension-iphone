package org.jboss.arquillian.iphone.spi;

import java.io.IOException;
import org.jboss.arquillian.iphone.api.Application;

public interface ApplicationLauncher {

    void launch(Application application) throws IOException;

    int precedence();
}
