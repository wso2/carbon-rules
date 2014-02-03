/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
/**
 * Create Name spaces from the data collected through the UI
 */
package org.wso2.carbon.rule.ws.ui.ns;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;

public class NameSpacesFactory {

    private static final Log log = LogFactory.getLog(NameSpacesFactory.class);

    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();

    private static NameSpacesFactory ourInstance = new NameSpacesFactory();

    public static NameSpacesFactory getInstance() {
        return ourInstance;
    }

    private NameSpacesFactory() {
    }

    public Collection<OMNamespace> createNameSpaces(String id,
                                                    String opName,
                                                    HttpSession httpSession) {

        Collection<OMNamespace> namespaces = new ArrayList<OMNamespace>();

        if (!assertIDNotEmpty(id) || !assertOperationNameNotEmpty(opName)) {
            return null;
        }

        NameSpacesInformationRepository repository =
                (NameSpacesInformationRepository) httpSession.getAttribute(
                        NameSpacesInformationRepository.NAMESPACES_INFORMATION_REPOSITORY);
        if (repository == null) {
            return namespaces;
        }

        NameSpacesInformation information = repository.getNameSpacesInformation(opName, id);
        if (information == null) {
            return namespaces;
        }
        if (log.isDebugEnabled()) {
            log.debug("Getting NameSpaces :" + information + " for id :" + id);
        }

        Collection<String> ns = information.getPrefixes();
        for (String prefix : ns) {
            if (prefix == null) {
                continue;
            }
            String uri = information.getNameSpaceURI(prefix);
            if (uri == null || "".equals(uri)) {
                continue;
            }
            namespaces.add(OM_FACTORY.createOMNamespace(uri, prefix));
        }
        information.removeAllNameSpaces();
        return namespaces;
    }

    private static boolean assertIDNotEmpty(String id) {
        if (id == null || "".equals(id)) {
            if (log.isDebugEnabled()) {
                log.debug("Provided id is empty or null ,returning a null as QName");
            }
            return false;
        }
        return true;
    }

    private static boolean assertOperationNameNotEmpty(String source) {
        if (source == null || "".equals(source)) {
            if (log.isDebugEnabled()) {
                log.debug("Provided operation is empty or null ,returning a null as QName");
            }
            return false;
        }
        return true;
    }
}
