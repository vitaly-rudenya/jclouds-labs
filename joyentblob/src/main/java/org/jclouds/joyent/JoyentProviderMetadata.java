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

import com.google.common.reflect.TypeToken;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rest.ApiContext;

import java.net.URI;
import java.util.Properties;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for Joyent Service.
 *
 * @author vitaly.rudenya
 */
public class JoyentProviderMetadata extends BaseProviderMetadata {

    public static final TypeToken<ApiContext<JoyentBlobClient>> CONTEXT_TOKEN =
            new TypeToken<ApiContext<JoyentBlobClient>>() {
                private static final long serialVersionUID = 1L;
            };

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return builder().fromProviderMetadata(this);
    }

    public JoyentProviderMetadata() {
        super(builder());
    }

    public JoyentProviderMetadata(Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        return new Properties();
    }

    public static class Builder extends BaseProviderMetadata.Builder {

        protected Builder() {
            id("joyentblob")
                    .name("Joyent Service")
                    .apiMetadata(new JoyentApiMetadata())
                    .endpoint("https://us-east.manta.joyent.com")
                    .homepage(URI.create("http://joyent.com/"))
                    .console(URI.create("https://joyent.com/"))
                    .linkedServices("joyentblob")
                    .iso3166Codes("US-VA")
                    .defaultProperties(JoyentProviderMetadata.defaultProperties());
        }

        @Override
        public JoyentProviderMetadata build() {
            return new JoyentProviderMetadata(this);
        }

        @Override
        public Builder fromProviderMetadata(
                ProviderMetadata in) {
            super.fromProviderMetadata(in);
            return this;
        }
    }
}
