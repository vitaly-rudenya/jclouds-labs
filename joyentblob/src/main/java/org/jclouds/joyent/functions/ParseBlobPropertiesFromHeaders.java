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
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author vitaly.rudenya
 */
public class ParseBlobPropertiesFromHeaders implements Function<HttpResponse, MutableBlobMetadata>,
        InvocationContext<ParseBlobPropertiesFromHeaders> {
   private final ParseSystemAndUserMetadataFromHeaders blobMetadataParser;

   @Inject
   public ParseBlobPropertiesFromHeaders(ParseSystemAndUserMetadataFromHeaders blobMetadataParser) {
      this.blobMetadataParser = blobMetadataParser;
   }

   public MutableBlobMetadata apply(HttpResponse from) {
      return blobMetadataParser.apply(from);
   }

   @Override
   public ParseBlobPropertiesFromHeaders setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
      blobMetadataParser.setContext(request);
      return this;
   }
}
