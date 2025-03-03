package ru.sfedu.teamselection.mapper;

import java.util.List;

/**
 * Interface for classes designed for mapping entity objects to their corresponding dto classes
 * @param <D> Dto class
 * @param <E> Entity class
 */
public interface DtoListMapper<D, E> {
    /**
     * Map Dto to Entity
     * @param dto Dto object to be mapped
     * @return mapped entity
     */
    List<E> mapListToEntity(List<D> dto);

    /**
     * Map Entity to Dto
     * @param entity Entity object to be mapped
     * @return mapped dto
     */
    List<D> mapListToDto(List<E> entity);
}
