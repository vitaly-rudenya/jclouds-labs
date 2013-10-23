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

import com.google.common.base.Charsets;
import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payloads;
import org.jclouds.joyent.parsers.JoyentObject;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Set;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Date: 25.09.13
 * Time: 4:15
 *
 * @author : vitaly.rudenya
 */
@Test(singleThreaded = true)
public class JoyentObjectStorageClientLiveTest extends BaseBlobStoreIntegrationTest {

   private static final String PROVIDER = "joyentblob";
   private static final String USER_NAME = "altoros2";
   private static final String BLOB_NAME = "jcloudsTest";
   private static final String BLOB_NAME_2 = "jcloudsTest2";
   private static final String CERT_FINGERPRINT = "04:92:7b:23:bc:08:4f:d7:3b:5a:38:9e:4a:17:2e:df";
   private static final String CERT_CLASSPATH = "/data/id_rsa"; //todo your certificate path

   public JoyentObjectStorageClientLiveTest() {
      provider = PROVIDER;
   }

   public JoyentBlobClient getApi() {
      return view.unwrap(JoyentProviderMetadata.CONTEXT_TOKEN).getApi();
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateContainer() throws Exception {
      boolean created = false;
      String privateContainer;
      while (!created) {
         privateContainer = prefix + new SecureRandom().nextInt();
         try {
            created = getApi().createContainer(privateContainer);
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      Set<JoyentObject> response = getApi().listContainers();
      assert null != response;
      long containerCount = response.size();
      assertTrue(containerCount >= 1);
   }

   @Test(enabled = true)
   public void testCreateNewBlob() throws Exception {

      JoyentBlobClient joyentBlobClient = getApi();

      Blob blob = joyentBlobClient.newBlob();
      blob.setPayload(getClass().getResourceAsStream("/data/Master-Yoda.jpg"));
      Payloads.calculateMD5(blob);
      blob.getMetadata().getContentMetadata().setContentType("image/jpeg");
      blob.getMetadata().setName(BLOB_NAME);

      String container = getContainerName();
      blob.getMetadata().setContainer(container);

      joyentBlobClient.putBlob(container, blob);

      assertTrue(joyentBlobClient.blobExists(container, BLOB_NAME));
      Blob joyentBlob = joyentBlobClient.getBlob(container, BLOB_NAME);
      assertNotNull(joyentBlob);
      assertEquals(blob.getPayload().getContentMetadata().getContentMD5(),
              joyentBlob.getPayload().getContentMetadata().getContentMD5());
   }

   @Test(enabled = true)
   public void testPutBlobWithBlobStore() throws Exception {
      BlobStore blobStore = view.getBlobStore();
      Blob newBlob = blobStore.blobBuilder(BLOB_NAME_2)
                              .payload(getClass().getResourceAsStream("/data/Master-Yoda.jpg"))
                              .calculateMD5()
                              .contentType("image/jpeg")
                              .build();

      String container = getContainerName();
      blobStore.putBlob(container, newBlob);

      assertTrue(blobStore.blobExists(container, BLOB_NAME_2));
      Blob joyentBlob = blobStore.getBlob(container, BLOB_NAME_2);
      assertNotNull(joyentBlob);
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testObjectOperations() throws Exception {
      String data = "Here is my data";

      String privateContainer = getContainerName();

      // Test PUT with string data, ETag hash, and a piece of metadata
      Blob object = getApi().newBlob();
      object.getMetadata().setName(BLOB_NAME);
      object.setPayload(data);
      Payloads.calculateMD5(object);
      object.getMetadata().getContentMetadata().setContentType("text/plain");
      byte[] md5 = object.getMetadata().getContentMetadata().getContentMD5();
      object.getMetadata().getUserMetadata().put("mykey", "metadata-value");
      String newEtag = getApi().putBlob(privateContainer, object);
      assertEquals(base16().lowerCase().encode(md5),
              base16().lowerCase().encode(object.getMetadata().getContentMetadata().getContentMD5()));

      // Test HEAD of object
      BlobMetadata metadata = getApi().getBlobMetadata(privateContainer, object.getMetadata().getName());
      assertEquals(metadata.getName(), object.getMetadata().getName());
      assertEquals(metadata.getContentMetadata().getContentType(), "text/plain");
      assertEquals(base16().lowerCase().encode(md5),
              base16().lowerCase().encode(object.getMetadata().getContentMetadata().getContentMD5()));
      assertEquals(metadata.getETag(), newEtag);

      // Test GET of missing object
      assert getApi().getBlob(privateContainer, "non-existent-object") == null;

      // Test GET of object (including updated metadata)
      Blob getBlob = getApi().getBlob(privateContainer, object.getMetadata().getName());
      assertEquals(Strings2.toString(getBlob.getPayload()), data);
      assertEquals(getBlob.getMetadata().getName(), object.getMetadata().getName());
      assertEquals(getBlob.getPayload().getContentMetadata().getContentLength(), Long.valueOf(data.length()));
      assertEquals(getBlob.getMetadata().getContentMetadata().getContentType(), "text/plain");
      assertEquals(base16().lowerCase().encode(md5),
              base16().lowerCase().encode(getBlob.getMetadata().getContentMetadata().getContentMD5()));
      assertEquals(newEtag, getBlob.getMetadata().getETag());

      // test listing
      Blob response = getApi().getBlob(privateContainer, BLOB_NAME);
      assertEquals(response.getMetadata().getName(), object.getMetadata().getName());

      ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes(Charsets.UTF_8));
      object = getApi().newBlob();
      object.getMetadata().setName(BLOB_NAME_2);
      object.setPayload(bais);
      object.getPayload().getContentMetadata().setContentLength((long) data.getBytes().length);
      assertEquals(base16().lowerCase().encode(md5),
              base16().lowerCase().encode(getBlob.getMetadata().getContentMetadata().getContentMD5()));


      getApi().removeBlob(privateContainer, BLOB_NAME);
      getApi().removeBlob(privateContainer, BLOB_NAME_2);
   }

   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(Constants.PROPERTY_IDENTITY, USER_NAME);
      overrides.setProperty(JoyentConstants.JOYENT_CERT_FINGERPRINT, CERT_FINGERPRINT);
      overrides.setProperty(JoyentConstants.JOYENT_CERT_CLASSPATH, CERT_CLASSPATH);
      return overrides;
   }
}
