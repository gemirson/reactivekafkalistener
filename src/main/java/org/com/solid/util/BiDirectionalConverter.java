package org.com.solid.util;

import java.util.List;
import java.util.Map;


public interface BiDirectionalConverter<T, R> {
    R convertToModel(T payload);
    T convertToPayload(R model);

    default List<R> convertAllToModel(List<T> payloads) {
        return payloads.stream().map(this::convertToModel).toList();
    }

    default List<T> convertAllToPayload(List<R> models) {
        return models.stream().map(this::convertToPayload).toList();
    }

    /**
     * Registra um mapeamento entre modelos
     */
    <N, M> void registerModelMapping(Class<N> sourceClass, Class<M> targetClass,
                                     Map<String, String> attributeMapping);

    /**
     * Conversão direta entre modelos com mapeamento registrado
     */
    <N, M> M convert(N source, Class<M> targetClass);

    /**
     * Conversão com mapeamento dinâmico (sobrescreve o registrado)
     */
    <N, M> M convert(N source, Class<M> targetClass,
                     Map<String, String> dynamicMapping);

    /**
     * Adiciona campos extras durante a conversão
     */
    <N, M> M convertWithExtras(N source, Class<M> targetClass,
                               Map<String, Object> extraFields);

    /**
     * Versão otimizada que evita cópia quando possível
     */
    <N, M> M convertNoCopy(N source, Class<M> targetClass);
}
