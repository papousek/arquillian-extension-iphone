package org.jboss.arquillian.iphone.spi;

import javax.imageio.IIOException;

public interface IPhoneApplicationLauncher {

    void launch(IPhoneApplication application) throws IIOException;

}
