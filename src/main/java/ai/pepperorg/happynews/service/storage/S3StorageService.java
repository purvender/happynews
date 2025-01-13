package ai.pepperorg.happynews.service.storage;

import ai.pepperorg.happynews.service.StorageService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
@Profile("prod")
public class S3StorageService implements StorageService {

    private final AmazonS3 s3Client;
    private final String bucket;

    public S3StorageService(
            @Value("${app.storage.s3.region}") String region,
            @Value("${app.storage.s3.accessKey}") String accessKey,
            @Value("${app.storage.s3.secretKey}") String secretKey,
            @Value("${app.storage.s3.bucket}") String bucket) {

        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
        this.bucket = bucket;

        if (!s3Client.doesBucketExistV2(bucket)) {
            s3Client.createBucket(bucket);
        }
    }

    @Override
    public String storeImage(String imageUrl) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            String key = UUID.randomUUID().toString();
            s3Client.putObject(bucket, key, in, null);
            return key;
        } catch(Exception e) {
            log.error("Error storing image to S3", e);
            return null;
        }
    }
}
