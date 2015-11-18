package qwerty;

import org.apache.hadoop.fs.Path;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;


import org.eclipse.jetty.security.HashLoginService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static qwerty.SecuredContext.getConstraintSecurityHandler;
import static qwerty.SecuredContext.getHashLoginService;

public class Main {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);
        HashLoginService loginService = getHashLoginService();
        server.addBean(loginService);

        ContextHandler context = new ContextHandler("/workflows/");

        ConstraintSecurityHandler security = getConstraintSecurityHandler(loginService);

        final ZZHandler helloHandler = new ZZHandler();

        context.setHandler(helloHandler);
        security.setHandler(context);
        server.setHandler(security);

        server.start();
        server.join();
    }


    public static void main2(String[] args ) throws Exception {

        final InputStream inputStream = Files.newInputStream(Paths.get("/home/obaskakov/ttt.txt"));

        Path file = new Path("/user/obaskakov/table.html");

        WriteOnHDFS.apply(inputStream, file);



    }

    }
