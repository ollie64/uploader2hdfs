package qwerty;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Progressable;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import org.apache.hadoop.fs.FileSystem;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;

public class ManyContexts {

    public static void main2(String[] args ) throws Exception {

        Server server = new Server(8080);
        ContextHandler context = new ContextHandler("/");

        context.setContextPath("/");
        context.setHandler(new HelloHandler());

        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new HelloHandler());

        ContextHandler contextIT = new ContextHandler("/it");
        contextIT.setHandler(new HelloHandler());

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context, contextFR, contextIT});

        server.setHandler(contexts);

        server.start();
        server.join();
    }


    public static void main(String[] args ) throws Exception {

        final String NAME_NODE = "hdfs://nn001.ewr004.collective-media.net:8022";

        Configuration configuration = new Configuration();

        configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        configuration.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

        configuration.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        configuration.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));

        UserGroupInformation.setConfiguration(configuration);

        FileSystem hdfs = FileSystem.get(new URI(NAME_NODE), configuration );

        Path file = new Path("/user/obaskakov/table.html");
        if ( hdfs.exists( file )) { hdfs.delete( file, true ); }
        OutputStream os = hdfs.create( file,
                new Progressable() {
                    public void progress() {
                        System.out.println("...bytes written: [ "+ "few" +" ]");
                    } });
        BufferedWriter br = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );
        br.write("Hello World\n");
        br.close();
        hdfs.close();


    }

    }
