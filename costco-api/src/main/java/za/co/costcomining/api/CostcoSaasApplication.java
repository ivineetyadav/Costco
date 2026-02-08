package za.co.costcomining.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "za.co.costcomining")
@EntityScan(basePackages = "za.co.costcomining.common.entity")
@EnableJpaRepositories(basePackages = {
    "za.co.costcomining.api.repository",
    "za.co.costcomining.iot"
})
@EnableScheduling
public class CostcoSaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CostcoSaasApplication.class, args);
    }
}
