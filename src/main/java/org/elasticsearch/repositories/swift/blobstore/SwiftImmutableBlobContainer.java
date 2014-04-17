package org.elasticsearch.repositories.swift.blobstore;

import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.ImmutableBlobContainer;
import org.elasticsearch.common.blobstore.support.BlobStores;
import org.javaswift.joss.model.StoredObject;

import java.io.IOException;
import java.io.InputStream;


public class SwiftImmutableBlobContainer extends AbstractSwiftBlobContainer implements ImmutableBlobContainer {

    protected SwiftImmutableBlobContainer(BlobPath path, SwiftBlobStore blobStore) {
        super(path, blobStore);
    }

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

    @Override
    public void writeBlob(String blobName, InputStream is, long sizeInBytes) throws IOException {
        BlobStores.syncWriteBlob(this, blobName, is, sizeInBytes);
    }
}
