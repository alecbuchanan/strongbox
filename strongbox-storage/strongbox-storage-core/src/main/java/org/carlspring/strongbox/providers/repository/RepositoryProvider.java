package org.carlspring.strongbox.providers.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.carlspring.strongbox.data.criteria.Paginator;
import org.carlspring.strongbox.data.criteria.Predicate;
import org.carlspring.strongbox.io.RepositoryInputStream;
import org.carlspring.strongbox.io.RepositoryOutputStream;
import org.carlspring.strongbox.storage.repository.MutableRepository;

/**
 * This interface provide functionality to operate with artifact Paths.
 * Implementation depends of {@link MutableRepository} type which can be: Hosted, Group
 * or Proxy.
 * 
 * @author carlspring
 */
public interface RepositoryProvider
{

    /**
     * Return {@link MutableRepository} type alias.
     * 
     * @return
     */
    String getAlias();

    /**
     * Return {@link InputStream} to read Artifact content.
     * 
     * @param path
     * @return
     * @throws IOException
     */
    RepositoryInputStream getInputStream(Path path) throws IOException;
    
    /**
     * Return {@link OutputStream} to write Artifact content.
     * 
     * @param path
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    RepositoryOutputStream getOutputStream(Path path)
            throws IOException, NoSuchAlgorithmException;
    
    /**
     * Searches Artifact Paths. For Group Repositories result will be group
     * member Paths.
     * 
     * @param searchRequest
     * @param pageRequest
     * @return
     */
    List<Path> search(RepositorySearchRequest searchRequest, RepositoryPageRequest pageRequest);
    
    /**
     * Counts Artifacts. For Group repositories result will be distinct within
     * group members.
     * 
     * @param searchRequest
     * @return
     */
    Long count(RepositorySearchRequest searchRequest);
    
    /**
     * Searches Artifact Paths. For Group Repositories result will be group
     * member Paths.
     * 
     * @param storageId
     * @param repositoryId
     * @param predicate
     * @param paginator
     * @return
     */
    List<Path> search(String storageId,
                      String repositoryId,
                      Predicate predicate,
                      Paginator paginator);
    
    /**
     * Counts Artifacts. For Group repositories result will be distinct within
     * group members.
     * 
     * @param storageId
     * @param repositoryId
     * @param predicate
     * @return
     */
    Long count(String storageId,
               String repositoryId,
               Predicate predicate);
    
    /**
     * Fetch Artifact Path from target repository.
     * For Group repository it will resolve Path from underlying group member.
     * For Proxy repository it will try to download remote Artifact if it's not cached.
     * Return  <code>null<code> if there is no such Path in target repository.
     * 
     * To resolve target path you should use {@link RepositoryPathResolver}
     * 
     * @param repositoryPath
     * @return
     * @throws IOException
     */
    Path fetchPath(Path repositoryPath)
        throws IOException;

}
