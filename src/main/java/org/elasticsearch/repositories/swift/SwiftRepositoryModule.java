package org.elasticsearch.repositories.swift;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.index.snapshots.blobstore.BlobStoreIndexShardRepository;
import org.elasticsearch.repositories.Repository;

/**
 * Swift repository module
 */
public class SwiftRepositoryModule extends AbstractModule {

    public SwiftRepositoryModule() {
        super();
    }

    @Override
    protected void configure() {
        bind(Repository.class).to(SwiftRepository.class).asEagerSingleton();
        bind(IndexShardRepository.class).to(BlobStoreIndexShardRepository.class).asEagerSingleton();
    }
}
