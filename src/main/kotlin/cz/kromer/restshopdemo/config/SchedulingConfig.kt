package cz.kromer.restshopdemo.config

import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import javax.sql.DataSource

@ConditionalOnProperty(value = ["app.scheduling.enable"], havingValue = "true", matchIfMissing = true)
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
class SchedulingConfig {

    @Bean
    fun lockProvider(dataSource: DataSource) = JdbcTemplateLockProvider(dataSource)
}