package id.my.hendisantika.distributedmonitoring.infra;

import id.my.hendisantika.distributedmonitoring.annotation.Gateway;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
@Gateway
@Observed
@Component
@RequiredArgsConstructor
public class UserJPAGateway {

    private final UserJPARepository userJPARepository;

    public UserTRecord saveOrUpdate(UserTRecord user) {
        return userJPARepository.save(user);
    }

    public Optional<UserTRecord> findUserById(UUID id) {
        return userJPARepository.findById(id);
    }

    public Optional<UserTRecord> findUserByEmail(String email) {
        return userJPARepository.findByEmail(email);
    }

    public List<UserTRecord> allUsers() {
        return userJPARepository.findAll();
    }

    public void deleteById(UUID id) {
        userJPARepository.findById(id)
                .ifPresent(user -> {
                    user.setActive(false);
                    saveOrUpdate(user);
                });
    }

    public boolean userExists(UUID id) {
        return userJPARepository.existsById(id);
    }
}
