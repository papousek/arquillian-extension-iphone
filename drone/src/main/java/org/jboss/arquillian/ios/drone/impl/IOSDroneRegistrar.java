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
package org.jboss.arquillian.ios.drone.impl;

import java.io.IOException;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.drone.api.annotation.Default;
import org.jboss.arquillian.ios.api.Application;
import org.jboss.arquillian.ios.api.ApplicationLauncher;
import org.jboss.arquillian.ios.spi.event.IOSDroneConfigured;
import org.jboss.arquillian.ios.spi.event.IOSDroneReady;
import org.jboss.arquillian.ios.spi.event.IOSReady;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;

/**
 * @author <a href="jpapouse@redhat.com">Jan Papousek</a>
 */
public class IOSDroneRegistrar {

    @Inject
    private Event<IOSDroneReady> ready;
    @Inject
    private Event<IOSDroneConfigured> configured;
    @Inject
    @SuiteScoped
    private InstanceProducer<IOSDroneConfiguration> configuration;

    public void configure(@Observes IOSReady event, ArquillianDescriptor descriptor) {
        configuration.set(new IOSDroneConfiguration().configure(descriptor, Default.class));
        configured.fire(new IOSDroneConfigured());
    }

    public void register(@Observes IOSDroneConfigured event, ApplicationLauncher launcher) throws IOException, InterruptedException {
        Application application = new IPhoneDriverApplication(configuration.get().getLocalSeleniumCopy(), IPhoneDriverApplication.SVN_TRUNK);
        launcher.launch(application);
        Thread.sleep(1000); // FIXME
        ready.fire(new IOSDroneReady());
    }

}
