package id.my.hendisantika.distributedmonitoring.controller;

import id.my.hendisantika.distributedmonitoring.core.UserService;
import id.my.hendisantika.distributedmonitoring.dto.UserData;
import id.my.hendisantika.distributedmonitoring.dto.UserRequest;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    @PostMapping
    public ResponseEntity<UserData> createUser(@RequestBody UserRequest userRequest) {
        UserData userData = userService.createUser(userRequest);
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserData> getUserByEmail(@PathVariable String email) {
        UserData userData = userService.getUserByEmail(email);
        return ResponseEntity.ok(userData);
    }

    @GetMapping
    public ResponseEntity<List<UserData>> getAllUsers() {
        List<UserData> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> userExists(@PathVariable UUID id) {
        boolean exists = userService.userExists(id);
        return ResponseEntity.ok(exists);
    }
}
