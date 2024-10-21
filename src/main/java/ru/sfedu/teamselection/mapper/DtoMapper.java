package ru.sfedu.teamselection.mapper;

/**
 * Interface for classes designed for mapping entity objects to their corresponding dto classes
 * @param <D> Dto class
 * @param <E> Entity class
 */
public interface DtoMapper<D, E> {
    /**
     * Map Dto to Entity
     * @param dto Dto object to be mapped
     * @return mapped entity
     */
    E mapToEntity(D dto);

    /**
     * Map Entity to Dto
     * @param entity Entity object to be mapped
     * @return mapped dto
     */
    D mapToDto(E entity);
}
