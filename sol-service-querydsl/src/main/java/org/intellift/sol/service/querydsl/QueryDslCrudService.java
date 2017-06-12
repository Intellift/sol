package org.intellift.sol.service.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.intellift.sol.domain.Identifiable;
import org.intellift.sol.domain.querydsl.repository.QueryDslRepository;
import org.intellift.sol.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Objects;

import static io.vavr.API.Option;
import static io.vavr.API.Try;

/**
 * @author Achilleas Naoumidis, Chrisostomos Bakouras
 */
public interface QueryDslCrudService<E extends Identifiable<ID>, ID extends Serializable> extends CrudService<E, ID> {

    @Override
    QueryDslRepository<E, ID> getRepository();

    default Try<Stream<E>> findAll(final Predicate predicate) {
        return Try(() -> Stream.ofAll(getRepository().findAll(predicate)));
    }

    default Try<Stream<E>> findAll(final OrderSpecifier<?>... orders) {
        return Try(() -> Stream.ofAll(getRepository().findAll(orders)));
    }

    default Try<Stream<E>> findAll(final Predicate predicate, final Sort sort) {
        Objects.requireNonNull(sort, "sort is null");

        return Try(() -> Stream.ofAll(getRepository().findAll(predicate, sort)));
    }

    default Try<Page<E>> findAll(final Predicate predicate, final Pageable pageable) {
        return Try(() -> getRepository().findAll(predicate, pageable));
    }

    default Try<Stream<E>> findAll(final Predicate predicate, final OrderSpecifier<?>... orders) {
        return Try(() -> Stream.ofAll(getRepository().findAll(predicate, orders)));
    }

    default Try<Option<E>> findOne(final Predicate predicate) {
        return Try(() -> Option(getRepository().findOne(predicate)));
    }
}
