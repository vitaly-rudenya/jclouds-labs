package org.jclouds.joyent;

import org.jclouds.providers.internal.BaseProviderMetadataTest;
import org.testng.annotations.Test;

/**
 * @author Vitaly Rudenya
 */
@Test(testName = "JoyentProviderTest")
public class JoyentBlobProviderTest extends BaseProviderMetadataTest {

   public JoyentBlobProviderTest() {
      super(new JoyentProviderMetadata(), new JoyentApiMetadata());
   }
}
