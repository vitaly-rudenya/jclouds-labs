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
package org.jclouds.joyent.blobstore;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.joyent.JoyentConstants;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author vitaly.rudenya
 */
@Singleton
public class JoyentBlobRequestSigner implements HttpRequestFilter {

    private static final Log LOG = LogFactory.getLog(JoyentBlobRequestSigner.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy zzz", Locale.ENGLISH);
    private static final String AUTHZ_HEADER = "Signature keyId=\"/%s/keys/%s\",algorithm=\"rsa-sha256\","
            + "signature=\"%s\"";

    private static final String SIGNING_ALGORITHM = "SHA256WithRSAEncryption";
    private static final String AUTHZ_SIGNING_STRING = "date: %s";

    private volatile KeyPair keyPair_;
    private String identity;

    @Inject
    @Named(JoyentConstants.JOYENT_CERT_FINGERPRINT)
    private String fingerPrint_;

    @Inject
    @Named(JoyentConstants.JOYENT_CERT_CLASSPATH)
    private String certClasspath_;


    @Inject
    public JoyentBlobRequestSigner(@org.jclouds.location.Provider
                                   Supplier<Credentials> creds) {
        identity = creds.get().identity;
    }

    @Override
    public HttpRequest filter(HttpRequest request) throws HttpException {
        LOG.debug("signing request: " + request.getHeaders());

        if (keyPair_ == null) {
            synchronized (this) {
                if (keyPair_ == null) {
                    try {
                        keyPair_ = getKeyPair(certClasspath_);
                    } catch (IOException e) {
                        LOG.error(e);
                        throw new HttpException("Can't load key pair", e);
                    }
                }
            }
        }

        HttpRequest res = request;

        String reqURL = request.getEndpoint().toString();
        String path = request.getEndpoint().getPath();
        String storPath = "/" + identity + "/" + JoyentConstants.STOR_PATH;
        if (!path.startsWith(storPath)) {
            int pathInd = reqURL.lastIndexOf(path);
            String newUrl = reqURL.substring(0, pathInd) + storPath + reqURL.substring(pathInd);
            res = res.toBuilder().endpoint(newUrl).build();
        }

        String date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
        if (date == null) {
            Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
            date = DATE_FORMAT.format(now);
            LOG.debug("setting date header: " + date);
            res = res.toBuilder().addHeader(HttpHeaders.DATE, date).build();
        }
        try {
            Signature sig = Signature.getInstance(SIGNING_ALGORITHM);
            sig.initSign(keyPair_.getPrivate());
            String signingString = String.format(AUTHZ_SIGNING_STRING, date);
            sig.update(signingString.getBytes("UTF-8"));
            byte[] signedDate = sig.sign();
            byte[] encodedSignedDate = Base64.encode(signedDate);
            String authzHeader = String.format(AUTHZ_HEADER, identity, fingerPrint_, new String(encodedSignedDate));
            res = res.toBuilder().addHeader(HttpHeaders.AUTHORIZATION, authzHeader).build();
        } catch (NoSuchAlgorithmException e) {
            throw new HttpException("invalid algorithm", e);
        } catch (InvalidKeyException e) {
            throw new HttpException("invalid key", e);
        } catch (SignatureException e) {
            throw new HttpException("invalid signature", e);
        } catch (UnsupportedEncodingException e) {
            throw new HttpException("invalid encoding", e);
        }

        return res;
    }

    private static KeyPair getKeyPair(String keyPath) throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader(JoyentBlobRequestSigner.class.getResourceAsStream(keyPath)));
        Security.addProvider(new BouncyCastleProvider());
        PEMReader pemReader = new PEMReader(br);
        KeyPair kp = (KeyPair) pemReader.readObject();
        pemReader.close();
        return kp;
    }
}
