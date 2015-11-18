package qwerty;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;


// usage: curl -u oleg:123456 http://localhost:8080/workflows/dorado/lib/ -T main.json

public class ZZHandler extends ConstraintSecurityHandler {

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response
    ) throws ServletException {
        try {
            if (!request.getMethod().equals("PUT")) {
                response.setStatus(404);
                return;
            }

            // routing
            // 1 ./$workflowName/workflow.xml -> create and upload new workflow
            // 2 ./$workflowName/lib/$libName.jar -> upload jar
            // 3 ./$workflowName/workflow.js && start execution and save config

            System.out.println(">> request.getRequestURI():");
            final String requestURI = request.getRequestURI();
            System.out.println(requestURI);

            final Path path0 = Paths.get(requestURI);
            final Path libName = path0.getFileName();
            final Path path1 = path0.getParent();
            if (!path1.getFileName().endsWith("lib")) {
                throw new ServletException("bad path: " + path1);
            }
            final Path path2 = path1.getParent();
            final Path workflowName = path2.getFileName();
            // TODO check that workflow has been created on hdfs://user/celos/app/$workflowName/

            if (!path2.getParent().getFileName().toString().equals("workflows")) {
                throw new ServletException("bad path: " + path1);
            }

            PrintWriter out = response.getWriter();
            out.println("<h1>" + workflowName + " | " + libName + "</h1>");

            baseRequest.setHandled(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
