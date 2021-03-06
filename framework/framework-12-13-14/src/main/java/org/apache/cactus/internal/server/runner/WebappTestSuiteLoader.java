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
package org.apache.cactus.internal.server.runner;

import org.apache.cactus.internal.util.ClassLoaderUtils;

/**
 * Dynamic class loader to load classes from the webapp classpath.
 *
 * @version $Id: WebappTestSuiteLoader.java 238991 2004-05-22 11:34:50Z vmassol $
 */
public class WebappTestSuiteLoader
{
    /**
     * Try to load the test suite class using both the context class loader
     * or the class loader that loaded this class.
     *
     * @param theSuiteClassName the test suite class to load
     * @return the test suite class object
     * @throws ClassNotFoundException if failed to load the class
     */
    public Class load(String theSuiteClassName) throws ClassNotFoundException
    {
        return ClassLoaderUtils.loadClass(theSuiteClassName, this.getClass());
    }

    /**
     * Not implemented. Used to reload a class.
     *
     * @param theClass the class to reload
     * @return the reloaded class
     * @throws ClassNotFoundException if an error occurs during reloading
     */
    public Class reload(Class theClass) throws ClassNotFoundException
    {
        throw new ClassNotFoundException("Feature not implemented");
    }
}
