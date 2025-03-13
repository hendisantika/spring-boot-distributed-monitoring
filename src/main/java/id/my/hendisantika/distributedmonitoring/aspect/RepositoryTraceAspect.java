package id.my.hendisantika.distributedmonitoring.aspect;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-monitoring
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 14/03/25
 * Time: 06.30
 * To change this template use File | Settings | File Templates.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryTraceAspect {

    private final Tracer tracer;

    @Pointcut("execution(* org.springframework.data.repository.Repository+.*(..))")
    public void repositoryMethods() {
    }

    @Before("repositoryMethods()")
    public void beforeRepositoryCall() {
        // Start the span for repository method call
        tracer.currentSpan().tag("repository.call", "start");
    }

    @After("repositoryMethods()")
    public void afterRepositoryCall() {
        // Close the span after the repository method call
        tracer.currentSpan().tag("repository.call", "end");
    }
}
