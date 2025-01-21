package ai.pepperorg.happynews.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
@Profile("prod") // Active in production profile
public class S3StorageService implements StorageService {

    private final AmazonS3 s3Client;
    private final String bucket;

    public S3StorageService(
            @Value("${app.storage.s3.region}") String region,
            @Value("${app.storage.s3.bucket}") String bucket) {

        // Build the AmazonS3 client using default credentials (IAM role)
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .build();
        this.bucket = bucket;

        // Ensure the bucket exists
        if (!s3Client.doesBucketExistV2(bucket)) {
            s3Client.createBucket(bucket);
        }
    }

    @Override
    public String storeImage(String imageUrl) {
        try {
            // Open a connection to the image URL
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true); // Ensure redirections are handled

            // Download the image fully into a buffer
            try (InputStream in = connection.getInputStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[8192]; // Buffer size
                int bytesRead;
                while ((bytesRead = in.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }

                // Metadata for S3 upload
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(buffer.size()); // Set the correct size
                metadata.setContentType(connection.getContentType()); // Use correct MIME type

                // Generate a unique name for the object
                String objectName = UUID.randomUUID().toString();

                // Upload to S3
                s3Client.putObject(bucket, objectName, new ByteArrayInputStream(buffer.toByteArray()), metadata);
                log.info("Uploaded image to S3 with object name: {}", objectName);

                return objectName; // Return the object name for reference
            }
        } catch (Exception e) {
            log.error("Error storing image to S3: {}", imageUrl, e);
            return null; // Return null if upload fails
        }
    }
}
