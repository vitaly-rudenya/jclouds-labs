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
package org.jclouds.joyent.parsers;

import com.google.common.base.Supplier;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.joyent.JoyentBlobClient;

import javax.inject.Inject;

/**
 * Date: 17.10.13
 * Time: 9:03
 *
 * @author vitaly.rudenya
 */
public class ParseBlobFromJoyentResponse extends JoyentBlobMetadataParser<Blob> {

    @Inject
    private JoyentBlobClient sync;

    @Inject
    public ParseBlobFromJoyentResponse(@org.jclouds.location.Provider
                                       Supplier<Credentials> creds) {
        super(creds);
    }

    @Nullable
    @Override
    public Blob apply(@Nullable HttpResponse input) {

        if (input != null) {
            Payload payload = input.getPayload();

            Blob blob = sync.newBlob();
            blob.setPayload(payload);
            blob.setAllHeaders(input.getHeaders());

            populateMetadata(blob.getMetadata(), input);

            return blob;
        }
        return null;
    }
}
