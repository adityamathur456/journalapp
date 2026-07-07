package com.springboot.journalapp.service;

import com.springboot.journalapp.entity.UserEntity;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class UserArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(UserEntity.builder().userName("Michael").password("aditya4").build()),
                Arguments.of(UserEntity.builder().userName("Roman").password("brock").build())
        );
    }
}
