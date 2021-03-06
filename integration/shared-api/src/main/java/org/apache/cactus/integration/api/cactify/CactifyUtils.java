/* 
 * ========================================================================
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.apache.cactus.integration.api.cactify;

import org.codehaus.cargo.container.internal.util.ResourceUtils;
import org.codehaus.cargo.module.webapp.WebXml;
import org.codehaus.cargo.util.log.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Util class for cactifying purposes.
 * @author tahchiev
 */
public class CactifyUtils {
	
    /**
     * The cargo ResourceUtils.
     */
    private ResourceUtils utils = new ResourceUtils();
    /**
     * The abstract cargo logger used both by Ant and Maven2.
     */
    private Logger logger;
	

    /**
     * Log debug the given message.
     * @param msg
     */
    public void debug(String msg)
    {
    	getLogger().debug(msg, this.getClass().getName());
    }
    /**
     * Log info the given message.
     * @param msg
     */
    public void info(String msg)
    {
    	getLogger().info(msg, this.getClass().getName());
    }
    /**
     * Log warn the given message.
     * @param msg
     */
    public void warn(String msg)
    {
    	getLogger().warn(msg, this.getClass().getName());
    }
    
    /**
     * Adds the definitions corresponding to the nested redirector elements to
     * the provided deployment descriptor. 
     * 
     * @param theWebXml The deployment descriptor
     */
    public void addRedirectorDefinitions(WebXml theWebXml, List redirectors)
    {
        boolean filterRedirectorDefined = false;
        boolean jspRedirectorDefined = false;
        boolean servletRedirectorDefined = false;
        
        // add the user defined redirectors
        for (Iterator i = redirectors.iterator(); i.hasNext();)
        {
        
            Redirector redirector = (Redirector) i.next();
            if (redirector instanceof FilterRedirector)
            {
                filterRedirectorDefined = true;
            }
            else if (redirector instanceof JspRedirector)
            {
                jspRedirectorDefined = true;
            }
            else if (redirector instanceof ServletRedirector)
            {
                servletRedirectorDefined = true;
            }
            redirector.mergeInto(theWebXml);
        }

        // now add the default redirectors if they haven't been provided by
        // the user
        if (!filterRedirectorDefined)
        {
            new FilterRedirector(getLogger())
                .mergeInto(theWebXml);
        }
        if (!servletRedirectorDefined)
        {
            new ServletRedirector(getLogger())
                .mergeInto(theWebXml);
        }
        if (!jspRedirectorDefined)
        {
            new JspRedirector(getLogger()).mergeInto(theWebXml);
        }
    }
    /**
     * Getter method for the logger.
     * @return AbstractLogger
     */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Setter method for the logger.
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

}
