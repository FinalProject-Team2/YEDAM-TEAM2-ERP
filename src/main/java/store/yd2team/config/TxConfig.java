package store.yd2team.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TxConfig {

    @Bean(name = "mybatisTxManager")
    public PlatformTransactionManager mybatisTxManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
