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

import org.jclouds.openstack.swift.domain.ObjectInfo;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date: 15.10.13
 * Time: 11:55
 *
 * @author vitaly.rudenya
 */
public class JoyentObject implements ObjectInfo {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ENGLISH);
    public static final String TYPE_FOLDER = "directory";
    public static final String TYPE_BLOB = "object";

    private String name;
    private String type;
    private String mtime;
    private String etag;
    private Long size;

    @Override
    public URI getUri() {
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public byte[] getHash() {
        return etag == null ? new byte[0] : etag.getBytes();
    }

    @Override
    public Long getBytes() {
        return size;
    }

    @Override
    public String getContentType() {
        return type;
    }

    @Override
    public Date getLastModified() {
        try {
            return mtime == null ? null : DATE_FORMAT.parse(mtime);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public String getContainer() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public int compareTo(ObjectInfo o) {
        return name.compareTo(o.getName());
    }
}
