package com.reucon.maven.plugin.openfire;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

import java.util.AbstractMap;
import java.util.Set;

/**
 * @version $Id: ReflectionProperties.java 1035 2008-04-08 17:21:16Z srt $
 * @todo merge with resources/assembly plugin
 */
public class ReflectionProperties
        extends AbstractMap
{
    private MavenProject project;

    public ReflectionProperties(MavenProject project)
    {
        this.project = project;
    }

    public synchronized Object get(Object key)
    {
        Object value = null;
        try
        {
            value = ReflectionValueExtractor.evaluate(String.valueOf(key), project);
        }
        catch (Exception e)
        {
            //TODO: remove the try-catch block when ReflectionValueExtractor.evaluate() throws no more exceptions
        }
        return value;
    }

    public Set entrySet()
    {
        throw new UnsupportedOperationException("Cannot enumerate properties in a project");
    }
}
