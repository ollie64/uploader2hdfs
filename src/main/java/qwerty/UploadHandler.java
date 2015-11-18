package qwerty;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.eclipse.jetty.server.handler.AbstractHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;


public class UploadHandler extends AbstractHandler {

    final String greeting = "dsadsad";


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

            System.out.println(request.getParameter("ololo"));

            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);

            ServletInputStream inputStream = request.getInputStream();

            System.out.println(">> request.getRequestURI():");
            System.out.println(request.getRequestURI());
            System.out.println(">> secret =");
            System.out.println(request.getParameter("secret"));


            Path file = new Path("/user/obaskakov/table.html");

            WriteOnHDFS.apply(inputStream, file);

    //        System.out.println("inputStream.read():");
    //        System.out.println(IOUtils.toString(inputStream));

            PrintWriter out = response.getWriter();
            out.println("<h1>" + greeting + "</h1>");

            baseRequest.setHandled(true);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
