package org.intellift.sol.controller.api;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.control.Option;
import javaslang.control.Try;
import org.intellift.sol.domain.Identifiable;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.net.URI;
import java.util.function.Function;

import static javaslang.API.*;
import static javaslang.Patterns.None;
import static javaslang.Patterns.Some;

public abstract class CrudApiDefaultImpl {

    public static <E extends Identifiable<ID>, D extends Identifiable<ID>, ID extends Serializable>
    Function1<ID, Try<ResponseEntity<D>>> getOne(final Function<ID, Try<Option<E>>> findOne,
                                                 final Function<E, D> toDTO) {
        return (final ID id) -> findOne.apply(id)
                .map(optionalEntity -> Match(optionalEntity).<ResponseEntity<D>>of(

                        Case(Some($()), entity -> ResponseEntity
                                .ok(toDTO.apply(entity))),

                        Case(None(), ResponseEntity
                                .notFound()
                                .build())));
    }

    public static <E extends Identifiable<ID>, D extends Identifiable<ID>, ID extends Serializable>
    Function1<D, Try<ResponseEntity<D>>> post(final Function<E, Try<E>> create,
                                              final Function<D, E> toEntity,
                                              final Function<E, D> toDTO,
                                              final Function<ID, URI> constructLocation) {
        return asymmetricPost(create, toEntity, toDTO, constructLocation);
    }

    public static <E extends Identifiable<ID>, D extends Identifiable<ID>, RD extends Identifiable<ID>, ID extends Serializable>
    Function1<RD, Try<ResponseEntity<D>>> asymmetricPost(final Function<E, Try<E>> create,
                                                         final Function<RD, E> toEntity,
                                                         final Function<E, D> toDTO,
                                                         final Function<ID, URI> constructLocation) {
        return (final RD dto) -> Try
                .of(() -> {
                    final E entity = toEntity.apply(dto);
                    entity.setId(null);
                    return entity;
                })
                .flatMap(create)
                .map(toDTO)
                .map(createdDto -> ResponseEntity
                        .created(constructLocation.apply(createdDto.getId()))
                        .body(createdDto));
    }

    public static <E extends Identifiable<ID>, D extends Identifiable<ID>, ID extends Serializable>
    Function2<ID, D, Try<ResponseEntity<D>>> put(final Function<ID, Try<Boolean>> exists,
                                                 final Function<E, Try<E>> create,
                                                 final Function<E, Try<E>> update,
                                                 final Function<D, E> toEntity,
                                                 final Function<E, D> toDTO,
                                                 final Function<ID, URI> constructLocation) {
        return asymmetricPut(exists, create, update, toEntity, toDTO, constructLocation);
    }

    public static <E extends Identifiable<ID>, D extends Identifiable<ID>, RD extends Identifiable<ID>, ID extends Serializable>
    Function2<ID, RD, Try<ResponseEntity<D>>> asymmetricPut(final Function<ID, Try<Boolean>> exists,
                                                            final Function<E, Try<E>> create,
                                                            final Function<E, Try<E>> update,
                                                            final Function<RD, E> toEntity,
                                                            final Function<E, D> toDTO,
                                                            final Function<ID, URI> constructLocation) {
        return (final ID id, final RD dto) -> Try
                .of(() -> {
                    final E entity = toEntity.apply(dto);
                    entity.setId(id);
                    return entity;
                })
                .flatMap(entity -> exists.apply(entity.getId())
                        .flatMap(doesExist -> doesExist

                                ? update.apply(entity)
                                .map(toDTO)
                                .map(ResponseEntity::ok)

                                : create.apply(entity)
                                .map(toDTO)
                                .map(persistedDto -> ResponseEntity
                                        .created(constructLocation.apply(persistedDto.getId()))
                                        .body(persistedDto))));
    }

    public static <ID extends Serializable>
    Function1<ID, Try<ResponseEntity<Void>>> delete(final Function<ID, Try<Void>> delete) {
        return (final ID id) -> delete.apply(id)
                .map(ignored -> ResponseEntity
                        .noContent()
                        .<Void>build());
    }
}
