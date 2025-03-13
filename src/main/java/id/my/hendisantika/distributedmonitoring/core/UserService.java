package id.my.hendisantika.distributedmonitoring.core;

import id.my.hendisantika.distributedmonitoring.annotation.Core;
import id.my.hendisantika.distributedmonitoring.infra.UserJPAGateway;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

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
@Core
@Observed
@RequiredArgsConstructor
public class UserService {
    private final UserJPAGateway userGateway;
}
