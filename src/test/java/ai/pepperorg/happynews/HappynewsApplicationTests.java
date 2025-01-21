package ai.pepperorg.happynews;

import ai.pepperorg.happynews.config.JwtRequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.profiles.active=test"})
class HappynewsApplicationTests {

	@Test
	void contextLoads() {
	}
}
