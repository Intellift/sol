package org.intellift.sol.sdk.client.internal;

import org.springframework.core.ParameterizedTypeReference;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;

public class PageResponseTypeReference<T> extends ParameterizedTypeReference<T> {

    private final Class<?> dtoClass;

    public PageResponseTypeReference(final Class<?> dtoClass) {
        this.dtoClass = dtoClass;
    }

    @Override
    public Type getType() {
        final Type[] responseWrapperActualTypes = {dtoClass};

        return ParameterizedTypeImpl.make(
                PageResponse.class,
                responseWrapperActualTypes,
                null
        );
    }
}
