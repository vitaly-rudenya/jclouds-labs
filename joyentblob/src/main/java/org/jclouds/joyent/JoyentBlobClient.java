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

import com.google.inject.Provides;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.joyent.parsers.JoyentObject;

import java.util.Set;

/**
 * Provides access to Joyent Services via their REST API.
 *
 * @author vitaly.rudenya
 */
public interface JoyentBlobClient {
    @Provides
    public Blob newBlob();

    /**
     * Put Blob object into Joyent storage.
     */
    public String putBlob(String container, Blob object);

    /**
     * Retrieve Blob from Joyent storage
     */
    public Blob getBlob(String container, String name);

    /**
     * Retrieve Blob metadata from Joyent storage
     */
    public BlobMetadata getBlobMetadata(String container, String name);

    /**
     * Create new folder in the rood directory.
     */
    public boolean createContainer(String container);

    /**
     * List all objects from the root directory.
     */
    public Set<JoyentObject> listContainers();

    /**
     * List all objects from directory.
     */
    public Set<JoyentObject> listContainers(String container);

    /**
     * Delete existing folder.
     */
    public boolean deleteContainer(String container);

    /**
     * Delete existing Blob.
     */
    public boolean removeBlob(String container, String name);

    /**
     * Checks if container exists.
     */
    public boolean containerExists(String container);

    /**
     * Checks if Blob exists.
     */
    public boolean blobExists(String container, String name);
}
