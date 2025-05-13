package org.com.solid.gateway;

import org.springframework.messaging.MessageHeaders;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageHeaderConverter {

    /**
     * Converte MessageHeaders para Map<String, String>
     * @param headers MessageHeaders a ser convertido
     * @return Mapa com chaves e valores convertidos para String
     */
    public static Map<String, String> convertHeadersToStringMap(MessageHeaders headers) {
        Map<String, String> resultMap = new HashMap<>();

        if (headers == null || headers.isEmpty()) {
            return resultMap;
        }

        headers.forEach((key, value) -> {
            if (key != null && value != null) {
                resultMap.put(key.toString(), convertHeaderValue(value));
            }
        });

        return resultMap;
    }

    /**
     * Converte um valor de header para String de forma segura
     * @param value Objeto a ser convertido
     * @return Representação String do valor
     */
    private static String convertHeaderValue(Object value) {
        if (value == null) {
            return "";
        }

        // Casos especiais para tipos comuns
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof byte[]) {
            return bytesToHex((byte[]) value);
        } else if (value instanceof Iterable) {
            return iterableToString((Iterable<?>) value);
        } else {
            // Fallback para outros tipos
            return Objects.toString(value, "");
        }
    }

    /**
     * Converte array de bytes para representação hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Converte um Iterable para String delimitada por vírgula
     */
    private static String iterableToString(Iterable<?> iterable) {
        StringBuilder sb = new StringBuilder();
        iterable.forEach(item -> {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(Objects.toString(item, ""));
        });
        return sb.toString();
    }
}
