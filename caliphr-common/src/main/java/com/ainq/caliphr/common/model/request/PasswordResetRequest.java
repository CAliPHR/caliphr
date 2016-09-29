package com.ainq.caliphr.common.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by mmelusky on 9/22/2015.
 */
@Data
public class PasswordResetRequest {

    private Integer userId;

    @NotNull
    @Size(min=4)
    private String newPassword;

    @NotNull
    @Size(min=4)
    private String newPasswordConfirm;

    private String passwordHash;

    private String token;

}
