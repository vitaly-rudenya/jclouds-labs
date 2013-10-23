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
package org.jclouds.joyent.blobstore.config;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.joyent.blobstore.JoyentAsyncBlobStore;
import org.jclouds.joyent.blobstore.JoyentBlobStore;

/**
 * Date: 30.09.13
 * Time: 11:04
 *
 * @author vitaly.rudenya
 */
public class JoyentBlobStoreContextModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
        bind(AsyncBlobStore.class).to(JoyentAsyncBlobStore.class).in(Scopes.SINGLETON);
        bind(BlobStore.class).to(JoyentBlobStore.class).in(Scopes.SINGLETON);
    }
}
