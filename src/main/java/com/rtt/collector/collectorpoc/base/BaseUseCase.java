package com.rtt.collector.collectorpoc.base;

public abstract class BaseUseCase<T, P extends BaseUseCase.UseCaseParameters> {

    public abstract T execute(P parameters) throws Exception;

    public T execute() throws Exception {
        return execute(null);
    }

    public interface UseCaseParameters {}
}
