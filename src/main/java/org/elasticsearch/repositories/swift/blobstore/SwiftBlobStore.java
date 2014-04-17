package org.elasticsearch.repositories.swift.blobstore;

import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.blobstore.ImmutableBlobContainer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;

import java.util.concurrent.Executor;


public class SwiftBlobStore extends AbstractComponent implements BlobStore {

    private final Executor executor;

    private final int bufferSizeInBytes;

    private final Container swift;

    public SwiftBlobStore(Settings settings, Account auth, String container, Executor executor) {
        super(settings);
        this.executor = executor;

        swift = auth.getContainer(container);

        this.bufferSizeInBytes = (int) settings.getAsBytesSize("buffer_size", new ByteSizeValue(100, ByteSizeUnit.KB)).bytes();
    }

    public Container swift() {
        return swift;
    }

    public Executor executor() {
        return executor;
    }

    public int bufferSizeInBytes() {
        return bufferSizeInBytes;
    }

    @Override
    public ImmutableBlobContainer immutableBlobContainer(BlobPath path) {
        return new SwiftImmutableBlobContainer(path, this);
    }

    @Override
    public void delete(BlobPath path) {
    	String keyPath = path.buildAsString("/");
        if (!keyPath.isEmpty()) {
            keyPath = keyPath + "/";
        }
        swift().getObject(keyPath).delete();
    }

    @Override
    public void close() {
    }
}
