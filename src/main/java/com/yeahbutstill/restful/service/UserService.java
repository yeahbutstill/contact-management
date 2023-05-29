package com.yeahbutstill.restful.service;

import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.RegisterUserRequest;
import com.yeahbutstill.restful.model.UserResponse;

public interface UserService {

    void register(RegisterUserRequest request);

    UserResponse get(User user);

}
