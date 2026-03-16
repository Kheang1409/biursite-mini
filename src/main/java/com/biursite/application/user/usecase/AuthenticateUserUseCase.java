package com.biursite.application.user.usecase;

public interface AuthenticateUserUseCase {
    String execute(String username, String password);
}
