package org.wikimedia.elasticsearch.repositories.swift.blobstore;

import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.blobstore.ImmutableBlobContainer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;

import java.util.concurrent.Executor;

/**
 * Our blob store
 */
public class SwiftBlobStore extends AbstractComponent implements BlobStore {
	// Executor for our operations. Sorta like a dedicated Thread pool.
    private final Executor executor;

    // How much to buffer our blobs by
    private final int bufferSizeInBytes;

    // Our Swift container. This is important.
    private final Container swift;

    /**
     * Constructor. Sets up the container mostly.
     * @param settings Settings for our repository. Only care about buffer size.
     * @param auth
     * @param container
     * @param executor
     */
    public SwiftBlobStore(Settings settings, Account auth, String container, Executor executor) {
        super(settings);
        this.executor = executor;
        this.bufferSizeInBytes = (int)settings.getAsBytesSize("buffer_size", new ByteSizeValue(100, ByteSizeUnit.KB)).bytes();

        swift = auth.getContainer(container);
        if (!swift.exists()) {
            swift.create();
            swift.makePublic();
        }
    }

    /**
     * Get the container
     */
    public Container swift() {
        return swift;
    }

    /**
     * Get the executor
     */
    public Executor executor() {
        return executor;
    }

    /**
     * Get our buffer size
     */
    public int bufferSizeInBytes() {
        return bufferSizeInBytes;
    }

    /**
     * Factory for getting blob containers for a path
     * @param path The blob path to search
     */
    @Override
    public ImmutableBlobContainer immutableBlobContainer(BlobPath path) {
        return new SwiftImmutableBlobContainer(path, this);
    }

    /**
     * Delete an arbitrary BlobPath from our store.
     * @param path The blob path to delete
     */
    @Override
    public void delete(BlobPath path) {
    	String keyPath = path.buildAsString("/");
        if (!keyPath.isEmpty()) {
            keyPath = keyPath + "/";
        }
        StoredObject obj = swift().getObject(keyPath);
        if (obj.exists()) {
            obj.delete();
        }
    }

    /**
     * Close the store. No-op for us.
     */
    @Override
    public void close() {
    }
}
