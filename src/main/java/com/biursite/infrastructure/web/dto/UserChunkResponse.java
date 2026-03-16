package com.biursite.infrastructure.web.dto;

import com.biursite.application.user.dto.UserDto;

import java.util.List;

public record UserChunkResponse(List<UserDto> users, boolean hasNext) {}
