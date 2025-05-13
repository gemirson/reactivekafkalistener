package org.com.solid.util;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface UniversalConverter<T, R> {

    // Métodos do BiDirectionalConverter original
    R convertToModel(T payload);
    T convertToPayload(R model);
    List<R> convertAllToModel(List<T> payloads);
    List<T> convertAllToPayload(List<R> models);

    // Métodos do AdvancedBiDirectionalConverter
    <N, M> void registerModelMapping(Class<N> sourceClass, Class<M> targetClass,
                                     Map<String, String> attributeMapping);
    <M> M convertTo(R source, Class<M> targetClass);
    <M> M convertTo(R source, Class<M> targetClass, Map<String, String> dynamicMapping);
    <M> M convertWithExtras(R source, Class<M> targetClass, Map<String, Object> extraFields);
    <M> M convertNoCopy(R source, Class<M> targetClass);

    // Novos métodos combinados
    <N> N convertFrom(R source, Class<N> targetClass);
    Map<String, Object> inspectMappings();
}
