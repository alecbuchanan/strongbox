package org.carlspring.strongbox.configuration;

import org.carlspring.strongbox.storage.Storage;
import org.carlspring.strongbox.storage.MutableStorage;
import org.carlspring.strongbox.storage.repository.HttpConnectionPool;
import org.carlspring.strongbox.storage.repository.Repository;
import org.carlspring.strongbox.storage.repository.RepositoryTypeEnum;
import org.carlspring.strongbox.storage.routing.RoutingRules;
import org.carlspring.strongbox.storage.routing.MutableRoutingRules;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import static java.util.stream.Collectors.toMap;

/**
 * @author Przemyslaw Fusik
 */
@Immutable
public class Configuration
{

    private final String id;

    private final String instanceName;

    private final String version;

    private final String revision;

    private final String baseUrl;

    private final int port;

    private final ProxyConfiguration proxyConfiguration;

    private final SessionConfiguration sessionConfiguration;

    private final RemoteRepositoriesConfiguration remoteRepositoriesConfiguration;

    private final Map<String, Storage> storages;

    private final RoutingRules routingRules;

    public Configuration(final MutableConfiguration delegate)
    {

        id = delegate.getId();
        instanceName = delegate.getInstanceName();
        version = delegate.getVersion();
        revision = delegate.getRevision();
        baseUrl = delegate.getBaseUrl();
        port = delegate.getPort();
        proxyConfiguration = immuteProxyConfiguration(delegate.getProxyConfiguration());
        sessionConfiguration = immuteSessionConfiguration(delegate.getSessionConfiguration());
        remoteRepositoriesConfiguration = immuteRemoteRepositoriesConfiguration(
                delegate.getRemoteRepositoriesConfiguration());
        storages = immuteStorages(delegate.getStorages());
        routingRules = immuteRoutingRules(delegate.getRoutingRules());
    }


    private ProxyConfiguration immuteProxyConfiguration(final MutableProxyConfiguration source)
    {
        return source != null ? new ProxyConfiguration(source) : null;
    }

    private SessionConfiguration immuteSessionConfiguration(final MutableSessionConfiguration source)
    {
        return source != null ? new SessionConfiguration(source) : null;
    }

    private Map<String, Storage> immuteStorages(final Map<String, MutableStorage> source)
    {
        return source != null ? ImmutableMap.copyOf(source.entrySet().stream().collect(
                toMap(Map.Entry::getKey, e -> new Storage(e.getValue())))) : Collections.emptyMap();
    }

    private RemoteRepositoriesConfiguration immuteRemoteRepositoriesConfiguration(final MutableRemoteRepositoriesConfiguration source)
    {
        return source != null ? new RemoteRepositoriesConfiguration(source) : null;
    }

    private RoutingRules immuteRoutingRules(final MutableRoutingRules source)
    {
        return source != null ? new RoutingRules(source) : null;
    }

    public String getId()
    {
        return id;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public String getVersion()
    {
        return version;
    }

    public String getRevision()
    {
        return revision;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public int getPort()
    {
        return port;
    }

    public ProxyConfiguration getProxyConfiguration()
    {
        return proxyConfiguration;
    }

    public SessionConfiguration getSessionConfiguration()
    {
        return sessionConfiguration;
    }

    public RemoteRepositoriesConfiguration getRemoteRepositoriesConfiguration()
    {
        return remoteRepositoriesConfiguration;
    }

    public Map<String, Storage> getStorages()
    {
        return storages;
    }

    public Storage getStorage(final String storageId)
    {
        return storages.get(storageId);
    }

    public RoutingRules getRoutingRules()
    {
        return routingRules;
    }

    public List<Repository> getRepositoriesWithLayout(String storageId,
                                                      String layout)
    {
        Stream<Repository> repositories;
        if (storageId != null)
        {
            Storage storage = getStorage(storageId);
            if (storage != null)
            {
                repositories = storage.getRepositories().values().stream();
            }
            else
            {
                return Collections.emptyList();
            }
        }
        else
        {
            repositories = getStorages().values().stream().flatMap(
                    storage -> storage.getRepositories().values().stream());
        }

        return repositories.filter(repository -> repository.getLayout().equals(layout))
                           .collect(Collectors.toList());
    }

    public List<Repository> getGroupRepositories()
    {
        List<Repository> groupRepositories = new ArrayList<>();

        for (Storage storage : getStorages().values())
        {
            groupRepositories.addAll(storage.getRepositories()
                                            .values()
                                            .stream()
                                            .filter(repository -> repository.getType()
                                                                            .equals(RepositoryTypeEnum.GROUP.getType()))
                                            .collect(Collectors.toList()));
        }

        return groupRepositories;
    }

    public Repository getRepository(String storageId,
                                    String repositoryId)
    {
        return getStorage(storageId).getRepository(repositoryId);
    }

    public List<Repository> getGroupRepositoriesContaining(String storageId,
                                                           String repositoryId)
    {
        List<Repository> groupRepositories = new ArrayList<>();

        Storage storage = getStorage(storageId);

        groupRepositories.addAll(storage.getRepositories()
                                        .values()
                                        .stream()
                                        .filter(repository -> repository.getType()
                                                                        .equals(RepositoryTypeEnum.GROUP.getType()))
                                        .filter(repository -> repository.getGroupRepositories()
                                                                        .keySet()
                                                                        .contains(repositoryId))
                                        .collect(Collectors.toList()));

        return groupRepositories;
    }

    public HttpConnectionPool getHttpConnectionPoolConfiguration(String storageId,
                                                                 String repositoryId)
    {
        return getStorage(storageId).getRepository(repositoryId).getHttpConnectionPool();
    }

}
