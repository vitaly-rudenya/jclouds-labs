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
package org.jclouds.joyent;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;
import org.jclouds.Fallbacks;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.functions.BlobName;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.joyent.blobstore.JoyentBlobRequestSigner;
import org.jclouds.joyent.parsers.JoyentObject;
import org.jclouds.joyent.parsers.ParseBlobFromJoyentResponse;
import org.jclouds.joyent.parsers.ParseBlobMetadataFromJoyentResponse;
import org.jclouds.joyent.parsers.ParseObjectInfoListFromJoyentResponse;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Date: 30.09.13
 * Time: 10:38
 *
 * @author vitaly.rudenya
 */
@Path("/")
@RequestFilters(JoyentBlobRequestSigner.class)
public interface JoyentBlobAsyncClient {

    @Provides
    public Blob newBlob();

    /**
     * Put Blob object into Joyent storage.
     */
    @Named("PutBlob")
    @PUT
    @Path("{container}/{name}")
    @ResponseParser(ParseETagHeader.class)
    ListenableFuture<String> putBlob(
            @PathParam("container") String container,
            @PathParam("name") @ParamParser(BlobName.class) Blob object);

    /**
     * Retrieve Blob from Joyent storage
     */    @Named("GetBlob")
    @GET
    @ResponseParser(ParseBlobFromJoyentResponse.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Path("/{container}/{name}")
    ListenableFuture<Blob> getBlob(@PathParam("container") String container,
                                   @PathParam("name") String name);

    /**
     * Retrieve Blob metadata from Joyent storage
     */
    @Named("GetBlobMetadata")
    @GET
    @ResponseParser(ParseBlobMetadataFromJoyentResponse.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Path("/{container}/{name}")
    public ListenableFuture<BlobMetadata> getBlobMetadata(@PathParam("container") String container,
                                                          @PathParam("name") String name);

    /**
     * Create new folder in the rood directory.
     */
    @Named("CreateContainer")
    @PUT
    @Path("{container}")
    @Headers(keys = {"Content-Type"}, values = {"application/json; type=directory"})
    ListenableFuture<Boolean> createContainer(@PathParam("container") String container);

    /**
     * List all objects from the root directory.
     */
    @Named("ListContainers")
    @GET
    @ResponseParser(ParseObjectInfoListFromJoyentResponse.class)
    @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
    ListenableFuture<PageSet<JoyentObject>> listContainers();

    /**
     * List all objects from directory.
     */
    @Named("ListContainers")
    @GET
    @ResponseParser(ParseObjectInfoListFromJoyentResponse.class)
    @Path("{container}")
    @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
    ListenableFuture<PageSet<JoyentObject>> listContainers(@PathParam("container") String container);

    /**
     * Delete existing folder.
     */
    @Named("DeleteContainer")
    @DELETE
    @Path("{container}")
    @Fallback(Fallbacks.TrueOnNotFoundOr404.class)
    ListenableFuture<Boolean> deleteContainer(@PathParam("container") String container);

    /**
     * Delete existing Blob.
     */
    @Named("DeleteBlob")
    @DELETE
    @Path("{container}/{name}")
    @Fallback(Fallbacks.TrueOnNotFoundOr404.class)
    ListenableFuture<Boolean> removeBlob(@PathParam("container") String container,
                                         @PathParam("name") String name);

    /**
     * Checks if container exists.
     */
    @Named("GetContainerProperties")
    @HEAD
    @Path("{container}")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    ListenableFuture<Boolean> containerExists(@PathParam("container") String container);

    /**
     * Checks if Blob exists.
     */
    @Named("GetBlobProperties")
    @HEAD
    @Path("{container}/{name}")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    ListenableFuture<Boolean> blobExists(@PathParam("container") String container,
                                         @PathParam("name") String name);
}
