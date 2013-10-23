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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.joyent.blobstore.config.JoyentBlobStoreContextModule;
import org.jclouds.joyent.config.JoyentBlobRestClientModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.reflect.Reflection2.typeToken;

/**
 * @author vitaly.rudenya
 */
public class JoyentApiMetadata extends BaseHttpApiMetadata<JoyentBlobClient> {

    @Override
    public Builder<?> toBuilder() {
        return new ConcreteBuilder().fromApiMetadata(this);
    }

    public JoyentApiMetadata() {
        this(new ConcreteBuilder());
    }

    protected JoyentApiMetadata(Builder<?> builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        return BaseHttpApiMetadata.defaultProperties();
    }

    public abstract static class Builder<T extends Builder<T>> extends BaseHttpApiMetadata.Builder<JoyentBlobClient, T> {
        @SuppressWarnings("deprecation")
        protected Builder() {
            super();
            id("joyentblob")
                    .name("Joyent Blob Service API")
                    .defaultIdentity("foo")
                    .defaultCredential("bar")
                    .identityName("Account Name")
                    .credentialName("Access Key")
                    .version("1.0")
                    .defaultEndpoint("https://us-east.manta.joyent.com")
                    .documentation(URI.create("http://joyent.com/"))
                    .defaultProperties(JoyentApiMetadata.defaultProperties())
                    .view(typeToken(BlobStoreContext.class))
                    .defaultModules(ImmutableSet.<Class<? extends Module>>of(JoyentBlobRestClientModule.class, JoyentBlobStoreContextModule.class));

        }

        @Override
        public JoyentApiMetadata build() {
            return new JoyentApiMetadata(this);
        }
    }

    public static class ConcreteBuilder extends Builder<ConcreteBuilder> {
        @Override
        protected ConcreteBuilder self() {
            return this;
        }
    }
}
