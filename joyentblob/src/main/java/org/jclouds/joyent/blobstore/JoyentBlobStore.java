/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.joyent.blobstore;

import com.google.common.base.Supplier;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.joyent.JoyentBlobClient;
import org.jclouds.joyent.functions.ListBlobsResponseToResourceList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author vitaly.rudenya
 */
@Singleton
public class JoyentBlobStore extends BaseBlobStore {
    private final JoyentBlobClient sync;

    private ListBlobsResponseToResourceList listBlobsResponseToResourceList;

    @Inject
    JoyentBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
                    @Memoized Supplier<Set<? extends Location>> locations, JoyentBlobClient sync,
                    ListBlobsResponseToResourceList listBlobsResponseToResourceList) {
        super(context, blobUtils, defaultLocation, locations);
        this.sync = checkNotNull(sync, "sync");
        this.listBlobsResponseToResourceList = listBlobsResponseToResourceList;
    }

    @Override
    public PageSet<? extends StorageMetadata> list() {
        return listBlobsResponseToResourceList.apply(sync.listContainers());
    }

    @Override
    public boolean containerExists(String container) {
        return sync.containerExists(container);
    }

    @Override
    public boolean createContainerInLocation(Location location, String container) {
        //We have only one location
        return sync.createContainer(container);
    }

    @Override
    public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
        return listBlobsResponseToResourceList.apply(sync.listContainers(container));
    }

    @Override
    public boolean blobExists(String container, String key) {
        return sync.blobExists(container, key);
    }

    /**
     * This implementation invokes {@link JoyentBlobClient#getBlob}
     *
     * @param container container name
     * @param key       blob key
     */
    @Override
    public Blob getBlob(String container, String key, GetOptions options) {
        return sync.getBlob(container, key);
    }

    /**
     * @param container container name
     * @param blob      object
     */
    @Override
    public String putBlob(String container, Blob blob) {
        return sync.putBlob(container, blob);
    }

    /**
     * @param container container name
     * @param blob      object
     */
    @Override
    public String putBlob(String container, Blob blob, PutOptions options) {
        return putBlob(container, blob);
    }

    @Override
    public void removeBlob(String container, String key) {
        sync.removeBlob(container, key);
    }

    @Override
    public BlobMetadata blobMetadata(String container, String key) {
        return sync.getBlobMetadata(container, key);
    }

    @Override
    protected boolean deleteAndVerifyContainerGone(String container) {
        sync.deleteContainer(container);
        return !containerExists(container);
    }

    @Override
    public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
        //We have only one location
        return sync.createContainer(container);
    }
}
