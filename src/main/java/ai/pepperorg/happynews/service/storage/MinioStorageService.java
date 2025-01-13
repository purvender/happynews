package ai.pepperorg.happynews.service.storage;

import ai.pepperorg.happynews.service.StorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
@Profile("dev")
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioStorageService(
            @Value("${app.storage.minio.endpoint}") String endpoint,
            @Value("${app.storage.minio.accessKey}") String accessKey,
            @Value("${app.storage.minio.secretKey}") String secretKey,
            @Value("${app.storage.minio.bucket}") String bucket) {

        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucket = bucket;

        try {
            boolean found = minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(io.minio.MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch(Exception e) {
            throw new RuntimeException("Could not initialize Minio bucket", e);
        }
    }

    @Override
    public String storeImage(String imageUrl) {
        try(InputStream in = new URL(imageUrl).openStream()) {
            String objectName = UUID.randomUUID().toString();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(in, in.available(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );
            return objectName;
        } catch(Exception e) {
            log.error("Error storing image to Minio", e);
            return null;
        }
    }
}
