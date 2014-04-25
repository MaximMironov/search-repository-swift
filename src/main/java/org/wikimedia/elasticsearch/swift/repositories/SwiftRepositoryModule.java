package org.wikimedia.elasticsearch.swift.repositories;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.index.snapshots.blobstore.BlobStoreIndexShardRepository;
import org.elasticsearch.repositories.Repository;

/**
 * Swift repository module. Binds us to things.
 */
public class SwiftRepositoryModule extends AbstractModule {
	/**
	 * Constructor. Super boring.
	 */
    public SwiftRepositoryModule() {
        super();
    }

    /**
     * Do the binding.
     */
    @Override
    protected void configure() {
        bind(Repository.class).to(SwiftRepository.class).asEagerSingleton();
        bind(IndexShardRepository.class).to(BlobStoreIndexShardRepository.class).asEagerSingleton();
        bind(SwiftService.class).asEagerSingleton();
    }
}
