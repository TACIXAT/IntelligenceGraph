package org.opendao.IntelligenceGraph;

import com.tinkerpop.blueprints.Vertex;

import java.lang.Iterable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


import org.codehaus.jackson.map.annotate.JsonDeserialize;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MappableVertex")
public class MappableVertex {
	private Vertex vertex;
    @XmlElement(name = "_id") 
	private Long id;
	@JsonDeserialize(as=Map.class, contentAs=String.class, keyAs=String.class)
    @XmlElement(name = "properties") 
	private Map<String, String> propertyMap;
	@XmlElement(name = "neighbors")
	private List<Long> neighbors = new ArrayList<Long>();

	public MappableVertex() {
		// intentionally blank
	}

	public MappableVertex(Vertex vertex) {
		this.vertex = vertex;
		System.out.println(vertex.getId().getClass().getName());
		this.id = (Long)vertex.getId();
		this.propertyMap = getMapFromVertex(vertex);
		this.neighbors = null;
	}

	public MappableVertex(Vertex vertex, Iterable<Vertex> neighbors) {
		this.vertex = vertex;
		System.out.println(vertex.getId().getClass().getName());
		this.id = (Long)vertex.getId();
		this.propertyMap = getMapFromVertex(vertex);
		for(Vertex v : neighbors) {
			this.neighbors.add((Long)v.getId());
		}
	}

	public MappableVertex(String error) {
		this.vertex = null;
		this.id = -1l;
		this.propertyMap = new HashMap<String, String>();
		this.propertyMap.put("error", error);
	}

	public Map<String, String> getMapFromVertex(Vertex vertex) {
        Map<String, String> vertexMap = new HashMap<String, String>();
        for(String key : vertex.getPropertyKeys()) {
        	if(!key.equals("owner"))
	            vertexMap.put(key, vertex.getProperty(key).toString());
        }
        return vertexMap;
    }
}