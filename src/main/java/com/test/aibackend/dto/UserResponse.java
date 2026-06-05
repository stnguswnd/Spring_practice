package com.test.aibackend.dto;

import com.test.aibackend.domain.Role;
import com.test.aibackend.domain.User;

/**
 * 사용자 응답 DTO.
 *
 * passwordHash는 어떤 경우에도 노출하지 않습니다.
 */
public record UserResponse(Long id, String username, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}
