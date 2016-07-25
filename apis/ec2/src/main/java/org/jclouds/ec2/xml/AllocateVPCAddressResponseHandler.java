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
package org.jclouds.ec2.xml;

import org.jclouds.ec2.domain.ElasticIPAddress;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;

public class AllocateVPCAddressResponseHandler extends HandlerWithResult<ElasticIPAddress> {

    @Resource
    protected Logger logger = Logger.NULL;

    private String publicIp;

    private String allocationId;

    private StringBuilder currentText = new StringBuilder();

    protected String currentOrNull() {
        String returnVal = currentText.toString().trim();
        return returnVal.equals("") ? null : returnVal;
    }

    public void endElement(String uri, String name, String qName) {
        if (qName.equals("publicIp")) {
            publicIp = currentOrNull();
        } else if (qName.equals("allocationId")) {
            allocationId = currentOrNull();
        }
        currentText.setLength(0);
    }

    public void characters(char ch[], int start, int length) {
        currentText.append(ch, start, length);
    }

    @Override
    public ElasticIPAddress getResult() {
        return new ElasticIPAddress(publicIp, allocationId);
    }
}
