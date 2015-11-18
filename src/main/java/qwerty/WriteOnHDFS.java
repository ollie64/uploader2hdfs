package qwerty;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Progressable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class WriteOnHDFS {


    static void apply(InputStream inputStream, Path file) throws URISyntaxException, IOException {

        final String NAME_NODE = "hdfs://nn001.ewr004.collective-media.net:8022";

        Configuration configuration = new Configuration();

        configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        configuration.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

        configuration.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        configuration.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));

        UserGroupInformation.setConfiguration(configuration);

        FileSystem hdfs = FileSystem.get(new URI(NAME_NODE), configuration );

        if ( hdfs.exists( file )) {
            hdfs.delete( file, true );
        }

        OutputStream os = hdfs.create( file, new Progressable() {
            public void progress() {
                System.out.println("...bytes written: [ few ]");
            } });

        IOUtils.copy(inputStream, os);
        os.close();
        hdfs.close();


    }



}
