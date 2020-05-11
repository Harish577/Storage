package com.example.Sample2;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class Sample2Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Sample2Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        storeReports(args[0],args[1],args[2],args[3]);
    }

    public static void storeReports(String projectId,String bucketName,String jsonPath,String directoryPath) throws Exception {

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).setCredentials(GoogleCredentials.fromStream(new FileInputStream(jsonPath))).build().getService();
        try(Stream<Path> walk = Files.walk(Paths.get(directoryPath)))
        {
            List<String>  resultDirectory = walk.filter(Files::isRegularFile).map(x->x.toAbsolutePath().toString()).collect(Collectors.toList());
            resultDirectory.forEach(it -> {

                System.out.println(it);
                 String obj=it.replace(directoryPath, " ");
                System.out.println( "After"+obj);
                String obj1 = obj.replaceAll("\\\\","/");
                System.out.println(obj1);
                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, "colleges/"+obj1).build();
                try {
                    storage.create(blobInfo, Files.readAllBytes(Paths.get(it)));
                }catch (Exception e)
                {
                    System.out.println(e);
                }

            });


        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
}

