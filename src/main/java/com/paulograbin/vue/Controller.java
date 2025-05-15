package com.paulograbin.vue;

import io.javalin.http.Context;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    public static void getAccount(@NotNull Context context) {
        LOG.info("Account...");

        UserVM user = new UserVM("holymaster", Set.of("ROLE_USER", "ROLE_ADMIN"), Map.of("Holy", "Master"));
        //        UserVM user = new UserVM("paulo", Set.of("ROLE_USER"), Map.of("name", "Paulo"));

        context.json(user);
    }

    private static class UserVM {

        private String login;
        private Set<String> authorities;
        private Map<String, Object> details;

        UserVM(String login, Set<String> authorities, Map<String, Object> details) {
            this.login = login;
            this.authorities = authorities;
            this.details = details;
        }

        public boolean isActivated() {
            return true;
        }

        public Set<String> getAuthorities() {
            return authorities;
        }

        public String getLogin() {
            return login;
        }

        public Map<String, Object> getDetails() {
            return details;
        }
    }

    public static void getInfo(@NotNull Context context) {
        LOG.info("Info...");
    }

    public static void getHome(@NotNull Context context) {
        context.contentType("text/html");
        InputStream resourceAsStream = VueExperimentApp.class.getResourceAsStream("/index.html");
        context.result(resourceAsStream);
    }
}
