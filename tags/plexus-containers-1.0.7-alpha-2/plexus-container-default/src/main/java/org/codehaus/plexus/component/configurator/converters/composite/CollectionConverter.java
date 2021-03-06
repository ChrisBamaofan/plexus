package org.codehaus.plexus.component.configurator.converters.composite;

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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class CollectionConverter
    extends AbstractConfigurationConverter
{
    public boolean canConvert( Class type )
    {
        return Collection.class.isAssignableFrom( type ) && !Map.class.isAssignableFrom( type );
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassRealm classRealm, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Object retValue = fromExpression( configuration, expressionEvaluator, type );
        if ( retValue != null )
        {
            return retValue;
        }

        Class implementation = getClassForImplementationHint( null, configuration, classRealm );

        if ( implementation != null )
        {
            retValue = instantiateObject( implementation );
        }
        else
        {
            // we can have 2 cases here:
            //  - provided collection class which is not abstract
            //     like Vector, ArrayList, HashSet - so we will just instantantiate it
            // - we have an abtract class so we have to use default collection type
            int modifiers = type.getModifiers();

            if ( Modifier.isAbstract( modifiers ) )
            {
                retValue = getDefaultCollection( type );
            }
            else
            {
                try
                {
                    retValue = type.newInstance();
                }
                catch ( IllegalAccessException e )
                {
                    String msg = "An attempt to convert configuration entry " + configuration.getName() + "' into " +
                        type + " object failed: " + e.getMessage();

                    throw new ComponentConfigurationException( msg, e );
                }
                catch ( InstantiationException e )
                {
                    String msg = "An attempt to convert configuration entry " + configuration.getName() + "' into " +
                        type + " object failed: " + e.getMessage();

                    throw new ComponentConfigurationException( msg, e );
                }
            }
        }
        // now we have collection and we have to add some objects to it

        for ( int i = 0; i < configuration.getChildCount(); i++ )
        {
            PlexusConfiguration c = configuration.getChild( i );

            Class childType = getImplementationClass( null, baseType, c, classRealm );

            ConfigurationConverter converter = converterLookup.lookupConverterForType( childType );

            Object object = converter.fromConfiguration( converterLookup, c, childType, baseType, classRealm,
                                                         expressionEvaluator, listener );

            Collection collection = (Collection) retValue;
            collection.add( object );
        }

        return retValue;
    }

    protected Collection getDefaultCollection( Class collectionType )
    {
        Collection retValue = null;

        if ( List.class.isAssignableFrom( collectionType ) )
        {
            retValue = new ArrayList();
        }
        else if ( Set.class.isAssignableFrom( collectionType ) )
        {
            retValue = new HashSet();
        }

        return retValue;
    }

}
