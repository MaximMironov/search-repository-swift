package org.elasticsearch.repositories.swift.blobstore;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.blobstore.BlobMetaData;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.support.AbstractBlobContainer;
import org.elasticsearch.common.blobstore.support.PlainBlobMetaData;
import org.elasticsearch.common.collect.ImmutableMap;
import org.javaswift.joss.model.StoredObject;

import java.io.IOException;
import java.io.InputStream;


public class AbstractSwiftBlobContainer extends AbstractBlobContainer {

    protected final SwiftBlobStore blobStore;

    protected final String keyPath;


    protected AbstractSwiftBlobContainer(BlobPath path, SwiftBlobStore blobStore) {
        super(path);
        this.blobStore = blobStore;
        String keyPath = path.buildAsString("/");
        if (!keyPath.isEmpty()) {
            keyPath = keyPath + "/";
        }
        this.keyPath = keyPath;
    }

    @Override
    public boolean blobExists(String blobName) {
    	return blobStore.swift().getObject(buildKey(blobName)).exists();
    }

    @Override
    public void readBlob(final String blobName, final ReadBlobListener listener) {
        blobStore.executor().execute(new Runnable() {
            @Override
            public void run() {
                InputStream is;
                try {
                    StoredObject object = blobStore.swift().getObject(buildKey(blobName));
                    is = object.downloadObjectAsInputStream();
                } catch (Exception e) {
                    listener.onFailure(e);
                    return;
                }
                byte[] buffer = new byte[blobStore.bufferSizeInBytes()];
                try {
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        listener.onPartial(buffer, 0, bytesRead);
                    }
                    listener.onCompleted();
                } catch (Exception e) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        // ignore
                    }
                    listener.onFailure(e);
                }
            }
        });
    }

    @Override
    public boolean deleteBlob(String blobName) throws IOException {
    	StoredObject object = blobStore.swift().getObject(buildKey(blobName));
    	if (object.exists()) {
    		object.delete();
    	}
        return true;
    }

    @Override
    public ImmutableMap<String, BlobMetaData> listBlobsByPrefix(@Nullable String blobNamePrefix) throws IOException {
        ImmutableMap.Builder<String, BlobMetaData> blobsBuilder = ImmutableMap.builder();

        
        for (StoredObject object : blobStore.swift().list()) {
        	String name = object.getName().substring(keyPath.length());
        	blobsBuilder.put(name, new PlainBlobMetaData(name, object.getContentLength()));
        }

        return blobsBuilder.build();
    }

    @Override
    public ImmutableMap<String, BlobMetaData> listBlobs() throws IOException {
        return listBlobsByPrefix(null);
    }

    protected String buildKey(String blobName) {
        return keyPath + blobName;
    }
}
