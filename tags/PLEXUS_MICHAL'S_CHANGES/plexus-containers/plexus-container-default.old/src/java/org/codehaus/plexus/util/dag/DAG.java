package org.codehaus.plexus.util.dag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAG = Directed Acyclic Graph
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class DAG implements Cloneable
{
    //------------------------------------------------------------
    //Fields
    //------------------------------------------------------------
    /**
     * Nodes will be kept in two data strucures at the same time
     * for faster processing
     */
    /** Maps vertex's label to vertex */
    private Map vertexMap = new HashMap();

    /** Conatin list of all verticies */
    private List vertexList = new ArrayList();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     *
     */
    public DAG()
    {
        super();
    }

    // ------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------

    /**
     * @return
     */
    public List getVerticies()
    {
        return vertexList;
    }


    public Set getLabels()
    {
        final Set retValue = vertexMap.keySet();
        return retValue;
    }

    // ------------------------------------------------------------
    // Implementation
    // ------------------------------------------------------------

    /**
     * Adds vertex to DAG. If vertex of given label alredy exist in DAG
     * no vertex is added
     * @param label  The lable of the Vertex
     * @return  New vertex if vertext of given label was not presenst in the DAG
     *          or exising vertex if vertex of given labale was alredy added to DAG
     */
    public Vertex addVertex( final String label )
    {
        // check if vertex is alredy in DAG
        if ( vertexMap.containsKey( label ) )
        {
            return (Vertex) vertexMap.get( label );
        }

        final Vertex vertex = new Vertex( label );
        vertexMap.put( label, vertex );
        vertexList.add( vertex );
        return vertex;
    }

    public void addEdge( final String from, final String to )
    {
        final Vertex v1 = addVertex( from );
        final Vertex v2 = addVertex( to );
        v1.addEdgeTo( v2 );
        v2.addEdgeFrom( v1 );
    }

    public Vertex getVertex( final String label )
    {
        final Vertex retValue = (Vertex) vertexMap.get( label );
        return retValue;
    }

    public boolean hasEdge( final String label1, final String label2 )
    {
        final Vertex v1 = getVertex( label1 );
        if ( v1 == null )
        {
            throw new IllegalArgumentException( "getAdjacentLabels: A vertex for label '" + label1 + "' must exist" );
        }
        final Vertex v2 = getVertex( label2 );
        if ( v2 == null )
        {
            throw new IllegalArgumentException( "getAdjacentLabels: A vertex for label '" + label2 + "' must exist" );
        }
        return v1.getChildren().contains( v2 );

    }

    /**
     * @param label
     * @return
     */
    public List getChildLabels( final String label )
    {
        final Vertex vertex = getVertex( label );
        if ( vertex == null )
        {
            throw new IllegalArgumentException( "getChildLabels: A vertex for label '" + label + "' must exist" );
        }
        return vertex.getChildLabels();
    }
    
    /**
     * @param label
     * @return
     */
    public List getParentLabels( final String label )
    {
        final Vertex vertex = getVertex( label );
        if ( vertex == null )
        {
            throw new IllegalArgumentException( "getChildLabels: A vertex for label '" + label + "' must exist" );
        }
        return vertex.getChildLabels();
    }
    
    
    /**      
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException 
    {                
        Object o = super.clone();	// this is what's failing..               
        return o;
    }
   
    
}
