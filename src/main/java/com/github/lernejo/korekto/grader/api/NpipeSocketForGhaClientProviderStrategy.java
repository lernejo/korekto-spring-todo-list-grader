package com.github.lernejo.korekto.grader.api;

import com.github.lernejo.korekto.toolkit.misc.SubjectForToolkitInclusion;
import org.apache.commons.lang3.SystemUtils;
import org.testcontainers.dockerclient.DockerClientProviderStrategy;
import org.testcontainers.dockerclient.NpipeSocketClientProviderStrategy;
import org.testcontainers.dockerclient.TransportConfig;

import java.net.URI;

/**
 * Copy of NpipeSocketClientProviderStrategy, but changing the path, as the official docker installer for GHA sets
 * it elsewhere: <a href="https://github.com/docker/actions-toolkit/blob/main/src/docker/install.ts#L441">hardcoded host</a>
 */
@SubjectForToolkitInclusion
public class NpipeSocketForGhaClientProviderStrategy extends DockerClientProviderStrategy {

    public static final int PRIORITY = NpipeSocketClientProviderStrategy.PRIORITY + 1;
    protected static final String DOCKER_SOCK_PATH = "//./pipe/setup_docker_action";
    private static final String SOCKET_LOCATION = "npipe://" + DOCKER_SOCK_PATH;

    @Override
    public TransportConfig getTransportConfig() {
        return TransportConfig.builder().dockerHost(URI.create(SOCKET_LOCATION)).build();
    }

    @Override
    protected boolean isApplicable() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @Override
    public String getDescription() {
        return "local Npipe socket (" + SOCKET_LOCATION + ")";
    }

    @Override
    protected int getPriority() {
        return PRIORITY;
    }
}
