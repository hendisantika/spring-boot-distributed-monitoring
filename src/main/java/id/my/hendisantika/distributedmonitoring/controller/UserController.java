package id.my.hendisantika.distributedmonitoring.controller;

import id.my.hendisantika.distributedmonitoring.core.UserService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-monitoring
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 14/03/25
 * Time: 06.36
 * To change this template use File | Settings | File Templates.
 */
@Observed
@RestController
@RequestMapping("api/users")
@Tag(name = "User Rest API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
}
