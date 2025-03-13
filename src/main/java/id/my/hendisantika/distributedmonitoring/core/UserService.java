package id.my.hendisantika.distributedmonitoring.core;

import id.my.hendisantika.distributedmonitoring.annotation.Core;
import id.my.hendisantika.distributedmonitoring.dto.UserData;
import id.my.hendisantika.distributedmonitoring.dto.UserRequest;
import id.my.hendisantika.distributedmonitoring.infra.UserJPAGateway;
import id.my.hendisantika.distributedmonitoring.infra.UserTRecord;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    public UserData createUser(UserRequest userRequest) {
        UserTRecord userTRecord = userGateway.saveOrUpdate(
                buildUserTRecord(userRequest)
        );
        return buildUserData(userTRecord);
    }

    public UserData updateUser(UserRequest userRequest) {
        return null;
    }

    public UserData getUserByEmail(String email) {
        return userGateway.findUserByEmail(email)
                .map(this::buildUserData)
                .orElse(null);
    }

    public List<UserData> getAllUsers() {
        return userGateway.allUsers()
                .stream()
                .map(this::buildUserData)
                .toList();
    }

    public void deleteUser(UUID id) {
        userGateway.deleteById(id);
    }

    public boolean userExists(UUID id) {
        return userGateway.userExists(id);
    }

    private static String generateUniqueUsername(String firstName, String lastName) {
        String normalizedFirstName = firstName.trim().toLowerCase().replaceAll("\\s+", "");
        String normalizedLastName = lastName.trim().toLowerCase().replaceAll("\\s+", "");
        int randomNumber = new Random().nextInt(9000) + 1000;
        return normalizedFirstName + normalizedLastName + randomNumber;
    }

    private UserTRecord buildUserTRecord(UserRequest userRequest) {
        return UserTRecord.builder()
                .email(userRequest.getEmail())
                .username(generateUniqueUsername(userRequest.getFirstName(), userRequest.getLastName()))
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .password(userRequest.getPassword())
                .build();
    }
}
