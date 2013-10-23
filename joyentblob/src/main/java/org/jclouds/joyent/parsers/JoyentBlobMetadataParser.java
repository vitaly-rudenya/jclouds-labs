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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.joyent.JoyentConstants;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Date: 18.10.13
 * Time: 5:15
 *
 * @author vitaly.rudenya
 */
abstract class JoyentBlobMetadataParser<RETURN> implements Function<HttpResponse, RETURN>,
        InvocationContext<JoyentBlobMetadataParser<RETURN>> {

    private static final Log LOGGER = LogFactory.getLog(ParseBlobFromJoyentResponse.class);

    private String identity;

    private String blobName;
    private String container;
    private URI uri;

    @Inject
    public JoyentBlobMetadataParser(@org.jclouds.location.Provider
                                    Supplier<Credentials> creds) {
        identity = creds.get().identity;
    }

    protected MutableBlobMetadata populateMetadata(MutableBlobMetadata blobMetadata, HttpResponse input) {
        if (input != null) {
            blobMetadata.setName(blobName);
            blobMetadata.setContainer(container);
            blobMetadata.setContentMetadata(input.getPayload().getContentMetadata());

            Collection<String> etagList = input.getHeaders().get("Etag");
            blobMetadata.setETag(etagList.size() > 0 ? etagList.iterator().next() : null);

            Collection<String> lastModifiedList = input.getHeaders().get(HttpHeaders.LAST_MODIFIED);
            blobMetadata.setLastModified(lastModifiedList.size() > 0 ?
                    new Date(lastModifiedList.iterator().next()) : null);

            blobMetadata.setType(StorageType.BLOB);
            blobMetadata.setUri(uri);

        }
        return blobMetadata;
    }

    @Override
    public JoyentBlobMetadataParser<RETURN> setContext(HttpRequest request) {
        if (request instanceof GeneratedHttpRequest) {

            List args = ((GeneratedHttpRequest) request).getInvocation().getArgs();

            container = (String) args.get(0);
            blobName = (String) args.get(1);
        }

        String reqURL = request.getEndpoint().toString();
        String path = request.getEndpoint().getPath();
        String storPath = "/" + identity + "/" + JoyentConstants.STOR_PATH;
        if (!path.startsWith(storPath)) {
            int pathInd = reqURL.lastIndexOf(path);
            String newUrl = reqURL.substring(0, pathInd) + storPath + reqURL.substring(pathInd);
            try {
                uri = new URI(newUrl);
            } catch (URISyntaxException e) {
                LOGGER.warn("Can't parse URL " + newUrl, e);
            }
        }

        return this;
    }
}
