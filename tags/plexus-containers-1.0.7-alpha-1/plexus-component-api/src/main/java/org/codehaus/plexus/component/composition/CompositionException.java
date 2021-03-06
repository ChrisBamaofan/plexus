package org.codehaus.plexus.component.composition;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class CompositionException
    extends Exception
{

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public CompositionException( String s )
    {
        super( s );
    }

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message   The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public CompositionException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
