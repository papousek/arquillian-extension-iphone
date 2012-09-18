package org.jboss.arquillian.iphone.impl;

import java.io.IOException;
import org.testng.annotations.Test;

public class WaxSimTestCase {

    @Test
    public void testBuild() throws IOException {
        new WaxSim("git://github.com/jonathanpenn/WaxSim.git/");
    }

}
