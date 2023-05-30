package com.yeahbutstill.restful.service;

import com.yeahbutstill.restful.entity.User;
import com.yeahbutstill.restful.model.LoginUserRequest;
import com.yeahbutstill.restful.model.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginUserRequest request);

    void logout(User user);

}
