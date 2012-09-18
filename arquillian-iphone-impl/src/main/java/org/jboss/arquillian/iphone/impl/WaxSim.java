package org.jboss.arquillian.iphone.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

public class WaxSim {

    private final File binary;

    public WaxSim(File binary) {
        this.binary = binary;
    }

    public WaxSim(String gitRemote) throws IOException {
        this.binary = prepareBinary(gitRemote);
    }

    public File getBinary() {
        return binary;
    }

    private File prepareBinary(String gitRemote) throws IOException {
        File repository = null;
        try {
            repository = File.createTempFile("arq-openshift", "express");
            repository.delete();
            repository.mkdirs();
            repository.deleteOnExit();
            Git.cloneRepository().setDirectory(repository).setRemote(gitRemote).setCredentialsProvider(CredentialsProvider.getDefault());

        } catch (Exception e) {
            throw new IOException("Can't clone <" + gitRemote + ">", e);
        }
        List<String> output = ProcessExecutor.execute("xcodebuild", new File(repository, "WaxSim.xcodeproj").getAbsolutePath());
        for (String line : output) {
            System.out.println(line);
        }
        return new File(binary, "build" + File.pathSeparator + "Release" + File.pathSeparator + "waxsim");
    }
}
