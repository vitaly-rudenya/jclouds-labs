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
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.joyent.JoyentBlobAsyncClient;
import org.jclouds.joyent.functions.ListBlobsResponseToResourceList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Date: 30.09.13
 * Time: 10:03
 *
 * @author vitaly.rudenya
 */
@Singleton
public class JoyentAsyncBlobStore extends BaseAsyncBlobStore {

    private static final Log LOGGER = LogFactory.getLog(JoyentAsyncBlobStore.class);

    private final JoyentBlobAsyncClient async;
    private final ListeningExecutorService userExecutor;
    private ListBlobsResponseToResourceList converter;

    @Inject
    JoyentAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
                         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                         ListBlobsResponseToResourceList converter,
                         Supplier<Location> defaultLocation, @Memoized Supplier<Set<? extends Location>> locations,
                         JoyentBlobAsyncClient async) {
        super(context, blobUtils, userExecutor, defaultLocation, locations);
        this.async = checkNotNull(async, "async");
        this.userExecutor = checkNotNull(userExecutor, "userExecutor");
        this.converter = checkNotNull(converter, "converter");
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
        return Futures.transform(async.listContainers(), converter, userExecutor);
    }

    @Override
    public ListenableFuture<Boolean> containerExists(String container) {
        return async.containerExists(container);
    }

    @Override
    public ListenableFuture<Boolean> createContainerInLocation(Location location, String container) {
        return async.createContainer(container);
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container, ListContainerOptions options) {
        return Futures.transform(async.listContainers(container), converter, userExecutor);
    }

    @Override
    public ListenableFuture<Blob> getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions options) {
        return async.getBlob(container, key);
    }

    @Override
    public ListenableFuture<String> putBlob(String container, Blob blob) {
        return async.putBlob(container, blob);
    }

    @Override
    public ListenableFuture<Void> removeBlob(String container, String key) {
        async.removeBlob(container, key);
        return null;
    }

    @Override
    public ListenableFuture<Boolean> blobExists(String container, String name) {
        return async.blobExists(container, name);
    }

    @Override
    public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
        return async.getBlobMetadata(container, key);
    }

    @Override
    protected boolean deleteAndVerifyContainerGone(String container) {
        async.deleteContainer(container);
        try {
            return !containerExists(container).get();
        } catch (InterruptedException e) {
            LOGGER.warn("deleteAndVerifyContainerGone operation execution fail", e);
            return true;
        } catch (ExecutionException e) {
            LOGGER.warn("deleteAndVerifyContainerGone operation execution fail", e);
            return true;
        }
    }

    @Override
    public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options) {
        if (options.isMultipart()) {
            throw new UnsupportedOperationException("Multipart upload not supported in JoyentAsyncBlobStore");
        }
        return putBlob(container, blob);
    }

    @Override
    public ListenableFuture<Boolean> createContainerInLocation(Location location, String container,
                                                               CreateContainerOptions options) {
        return async.createContainer(container);
    }
}
