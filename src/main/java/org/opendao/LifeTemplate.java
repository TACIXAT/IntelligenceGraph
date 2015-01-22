package org.opendao.IntelligenceGraph;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.attribute.Geoshape;

public class LifeTemplate {
	public static void initializeGraph(TitanGraph intelligenceGraph) {
		intelligenceGraph.makePropertyKey("type").dataType(String.class).make();
		intelligenceGraph.makePropertyKey("owner").dataType(String.class).make();
		intelligenceGraph.makePropertyKey("apiKey").dataType(String.class).cardinality(Cardinality.SINGLE).make();
		intelligenceGraph.makePropertyKey("name").dataType(String.class).make();
		intelligenceGraph.makePropertyKey("born").dataType(Long.class).make();
		intelligenceGraph.makePropertyKey("date").dataType(Long.class).make();
		intelligenceGraph.makePropertyKey("notes").dataType(String.class).make();
		intelligenceGraph.makePropertyKey("geoloc").dataType(Geoshape.class).make();
		

		// PERSON
		Vertex personVertex = intelligenceGraph.addVertex(null);
		personVertex.setProperty("type", "schema");
		personVertex.setProperty("name", "person");

		// ATTR:NAME
		Vertex namePropertyVertex = intelligenceGraph.addVertex(null);
		namePropertyVertex.setProperty("type", "property");
		namePropertyVertex.setProperty("name", "name");
		namePropertyVertex.setProperty("dataType", "text");

		// ATTR:BORN
		Vertex bornPropertyVertex = intelligenceGraph.addVertex(null);
		bornPropertyVertex.setProperty("type", "property");
		bornPropertyVertex.setProperty("name", "born");
		bornPropertyVertex.setProperty("dataType", "date");

		// PERSON --has-- ATTR:NAME
		intelligenceGraph.addEdge(null, personVertex, namePropertyVertex, "has");

		// PERSON --has-- ATTR:BORN
		intelligenceGraph.addEdge(null, personVertex, bornPropertyVertex, "has");

		// LOCATION 
		Vertex locationVertex = intelligenceGraph.addVertex(null);
		locationVertex.setProperty("type", "schema");
		locationVertex.setProperty("name", "location");

		// ATTR:GEOLOC
		Vertex geoPropertyVertex = intelligenceGraph.addVertex(null);
		geoPropertyVertex.setProperty("type", "property");
		geoPropertyVertex.setProperty("name", "geoloc");
		geoPropertyVertex.setProperty("dataType", "geopoint");

		// LOCATION --has-- ATTR:NAME
		intelligenceGraph.addEdge(null, locationVertex, namePropertyVertex, "has");

		// LOCATION --has-- ATTR:GEOLOC
		intelligenceGraph.addEdge(null, locationVertex, geoPropertyVertex, "has");

		// EVENT
		Vertex eventVertex = intelligenceGraph.addVertex(null);
		eventVertex.setProperty("type", "schema");
		eventVertex.setProperty("name", "event");

		// ATTR:DATE
		Vertex datePropertyVertex = intelligenceGraph.addVertex(null);
		datePropertyVertex.setProperty("type", "property");
		datePropertyVertex.setProperty("name", "date");
		datePropertyVertex.setProperty("dataType", "date");

		// EVENT --has-- ATTR:NAME
		intelligenceGraph.addEdge(null, eventVertex, namePropertyVertex, "has");

		// EVENT --has-- ATTR:DATE
		intelligenceGraph.addEdge(null, eventVertex, datePropertyVertex, "has");

		// PERSON --connects-- EVENT
		intelligenceGraph.addEdge(null, personVertex, eventVertex, "connects");

		// EVENT --connects-- LOCATION
		intelligenceGraph.addEdge(null, eventVertex, locationVertex, "connects");

		// ADMIN
		// Vertex userVertex = intelligenceGraph.addVertex(null);
		// userVertex.setProperty("type", "user");
		// userVertex.setProperty("name", "TACIXAT");
		// userVertex.setProperty("apiKey", "douggey");
		intelligenceGraph.commit();
		// intelligenceGraph.makePropertyKey("phoneNumber").dataType(String.class).make();
		// intelligenceGraph.makePropertyKey("emailAddress").dataType(String.class).make();
		// intelligenceGraph.makePropertyKey("company").dataType(String.class).make();
		// intelligenceGraph.makePropertyKey("timestamp").dataType(Integer.class).indexed("search", Edge.class).make();
		// intelligenceGraph.makePropertyKey("location").dataType(Geoshape.class).indexed("search", Edge.class).make();
		return;
	}

	public static void updateGraph(TitanGraph intelligenceGraph) {
		// intelligenceGraph.makePropertyKey("date").dataType(Long.class).make();

		// // ATTR:DATE
		// Vertex datePropertyVertex = intelligenceGraph.addVertex(null);
		// datePropertyVertex.setProperty("type", "property");
		// datePropertyVertex.setProperty("name", "date");
		// datePropertyVertex.setProperty("dataType", "date");

		// Vertex eventVertex = intelligenceGraph.query()
		// 						.has("type", "schema")
		// 						.has("name", "event")
		// 						.vertices().iterator().next();

		// // EVENT --has-- ATTR:NAME
		// intelligenceGraph.addEdge(null, eventVertex, datePropertyVertex, "has");
		// intelligenceGraph.commit();
		return;
	}
}