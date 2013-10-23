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
package org.jclouds.joyent.functions;

import com.google.common.base.Function;
import com.google.inject.Singleton;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.joyent.parsers.JoyentObject;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 15.10.13
 * Time: 10:35
 *
 * @author vitaly.rudenya
 */
@Singleton
public class ListBlobsResponseToResourceList implements
        Function<Set<JoyentObject>, PageSet<? extends StorageMetadata>> {

    public PageSet<? extends StorageMetadata> apply(Set<JoyentObject> form) {
        Set<StorageMetadata> storageMetadatas = new HashSet<StorageMetadata>();

        for (JoyentObject joyentObject : form) {
            StorageType storageType = null;
            if (JoyentObject.TYPE_FOLDER.equals(joyentObject.getContentType())) {
                storageType = StorageType.FOLDER;
            } else if (JoyentObject.TYPE_BLOB.equals(joyentObject.getContentType())) {
                storageType = StorageType.BLOB;
            }

            Date creationDate = joyentObject.getLastModified();
            @SuppressWarnings("unchecked") StorageMetadata storageMetadata = new StorageMetadataImpl(storageType, null,
                    joyentObject.getName(), null, null, joyentObject.getEtag(), creationDate, creationDate,
                    Collections.EMPTY_MAP);
            storageMetadatas.add(storageMetadata);
        }

        return new PageSetImpl<StorageMetadata>(storageMetadatas, null);
    }
}
