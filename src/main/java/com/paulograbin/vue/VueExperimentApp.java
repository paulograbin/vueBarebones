package com.paulograbin.vue;

import io.javalin.Javalin;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VueExperimentApp {

    private static final Logger LOG = LoggerFactory.getLogger(VueExperimentApp.class);

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.useVirtualThreads = true;
            config.http.gzipOnlyCompression();
            config.vue.vueInstanceNameInJs = "app";
        });

        app.before(ctx -> LOG.info("Before ctx: {}", ctx.path()));
        app.get("/management/info", Controller::getInfo);
        app.get("/api/account", Controller::getAccount);

        app.start(7070);

        logApplicationStartup();
    }

    private static void logApplicationStartup() {
        String protocol = "http";
        String applicationName = "Vue Barebones";
        String serverPort = "7070";
        String contextPath = "/";
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.warn("The host name could not be determined, using `localhost` as fallback");
        }
        LOG.info(
            """

            ----------------------------------------------------------
            \tApplication '{}' is running! Access URLs:
            \tLocal: \t\t{}://localhost:{}{}
            \tExternal: \t{}://{}:{}{}
            ----------------------------------------------------------""",
            applicationName,
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath
        );
    }
}
