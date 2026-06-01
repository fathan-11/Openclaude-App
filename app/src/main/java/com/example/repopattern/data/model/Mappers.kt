package com.example.repopattern.data.model

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    avatarUrl = avatar
)

fun UserDto.toDomain(): User = User(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    avatarUrl = avatar
)

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    avatarUrl = avatarUrl
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    avatarUrl = avatarUrl
)

fun List<UserDto>.toEntities(): List<UserEntity> = map { it.toEntity() }
fun List<UserEntity>.toDomainList(): List<User> = map { it.toDomain() }
