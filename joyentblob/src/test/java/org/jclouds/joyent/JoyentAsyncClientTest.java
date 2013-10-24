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

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import org.jclouds.Constants;
import org.jclouds.Fallbacks;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.joyent.blobstore.JoyentBlobRequestSigner;
import org.jclouds.joyent.parsers.ParseBlobFromJoyentResponse;
import org.jclouds.joyent.parsers.ParseObjectInfoListFromJoyentResponse;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

/**
 * @author Vitaly Rudenya
 */
@Test(testName = "JoyentAsyncClientTest")
public class JoyentAsyncClientTest extends BaseAsyncClientTest<JoyentBlobAsyncClient> {

   public void testListContainers() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(JoyentBlobAsyncClient.class, "listContainers");
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request, "GET https://us-east.manta.joyent.com/ HTTP/1.1");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectInfoListFromJoyentResponse.class);
      assertFallbackClassEquals(method, Fallbacks.EmptyListOnNotFoundOr404.class);

   }

   public void testListContainersByPath() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(JoyentBlobAsyncClient.class, "listContainers", String.class);
      List<Object> args = new ArrayList<Object>();
      args.add("test");
      GeneratedHttpRequest request = processor.createRequest(method, args);

      assertRequestLineEquals(request, "GET https://us-east.manta.joyent.com/test HTTP/1.1");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectInfoListFromJoyentResponse.class);
      assertFallbackClassEquals(method, Fallbacks.EmptyListOnNotFoundOr404.class);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(JoyentBlobAsyncClient.class, "createContainer", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object>of("container"));

      assertRequestLineEquals(request, "PUT https://us-east.manta.joyent.com/container HTTP/1.1");

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(JoyentBlobAsyncClient.class, "deleteContainer", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object>of("container"));

      assertRequestLineEquals(request, "DELETE https://us-east.manta.joyent.com/container HTTP/1.1");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, Fallbacks.TrueOnNotFoundOr404.class);
   }

   public void testGetBlob() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(JoyentBlobAsyncClient.class, "getBlob", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("container", "blob"));

      assertRequestLineEquals(request, "GET https://us-east.manta.joyent.com/container/blob HTTP/1.1");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseBlobFromJoyentResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, Fallbacks.NullOnNotFoundOr404.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), JoyentBlobRequestSigner.class);
   }

   @Override
   public JoyentProviderMetadata createProviderMetadata() {
      return new JoyentProviderMetadata();
   }

   protected java.util.Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(Constants.PROPERTY_IDENTITY, TestConstants.USER_NAME);
      overrides.setProperty(JoyentConstants.JOYENT_CERT_FINGERPRINT, TestConstants.CERT_FINGERPRINT);
      overrides.setProperty(JoyentConstants.JOYENT_CERT_CLASSPATH, TestConstants.CERT_CLASSPATH);
      return overrides;
   }
}
