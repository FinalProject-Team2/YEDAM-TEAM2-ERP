package store.yd2team.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncodeTest {

	@Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void encode() {
        String raw = "12345678";
        String encoded = passwordEncoder.encode(raw);
        System.out.println(encoded);
    }
	
}
