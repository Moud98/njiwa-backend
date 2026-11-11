package com.wssoftwares.ikutibnb_backend.users;

import com.wssoftwares.ikutibnb_backend.users.dto.CreateUser;
import com.wssoftwares.ikutibnb_backend.users.dto.UpdateUser;
import com.wssoftwares.ikutibnb_backend.users.dto.ChangePassword;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(CreateUser dto);

    Page<User> getAllUsers(int page, int size, String role, String fullname);

    List<User> getAllHosts();

    List<User> getAllAgents();

    List<User> getAllClients();

    User getUserById(UUID id);

    User getUserByEmail(String email);

    User updateUser(UUID id, UpdateUser dto, String reviewerEmail);

    void deleteUser(UUID id);

    User changePassword(UUID id, ChangePassword dto);

    User getUserFromToken(String token);

    Page<User> getClientsWhoRented(String fullname, int page, int size);

}
