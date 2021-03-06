package org.intellift.sol.controller.querydsl.api;

import com.querydsl.core.types.Predicate;
import javaslang.control.Try;
import org.intellift.sol.controller.api.CrudApiController;
import org.intellift.sol.domain.Identifiable;
import org.intellift.sol.mapper.PageMapper;
import org.intellift.sol.service.querydsl.QueryDslCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface QueryDslCrudApiController<E extends Identifiable<ID>, D extends Identifiable<ID>, ID extends Serializable> extends CrudApiController<E, D, ID> {

    @Override
    PageMapper<E, D> getMapper();

    @Override
    default PageMapper<E, D> getReferenceMapper() {
        return getMapper();
    }

    @Override
    QueryDslCrudService<E, ID> getService();

    @GetMapping
    ResponseEntity<Page<D>> getAll(Predicate predicate, Pageable pageable);

    default ResponseEntity<Page<D>> getAllDefaultImplementation(final Predicate predicate, final Pageable pageable) {
        final BiFunction<Predicate, Pageable, Try<ResponseEntity<Page<D>>>> getAll = QueryDslCrudApiDefaultImpl.getAll(
                getService()::findAll,
                getMapper()::mapTo
        );

        return getAll.apply(predicate, pageable)
                .onFailure(throwable -> getLogger().error("Error occurred while processing GET request", throwable))
                .getOrElse(() -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }
}
