package org.codehaus.plexus.component.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentSetDescriptor
{
    private List components;

    private List dependencies;

    private boolean isolatedRealm;

    private String id;

    private String url;

    public List getComponents()
    {
        return components;
    }

    public void addComponentDescriptor( ComponentDescriptor cd )
    {
        if ( components == null )
        {
            components = new ArrayList();
        }

        components.add( cd );
    }

    public void setComponents( List components )
    {
        this.components = components;
    }

    public List getDependencies()
    {
        return dependencies;
    }

    public void addDependency( ComponentDependency cd )
    {
        if ( dependencies == null )
        {
            dependencies = new ArrayList();
        }

        dependencies.add( cd );
    }

    public void setDependencies( List dependencies )
    {
        this.dependencies = dependencies;
    }

    public void setIsolatedRealm( boolean isolatedRealm )
    {
        this.isolatedRealm = isolatedRealm;
    }

    public boolean isIsolatedRealm()
    {
        return isolatedRealm;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }
}
