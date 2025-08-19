package ru.stroy1click.user.mapper;

public interface Mappable<E, D>{

    E toEntity(D d);

    D toDto(E e);
}
