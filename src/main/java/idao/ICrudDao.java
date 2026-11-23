package idao;

import java.util.List;

public interface ICrudDao<T, K> {
    void create(T entity) throws Exception;

    T findById(K id) throws Exception;

    List<T> findAll(boolean incluirInactivos) throws Exception;

    void update(T entity) throws Exception;

    void deleteLogico(K id) throws Exception;

    void deleteFisico(K id) throws Exception;
}
