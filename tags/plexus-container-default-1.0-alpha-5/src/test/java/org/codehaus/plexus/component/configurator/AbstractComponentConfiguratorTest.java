package org.codehaus.plexus.component.configurator;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import junit.framework.Assert;
import junit.framework.TestCase;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractComponentConfiguratorTest
    extends TestCase
{
    public AbstractComponentConfiguratorTest( String s )
    {
        super( s );
    }

    protected abstract ComponentConfigurator getComponentConfigurator();

    public void testComponentConfigurator()
        throws Exception
    {
        String xml = "<configuration>" + "  <int-value>0</int-value>" + "  <float-value>1</float-value>"
            + "  <long-value>2</long-value>" + "  <double-value>3</double-value>"
            + "  <string-value>foo</string-value>" + "  <important-things>"
            + "    <important-thing><name>jason</name></important-thing>"
            + "    <important-thing><name>tess</name></important-thing>" + "  </important-things>"
            + "  <configuration>" + "      <name>jason</name>" + "  </configuration>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ConfigurableComponent component = new ConfigurableComponent();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );
        
        ClassWorld classWorld = new ClassWorld();
        
        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        assertEquals( "check integer value", 0, component.getIntValue() );

        assertEquals( "check float value", 1.0f, component.getFloatValue(), 0.001f );

        assertEquals( "check long value", 2L, component.getLongValue() );

        Assert.assertEquals( "check double value", 3.0, component.getDoubleValue(), 0.001 );

        assertEquals( "foo", component.getStringValue() );

        List list = component.getImportantThings();

        assertEquals( 2, list.size() );

        assertEquals( "jason", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( "tess", ( (ImportantThing) list.get( 1 ) ).getName() );

        // Embedded Configuration

        PlexusConfiguration c = component.getConfiguration();

        assertEquals( "jason", c.getChild( "name" ).getValue() );
    }

    public void testComponentConfigurationWhereFieldsToConfigureResideInTheSuperclass()
        throws Exception
    {
        String xml = "<configuration>" + "  <name>jason</name>" + "  <address>bollywood</address>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        DefaultComponent component = new DefaultComponent();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();
        
        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        assertEquals( "jason", component.getName() );

        assertEquals( "bollywood", component.getAddress() );
    }

    public void testComponentConfigurationWhereFieldsAreCollections()
        throws Exception
    {
        String xml = "<configuration>" + "  <vector>" + "    <important-thing>" + "       <name>life</name>"
            + "    </important-thing>" + "  </vector>" + "  <set>" + "    <important-thing>"
            + "       <name>life</name>" + "    </important-thing>" + "  </set>"
            + "   <list implementation=\"java.util.LinkedList\">" + "     <important-thing>"
            + "       <name>life</name>" + "    </important-thing>" + "  </list>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithCollectionFields component = new ComponentWithCollectionFields();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();
        
        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        Vector vector = component.getVector();

        assertEquals( "life", ( (ImportantThing) vector.get( 0 ) ).getName() );

        assertEquals( 1, vector.size() );

        Set set = component.getSet();

        assertEquals( 1, set.size() );

        Object[] setContents = set.toArray();

        assertEquals( "life", ( (ImportantThing) setContents[0] ).getName() );

        List list = component.getList();

        assertEquals( list.getClass(), LinkedList.class );

        assertEquals( "life", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( 1, list.size() );
    }

    public void testComponentConfigurationWithCompositeFields()
        throws Exception
    {

        String xml = "<configuration>"
            + "  <thing implementation=\"org.codehaus.plexus.component.configurator.ImportantThing\">"
            + "     <name>I am not abstract!</name>" + "  </thing>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithCompositeFields component = new ComponentWithCompositeFields();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();
        
        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        assertNotNull( component.getThing() );

        assertEquals( "I am not abstract!", component.getThing().getName() );

    }

    public void testInvalidComponentConfiguration()
        throws Exception
    {

        String xml = "<configuration><goodStartElement>theName</badStopElement></configuration>";

        try
        {
            PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test-Invalid>",
                                                                                new StringReader( xml ) );

            fail( "Should have caused an error because of the invalid XML." );
        }
        catch ( PlexusConfigurationException e )
        {
            // should catch this...
            System.out.println("Error Message:\n\n" + e.getLocalizedMessage() + "\n\n");
            System.err.println("Error with stacktrace:\n\n");
            e.printStackTrace();
            System.err.println("\n\n");
        }
        catch ( Exception e )
        {
            fail( "Should have caught the invalid plexus configuration exception." );
        }

    }

    public void testComponentConfigurationWithPropertiesFields()
        throws Exception
    {

        String xml = "<configuration>" + "  <someProperties>" + "     <property>" + "        <name>firstname</name>"
            + "        <value>michal</value>" + "     </property>" + "     <property>"
            + "        <name>lastname</name>" + "        <value>maczka</value>" + "     </property>"
            + "  </someProperties>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithPropertiesField component = new ComponentWithPropertiesField();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();
        
        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        Properties properties = component.getSomeProperties();

        assertNotNull( properties );

        assertEquals( "michal", properties.get( "firstname" ) );

        assertEquals( "maczka", properties.get( "lastname" ) );

    }

}
