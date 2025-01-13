package ai.pepperorg.happynews.service.storage;

import ai.pepperorg.happynews.service.StorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

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
        try {
            // Open a connection to the image URL
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // Read the full image into a ByteArrayOutputStream
            try (InputStream in = connection.getInputStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[8192]; // Buffer size
                int bytesRead;
                while ((bytesRead = in.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead); // Write full chunks to buffer
                }

                // Upload the image to Minio
                String objectName = UUID.randomUUID().toString();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .stream(new ByteArrayInputStream(buffer.toByteArray()), buffer.size(), -1)
                                .contentType(connection.getContentType()) // Use the correct MIME type
                                .build()
                );

                return objectName; // Return the object name for reference
            }
        } catch (Exception e) {
            log.error("Error storing image to Minio: {}", imageUrl, e);
            return null;
        }
    }

}
