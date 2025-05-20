package com.paulograbin.vue;

import io.javalin.http.Context;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class BeforeRequestFilter {

    public static void handleBeforeRequest(@NotNull Context context) throws ServletException, IOException {
        HttpServletRequest req = context.req();
        String requestURI = req.getRequestURI();
        HttpServletResponse res = context.res();

        // Request URI includes the contextPath if any, removed it.
        String path = requestURI.substring(req.getContextPath().length());
        if (!path.startsWith("/api") && !path.startsWith("/management") && !path.contains(".") && path.matches("/(.*)")) {
            req.getRequestDispatcher("/index.html").forward(req, res);
        }
    }
}
