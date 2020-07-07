package com.rtt.collector.collectorpoc.base;

public abstract class ObjectMapper<D extends BaseDto, E extends BaseEntity> {

    public abstract D toDto(E entity, boolean eager);

    public abstract E toEntity(D dto, boolean eager);

    public D toDto(E entity) {
        return toDto(entity, true);
    }

    public E toEntity(D dto) {
        return toEntity(dto, true);
    }
}
