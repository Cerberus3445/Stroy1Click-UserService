package ru.stroy1click.user.mapper;

import java.util.List;

public interface Mappable<E, D>{

    E toEntity(D d);

    D toDto(E e);

    default List<D> toDto(List<E> e){
        return e.stream()
                .map(this::toDto)
                .toList();
    }
}
