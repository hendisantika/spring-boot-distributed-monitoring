package id.my.hendisantika.distributedmonitoring.infra;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-monitoring
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 14/03/25
 * Time: 06.24
 * To change this template use File | Settings | File Templates.
 */
@Observed
public interface UserJPARepository extends JpaRepository<UserTRecord, UUID> {
    Optional<UserTRecord> findByEmail(String email);
}
