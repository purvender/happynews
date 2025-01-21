package ai.pepperorg.happynews.service.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test") // Active only in the test profile
public class MockStorageService implements StorageService {

    @Override
    public String storeImage(String imageUrl) {
        // Return a mocked image path
        return "mocked-image-path";
    }
}
