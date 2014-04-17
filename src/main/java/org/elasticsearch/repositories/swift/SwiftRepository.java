package org.elasticsearch.repositories.swift;

import org.elasticsearch.repositories.swift.blobstore.SwiftBlobStore;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.repositories.RepositoryException;
import org.elasticsearch.repositories.RepositoryName;
import org.elasticsearch.repositories.RepositorySettings;
import org.elasticsearch.repositories.blobstore.BlobStoreRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


public class SwiftRepository extends BlobStoreRepository {

    public final static String TYPE = "swift";

    private final SwiftBlobStore blobStore;

    private final BlobPath basePath;

    private ByteSizeValue chunkSize;

    private boolean compress;


    /**
     * Constructs new BlobStoreRepository
     * @param name                 repository name
     * @param repositorySettings   repository settings
     * @param indexShardRepository an instance of IndexShardRepository
     * @param swiftService an instance of SwiftService
     */
    @Inject
    protected SwiftRepository(RepositoryName name, RepositorySettings repositorySettings, IndexShardRepository indexShardRepository, SwiftService swiftService) {
        super(name.getName(), repositorySettings, indexShardRepository);

        String url = repositorySettings.settings().get("swift_url");
        if (url == null) {
            throw new RepositoryException(name.name(), "No url defined for swift repository");
        }

        String container = repositorySettings.settings().get("swift_container");
        if(container == null) {
        	throw new RepositoryException(name.name(), "No container defined for swift repository");
        }

        String username = repositorySettings.settings().get("swift_username", "");
        String password = repositorySettings.settings().get("swift_password", "");

        int concurrentStreams = repositorySettings.settings().getAsInt("concurrent_streams", componentSettings.getAsInt("concurrent_streams", 5));
        ExecutorService concurrentStreamPool = EsExecutors.newScaling(1, concurrentStreams, 5, TimeUnit.SECONDS, EsExecutors.daemonThreadFactory(settings, "[gridfs_stream]"));
        blobStore = new SwiftBlobStore(settings, swiftService.swift(url, username, password), container, concurrentStreamPool);
        this.chunkSize = repositorySettings.settings().getAsBytesSize("chunk_size", componentSettings.getAsBytesSize("chunk_size", null));
        this.compress = repositorySettings.settings().getAsBoolean("compress", componentSettings.getAsBoolean("compress", true));
        this.basePath = BlobPath.cleanPath();
    }

    @Override
    protected BlobStore blobStore() {
        return blobStore;
    }

    @Override
    protected BlobPath basePath() {
        return basePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ByteSizeValue chunkSize() {
        return chunkSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCompress() {
        return compress;
    }
}
