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
import com.google.inject.TypeLiteral;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.http.HttpResponse;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.openstack.swift.domain.ObjectInfo;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

/**
 * Date: 15.10.13
 * Time: 11:10
 *
 * @author vitaly.rudenya
 */
public class ParseObjectInfoListFromJoyentResponse implements Function<HttpResponse, PageSet<ObjectInfo>> {

    private static final Log LOG = LogFactory.getLog(ParseObjectInfoListFromJoyentResponse.class);

    @Inject
    private Json json;

    private static final TypeLiteral<PageSet<JoyentObject>> type = new TypeLiteral<PageSet<JoyentObject>>() {
    };

    @Nullable
    @Override
    public PageSet<ObjectInfo> apply(@Nullable HttpResponse input) {

        if (input != null) {
            InputStream stream = input.getPayload().getInput();
            try {
                try {
                    StringWriter writer = new StringWriter();
                    try {
                        IOUtils.copy(stream, writer);
                    } finally {
                        stream.close();
                    }

                    String result = "[" + writer.toString().replaceAll("\\n\\{", ",{") + "]";
                    Set<JoyentObject> objects = json.fromJson(result, type.getType());
                    return new PageSetImpl<ObjectInfo>(objects, null);

                } finally {
                    if (stream != null)
                        stream.close();
                }
            } catch (IOException ex) {
                LOG.error(ex);
            }
        }
        return null;
    }
}
