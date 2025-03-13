package id.my.hendisantika.distributedmonitoring.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-monitoring
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 14/03/25
 * Time: 06.32
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@ImportRuntimeHints(NativeConfig.RuntimeHints.class)
public class NativeConfig {

    static class RuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(org.springframework.aot.hint.RuntimeHints hints, ClassLoader classLoader) {
            // Register resource pattern
            hints.resources().registerPattern("logback-log4j2.xml");

            // Register ObservationRegistry and ObservedAspect
            hints.reflection().registerType(ObservationRegistry.class, MemberCategory.values());
            hints.reflection().registerType(ObservedAspect.class, MemberCategory.values());

            // Register MySQL dialect
            hints.reflection().registerTypeIfPresent(classLoader, "org.hibernate.dialect.MySQLDialect",
                    hint -> hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INTROSPECT_PUBLIC_METHODS)
                            .onReachableType(MySQLDialect.class));

            // Register MySQL-related JDBC class if needed
            hints.reflection().registerTypeIfPresent(classLoader, "com.mysql.cj.jdbc.Driver",
                    hint -> hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INTROSPECT_PUBLIC_METHODS));
        }
    }
}
