package com.aplazo.bnpl.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NonNull
    private String username; // For User
    @NonNull
    private String password; // For User
}
