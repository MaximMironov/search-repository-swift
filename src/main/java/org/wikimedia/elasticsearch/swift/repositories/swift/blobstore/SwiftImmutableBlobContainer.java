package org.wikimedia.elasticsearch.swift.repositories.swift.blobstore;

import java.io.IOException;
import java.io.InputStream;

import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.ImmutableBlobContainer;
import org.elasticsearch.common.blobstore.support.BlobStores;
import org.javaswift.joss.model.StoredObject;

/**
 * The final implementation of Swift blob storage. Implements the write functionality
 */
public class SwiftImmutableBlobContainer extends AbstractSwiftBlobContainer implements ImmutableBlobContainer {
	/**
	 * Constructor. Just call the parent.
     * @param path The BlobPath to find blobs in
     * @param blobStore The blob store to use for operations
	 */
    protected SwiftImmutableBlobContainer(BlobPath path, SwiftBlobStore blobStore) {
        super(path, blobStore);
    }

    /**
     * Write a blob!
     * @param blobName The name of the blob we're writing
     * @param is The data we're writing
     * @param sizeInBytes How big the data is. We don't care with Swift.
     * @param listener The listener for write results (failure, etc)
     */
    @Override
    public void writeBlob(final String blobName, final InputStream is, final long sizeInBytes, final WriterListener listener) {
        blobStore.executor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                	// need to remove old file if already exist
                	StoredObject object = blobStore.swift().getObject(buildKey(blobName));
                	if (object.exists()) {
                		object.delete();
                	}
                	object.uploadObject(is);
                    listener.onCompleted();
                } catch (Exception e) {
                    listener.onFailure(e);
                }
            }
        });
    }

    /**
     * Write a blob! Basically boilerplate from other implementations. 
     * @param blobName The name of the blob we're writing
     * @param is The data we're writing
     * @param sizeInBytes How big the data is. We don't care with Swift.
     */
    @Override
    public void writeBlob(String blobName, InputStream is, long sizeInBytes) throws IOException {
        BlobStores.syncWriteBlob(this, blobName, is, sizeInBytes);
    }
}
