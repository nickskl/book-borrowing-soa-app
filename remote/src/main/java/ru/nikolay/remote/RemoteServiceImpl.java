package ru.nikolay.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.nikolay.apiexception.ApiRequestValidationError;
import ru.nikolay.apiexception.ApiRequestValidationException;
import ru.nikolay.auth.ServiceCredentials;
import ru.nikolay.auth.ServiceTokens;
import ru.nikolay.responses.ErrorResponse;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

public class RemoteServiceImpl<Request, Response> implements RemoteService<Request, Response> {
    private final String baseUrl;
    private final Class<Response> responseClass;
    private final Class<Response[]> responseArrayClass;
    private final ServiceCredentials myCredentials;
    private final ServiceTokens tokens;

    public RemoteServiceImpl(String baseUrl, ServiceCredentials myCredentials, ServiceTokens tokens,
                             Class<Response> responseClass, Class<Response[]> responseArrayClass) {
        this.baseUrl = baseUrl;
        this.responseClass = responseClass;
        this.responseArrayClass = responseArrayClass;
        this.myCredentials = myCredentials;
        this.tokens = tokens;
    }

    @Override
    public String getUrl(String path) {
        return baseUrl + path;
    }

    private final RestTemplate restTemplate = new RestTemplate(
            new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));

    private RemoteServiceException remoteServiceError(HttpStatusCodeException ex) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return new RemoteServiceStatusException(objectMapper.readValue(ex.getResponseBodyAsString(),
                    ErrorResponse.class).getMessage(), ex.getStatusCode());
        }
        catch (IOException ioEx) {
            return new RemoteServiceException("An unknown error had occurred");
        }
    }

    private ApiRequestValidationException requestValidationError(HttpStatusCodeException ex) {
        ObjectMapper objectMapper = new ObjectMapper();

        JSONObject jObject =  (JSONObject)JSONValue.parse(ex.getResponseBodyAsString());
        try {

        return new ApiRequestValidationException(objectMapper.readValue((String)jObject.get("message"),
                ApiRequestValidationError.class));
        }
        catch(IOException exception) {
            throw new RemoteServiceStatusException(ex.getResponseBodyAsString(), ex.getStatusCode());
        }

    }

    public HttpEntity<Request> createEntity(Request request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (tokens != null && tokens.getAccessToken() != null && !tokens.getAccessToken().isEmpty()) {
        headers.set("Authorization", "Bearer " + tokens.getAccessToken());
    }
        return new HttpEntity<>(request, headers);
}

    public HttpEntity<Void> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (tokens != null && tokens.getAccessToken() != null && !tokens.getAccessToken().isEmpty()) {
            headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        }
        return new HttpEntity<>(headers);
    }

    private boolean refreshTokens() {
        if (tokens.getAccessToken() == null) {
            return receiveTokens();
        }

        try {
            ServiceTokens refreshed = restTemplate.postForObject(getUrl("/auth/token"),
                    tokens.getRefreshToken(), ServiceTokens.class);
            tokens.setAccessToken(refreshed.getAccessToken());
            tokens.setRefreshToken(refreshed.getRefreshToken());
            return true;
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return receiveTokens();
            }
            return false;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private boolean receiveTokens() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + myCredentials.toString());

        try {
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ServiceTokens newTokens = restTemplate.exchange(getUrl("/auth/token"), HttpMethod.GET, entity, ServiceTokens.class).getBody();
            tokens.setAccessToken(newTokens.getAccessToken());
            tokens.setRefreshToken(newTokens.getRefreshToken());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }


    @Override
    public Response findOne(String path, Object... urlVariables) {
        ResponseEntity<Response> response;

        HttpEntity<Void> entity = createEntity();

        try {
            response = restTemplate
                    .exchange(getUrl(path), HttpMethod.GET, entity, responseClass, urlVariables);
        }
        catch (ResourceAccessException ex) {
            throw new RemoteServiceException("Unable to connect to remote service[" + getUrl(path) + "]");
        }
        catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw requestValidationError(ex);
            }
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw remoteServiceError(ex);
                return findOne(path, urlVariables);
            }
            throw remoteServiceError(ex);
        }

        return response.getBody();

    }

    @Override
    public List<Response> findAll(String path, Object... urlVariables) {
        ResponseEntity<Response[]> response;

        HttpEntity<Void> entity = createEntity();

        try {
            response = restTemplate
                    .exchange(getUrl(path), HttpMethod.GET, entity, responseArrayClass, urlVariables);
            return Arrays.asList(response.getBody());
        }
        catch (ResourceAccessException ex) {
            throw new RemoteServiceException("Unable to connect to remote service[" + getUrl(path) + "]");
        }
        catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw requestValidationError(ex);
            }
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw remoteServiceError(ex);
                return findAll(path, urlVariables);
            }

            throw remoteServiceError(ex);
        }
    }

    @Override
    public Page<HashMap<String, Object>> findAllPaged(String path, Pageable pageable, Object... urlVariables) {
        ResponseEntity<RestPage<HashMap<String, Object>>> responseEntity;
        HttpEntity<Void> entity = createEntity();

        try {
            responseEntity = restTemplate.exchange(getUrl(path) +
                        "?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize() , HttpMethod.GET,
               entity, new ParameterizedTypeReference<RestPage<HashMap<String, Object>>>(){}, urlVariables);
            return responseEntity.getBody().pageImpl();
        }
        catch (ResourceAccessException ex) {
            throw new RemoteServiceAccessException("Unable to connect to remote service[" + getUrl(path) + "]", getUrl(path));
        }
        catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw requestValidationError(ex);
            }
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw remoteServiceError(ex);
                return findAllPaged(path, pageable, urlVariables);
            }

            throw remoteServiceError(ex);
        }
    }

    @Override
    public Response create(String path, Request request, Object... urlVariables) {
        ResponseEntity<Response> response;

        HttpEntity<Request> entity = createEntity(request);

        try {
            response = restTemplate.postForEntity(getUrl(path),
                    entity, responseClass, urlVariables);
            return response.getBody();
        }
        catch (ResourceAccessException ex) {
            throw new RemoteServiceException("Unable to connect to remote service[" + getUrl(path) + "]");
        }
        catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw requestValidationError(ex);
            }

            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw remoteServiceError(ex);
                return create(path, request, urlVariables);
            }
            throw remoteServiceError(ex);
        }
    }

    @Override
    public void update(String path, Request request, Object... urlVariables) {
        HttpEntity<Request> entity = createEntity(request);
        try {
            restTemplate.exchange(getUrl(path), HttpMethod.PATCH, entity,
                    Void.class, urlVariables);
        }
        catch (ResourceAccessException ex) {
            throw new RemoteServiceException("Unable to connect to remote service[" + getUrl(path) + "]");
        }
        catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw requestValidationError(ex);
            }
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw remoteServiceError(ex);
                update(path, request, urlVariables);
                return;
            }
            throw remoteServiceError(ex);
        }
    }

    @Override
    public void delete(String path, Object... urlVariables) {
        HttpEntity<Void> entity = createEntity();
        try {
            restTemplate.exchange(getUrl(path), HttpMethod.DELETE, entity, Void.class, urlVariables);
        }
        catch (ResourceAccessException ex) {
            throw new RemoteServiceException("Unable to connect to remote service[" + getUrl(path) + "]");
        }
        catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw ex;
                delete(path, urlVariables);
            }
            throw remoteServiceError(ex);
        }
        catch (RestClientException ex) {
            if (!refreshTokens()) throw ex;
            delete(path, urlVariables);
        }
    }

    @Getter
    @Setter
    static public class RestPage<T> extends PageImpl<T> {
        private int number;
        private int size;
        private int totalPages;
        private int numberOfElements;
        private long totalElements;
        private boolean previousPage;
        private boolean first;
        private boolean nextPage;
        private boolean last;
        private Sort sort;

        public RestPage() {
            super(new ArrayList<T>());
        }
        public RestPage(List<T> content) {
            super(content);
        }
        public RestPage(List<T> content, Pageable pageable, long totalElements) {
            super(content, pageable, totalElements);
        }

        public PageImpl<T> pageImpl() {
            return new PageImpl<T>(getContent(), new PageRequest(getNumber(),
                    getSize(), getSort()), getTotalElements());

        }
    }
}
