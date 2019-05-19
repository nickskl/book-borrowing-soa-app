package ru.nikolay.remote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;

public interface RemoteService<Request, Response> {
    String getUrl(String path);

    Response findOne(String path, Object... urlVariables);

    List<Response> findAll(String path, Object... urlVariables);

    Page<HashMap<String, Object>> findAllPaged(String path, Pageable pageable, Object... urlVariables);

    Response create(String path, Request request, Object... urlVariables);

    void update(String path, Request request, Object... urlVariables);

    void delete(String path, Object... urlVariables);
}
