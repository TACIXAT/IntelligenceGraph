
package org.opendao.IntelligenceGraph;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

import javax.servlet.ServletContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.lang.Iterable;
import java.lang.NumberFormatException;
import com.google.common.collect.Iterables;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Query;
import com.tinkerpop.blueprints.Direction;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.thinkaurelius.titan.core.attribute.Geo;
import com.thinkaurelius.titan.core.attribute.Cmp;
import com.thinkaurelius.titan.core.attribute.Text;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.TitanVertexQuery;

@Path("/utility")
public class IntelligenceGraph {
	@Context
    ServletContext context;

    @GET 
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        String hello = "Hello World!";
        return hello;
    }

    // getVertexTypes
    // requires: apiKey
    @POST
    @Path("get_vertex_types")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getVertexTypes(HashMap<String, String> properties) {
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        List<String> result = new ArrayList<String>();

        if(!properties.containsKey("apiKey")) {
            result.add("ERROR: Please provide an API Key!");
            return result;
        }

        String apiKey = properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result.add("ERROR: API Key not found!");
            return result;
        }

        Iterable<Vertex> schemaVertices = intelligenceGraph.getVertices("type", "schema");
        
        for(Vertex v : schemaVertices) {
            result.add((String)v.getProperty("name"));
        }

        intelligenceGraph.commit();
        return result;
    }

    // getTypeProperties
    // requires: apiKey, type
    @POST
    @Path("get_type_properties")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getTypeProperties(HashMap<String, String> properties) {
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        Map<String, String> result = new HashMap<String, String>();

        if(!properties.containsKey("apiKey")) {
            result.put("ERROR", "Please provide an API Key!");
            return result;
        }

        if(!properties.containsKey("type")) {
            result.put("ERROR", "Please provide a vertex type!");
            return result;
        }

        String apiKey = properties.get("apiKey");
        String type = properties.get("type");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result.put("ERROR", "API Key not found!");
            return result;
        }

        Vertex schemaVertex = getSchemaVertexByType(intelligenceGraph, type);
        if(schemaVertex == null) {
            result.put("ERROR", "Could not find a valid schema for provided type!");
            return result;
        }

        result = getValidProperties(intelligenceGraph, schemaVertex);
        return result;
    }

    // createUser
    // requires: password, apiKey, username
    @POST
    @Path("create_user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String createUser(HashMap<String, String> properties) {
        System.out.println(properties);
        String result = "";
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("password")) {
            result = "ERROR: Unauthorized!";
            return result;
        }

        if(!properties.containsKey("apiKey")) {
            result = "ERROR: Please provide an API Key!";
            return result;
        }

        if(!properties.containsKey("username")) {
            result = "ERROR: Please provide a target user!";
            return result;
        }

        String password = (String)properties.get("password");
        String apiKey = (String)properties.get("apiKey");
        String username = (String)properties.get("username");

        if(!password.equals("D5A1895F31A432D3F93F056DA66503CF")) {
            result = "ERROR: Invalid password!";
            return result;
        }

        Iterable<Vertex> users = intelligenceGraph.query().has("type", "user").has("name", username).vertices();
        if(Iterables.size(users) > 0) {
            result = "ERROR: A user already exists with this name!";
            intelligenceGraph.commit();
            return result;
        }

        String check = getUsername(intelligenceGraph, apiKey);
        if(check != null) {
            result = "ERROR: Provided API key already in use!";
            intelligenceGraph.commit();
            return result;
        }

        Vertex userVertex = intelligenceGraph.addVertex(null);
        userVertex.setProperty("type", "user");
        userVertex.setProperty("name", username);
        userVertex.setProperty("apiKey", apiKey);

        intelligenceGraph.commit();
        result = "SUCCESS!";
        return result;
    }    

    // deleteUser
        // delete user vertex
        // delete vertices .has('owner', username)

    // createGroup

    // addUserToGroup
        // owns group?

    // removeUserFromGroup
        // ownsGroup?

    // updateAPIKey
    // requires: password, apiKey, username
    @POST
    @Path("update_api_key")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateAPIKey(HashMap<String, String> properties) {
        System.out.println(properties);
        String result = "";
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("password")) {
            result = "ERROR: Unauthorized!";
            return result;
        }

        if(!properties.containsKey("apiKey")) {
            result = "ERROR: Please provide a new key!";
            return result;
        }

        if(!properties.containsKey("username")) {
            result = "ERROR: Please provide a target user!";
            return result;
        }

        String password = (String)properties.get("password");
        String apiKey = (String)properties.get("apiKey");
        String username = (String)properties.get("username");

        if(!password.equals("D5A1895F31A432D3F93F056DA66503CF")) {
            result = "ERROR: Invalid password!";
            return result;
        }

        Iterable<Vertex> users = intelligenceGraph.query().has("type", "user").has("name", username).vertices();
        Vertex userVertex = null;
        if(Iterables.size(users) == 1) {
            userVertex = users.iterator().next();
        }

        if(userVertex == null) {
            result = "ERROR: User not found!";
            intelligenceGraph.commit();
            return result;
        }

        String check = getUsername(intelligenceGraph, apiKey);
        if(check != null) {
            result = "ERROR: Provided API key already in use!";
            intelligenceGraph.commit();
            return result;
        }

        userVertex.setProperty("apiKey", apiKey);
        intelligenceGraph.commit();
        result = "SUCCESS!";
        return result;
    }

    // createVertexType

    // createGraph

    // getGraphs

    // deleteGraph

    // createVertex
    // requires: apiKey, type, properties...
    @POST
    @Path("create_vertex")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MappableVertex createVertex(HashMap<String, String> properties) {
        System.out.println(properties);
        MappableVertex result = null;
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            result = new MappableVertex("Please provide an API Key!");
            return result;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result = new MappableVertex("API Key not found!");
            return result;
        }

        if(!properties.containsKey("type")) {
            result = new MappableVertex("Please provide a vertex type!");
            return result;
        }

        String vertexType = (String)properties.get("type");
        System.out.println(vertexType);
        
        Vertex target = getSchemaVertexByType(intelligenceGraph, vertexType);

        if(target != null) {
            Map<String, String> validProperties = getValidProperties(intelligenceGraph, target);

            Vertex newVertex = intelligenceGraph.addVertex(null);
            newVertex.setProperty("owner", username);
            newVertex.setProperty("type", vertexType);
            
            for(String key : (Set<String>)properties.keySet()) {
                if(validProperties.containsKey(key)) {
                    String strValue = properties.get(key);
                    String dataType = validProperties.get(key);
                    Object value = convertProperty(dataType, strValue);
                    
                    if(value == null)
                        continue;

                    newVertex.setProperty(key, value);
                }
            }

            Iterable<Vertex> neighbors = newVertex.getVertices(Direction.BOTH, "connectedTo"); 
            result = new MappableVertex(newVertex, neighbors);
        } else {
            result = new MappableVertex("Could not find a valid schema for provided type!");
        }

        intelligenceGraph.commit();
        return result;
    }

    Object convertProperty(String dataType, String value) {
        if(dataType.equals("text")) {
            return value;
        } else if (dataType.equals("date")) {
            if(value.indexOf("[") < 0) {
                try {
                    Long timestamp = Long.parseLong(value);
                    return timestamp;
                } catch(NumberFormatException e) {
                    return null;
                }
            } else {
                String[] values = value.replaceAll("[\\[\\]]", "").split(",");
                if(values.length != 2)
                    return null;

                String rangeStart = values[0];
                String rangeEnd = values[1];

                try {
                    Long start = Long.parseLong(rangeStart);
                    Long end = Long.parseLong(rangeEnd);
                    DatePair datePair = new DatePair(start, end);
                    return datePair;
                } catch(NumberFormatException e) {
                    return null;
                }

            }
        } else if(dataType.equals("geopoint")) {
            String [] split = value.split(",");
            if(split.length != 2) {
                return null;
            }

            try {
                Float latitude = Float.parseFloat(split[0]);
                Float longitude = Float.parseFloat(split[1]);
                Geoshape geoPoint = Geoshape.point(latitude, longitude);
                return geoPoint;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if(dataType.equals("geocircle")) {
            String [] split = value.split(",");
            System.out.println(split);
            if(split.length != 3) {
                return null;
            }

            try {
                Float latitude = Float.parseFloat(split[0]);
                Float longitude = Float.parseFloat(split[1]);
                Float radius = Float.parseFloat(split[2]);
                System.out.println(latitude);
                System.out.println(longitude);
                System.out.println(radius);
                Geoshape geoCircle = Geoshape.circle(latitude, longitude, radius);
                return geoCircle;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    String getUsername(TitanGraph intelligenceGraph, String apiKey) {
        String username = null;

        Iterable<Vertex> userVertices = intelligenceGraph.getVertices("apiKey", apiKey);
        if(Iterables.size(userVertices) == 1) {
            Vertex userVertex = userVertices.iterator().next();
            username = userVertex.getProperty("name");
        }

        intelligenceGraph.commit();
        return username;
    }

    Map<String, String> getValidProperties(TitanGraph intelligenceGraph, Vertex schemaVertex) {
        Map<String, String> validProperties = new HashMap<String, String>();
        Iterable<Vertex> propertyVertices = schemaVertex.getVertices(Direction.OUT, "has");
        
        for(Vertex v : propertyVertices) {
            if(v.getProperty("type").equals("property")) {
                String propertyName = v.getProperty("name");
                String propertyType = v.getProperty("dataType");
                validProperties.put(propertyName, propertyType);
            }
        }

        intelligenceGraph.commit();
        return validProperties;
    }

    Vertex getSchemaVertexByType(TitanGraph intelligenceGraph, String vertexType) {
        Vertex target = null;
        Iterable<Vertex> schemaVertices = intelligenceGraph.getVertices("type", "schema");
        for(Vertex v : schemaVertices) {
            System.out.println(v.getProperty("name"));
            if(v.getProperty("name").equals(vertexType)) {
                // System.out.println("Name Match!");
                target = v;
                break;
            }
        }
        intelligenceGraph.commit();
        return target;
    }

    // searchGraph
    // requires: apiKey, type, properties...
    @POST
    @Path("search_vertices")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MappableVertex> searchVertices(HashMap properties) {
        System.out.println(properties);
        List<MappableVertex> results = new ArrayList<MappableVertex>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            results.add(new MappableVertex("Please provide an API Key!"));
            return results;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            results.add(new MappableVertex("API Key not found!"));
            return results;
        }

        if(!properties.containsKey("type")) {
            results.add(new MappableVertex("Please provide a vertex type!"));
            return results;
        }

        String vertexType = (String)properties.get("type");
        System.out.println(vertexType);
        
        Vertex target = getSchemaVertexByType(intelligenceGraph, vertexType);

        if(target != null) {
            Map<String, String> validProperties = getValidProperties(intelligenceGraph, target);
            
            Query vertexQuery = intelligenceGraph.query();
            vertexQuery.has("owner", username);
            vertexQuery.has("type", vertexType);
            
            for(String key : (Set<String>)properties.keySet()) {
                if(validProperties.containsKey(key)) {
                    String strValue = (String)properties.get(key);
                    if(!strValue.equals("")) {
                        String dataType = validProperties.get(key);
                        if(dataType.equals("geopoint"))
                            dataType = "geocircle";

                        Object value = convertProperty(dataType, strValue);
                        System.out.println(key);
                        System.out.println(value);
                        if(value == null)
                            continue;

                        if(dataType.equals("geocircle")) {
                            vertexQuery.has(key, Geo.WITHIN, value);
                        } else if(key.equals("notes") || key.equals("name")) {
                            vertexQuery.has(key, Text.CONTAINS, value);
                        } else if((key.equals("born") || key.equals("date")) && strValue.indexOf("[") > -1) { 
                            DatePair datePair = (DatePair) value;
                            vertexQuery.has(key, Cmp.GREATER_THAN_EQUAL, datePair.getStart());
                            vertexQuery.has(key, Cmp.LESS_THAN_EQUAL, datePair.getEnd());
                        } else {
                            vertexQuery.has(key, value);
                        }
                    }
                }
            }

            Iterable<Vertex> vertices = vertexQuery.vertices();
            for(Vertex v : vertices) {
                Iterable<Vertex> neighbors = v.getVertices(Direction.BOTH, "connectedTo"); 
                results.add(new MappableVertex(v, neighbors));
            }
        } else {
            results.add(new MappableVertex("Could not find a valid schema for provided type!"));
        }

        intelligenceGraph.commit();
        return results;
    }

    // deleteVertex
    // requires: apiKey, vertex
    @POST
    @Path("delete_vertex")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> deleteVertex(Map<String, String> properties) {
        Map<String, String> result = new HashMap<String, String>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            result.put("status", "ERROR");
            result.put("ERROR", "Please provide an API Key!");
            return result;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result.put("status", "ERROR");
            result.put("ERROR", "API Key not found!");
            return result;
        }

        if(!properties.containsKey("vertex")) {
            result.put("status", "ERROR");
            result.put("ERROR", "You must provide a vertex to delete!");
            return result;
        }

        Long vertexId;

        try {
            vertexId = Long.parseLong(properties.get("vertex"));
        } catch (NumberFormatException e) {
            result.put("status", "ERROR");
            result.put("ERROR", "Problem parsing vertex IDs!");
            return result;
        }

        result.put("vertex", vertexId.toString());
        Vertex vertex = intelligenceGraph.getVertex(vertexId);

        if(vertex != null && !vertex.getProperty("owner").equals(username)) {
            vertex = null;
        }

        if(vertex == null) {
            result.put("status", "ERROR");
            result.put("ERROR", "Vertex not found!");
            intelligenceGraph.commit();
            return result;
        }

        String vertexType = vertex.getProperty("type");
        Iterable<Edge> edges = vertex.getEdges(Direction.BOTH);
        for(Edge e : edges) {
            intelligenceGraph.removeEdge(e);
        }
        intelligenceGraph.removeVertex(vertex);
        intelligenceGraph.commit();

        result.put("status", "SUCCESS");
        result.put("SUCCESS", "Vertex " + vertexId + " successfully deleted!");
        result.put("type", vertexType);
        return result;
    }


    // updateVertex
    // requires: apiKey, vertex, properties...
    @POST
    @Path("update_vertex")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MappableVertex updateVertex(HashMap<String, String> properties) {
        System.out.println(properties);
        MappableVertex result = null;
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            result = new MappableVertex("Please provide an API Key!");
            return result;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result = new MappableVertex("API Key not found!");
            return result;
        }

        if(!properties.containsKey("vertex")) {
            result = new MappableVertex("Please provide a vertex ID!");
            return result;
        }

        Long vertexId;

        try {
            vertexId = Long.parseLong(properties.get("vertex"));
        } catch (NumberFormatException e) {
            result = new MappableVertex("Problem parsing vertex ID!");
            return result;
        }

        Vertex vertex = intelligenceGraph.getVertex(vertexId);

        if(vertex != null && !vertex.getProperty("owner").equals(username)) {
            vertex = null;
        }

        if(vertex == null) {
            result = new MappableVertex("Vertex not found!");
            intelligenceGraph.commit();
            return result;
        }

        String vertexType = vertex.getProperty("type");
        System.out.println(vertexType);
        
        Vertex target = getSchemaVertexByType(intelligenceGraph, vertexType);

        if(target != null) {
            Map<String, String> validProperties = getValidProperties(intelligenceGraph, target);
            
            for(String key : (Set<String>)properties.keySet()) {
                if(validProperties.containsKey(key)) {
                    String strValue = properties.get(key);
                    System.out.println(key + ":" + strValue);
                    if(strValue == "") {
                        System.out.println("REMOVED: " + key);
                        vertex.removeProperty(key);
                        continue;
                    }

                    String dataType = validProperties.get(key);
                    Object value = convertProperty(dataType, strValue);
                    
                    if(value == null)
                        continue;

                    vertex.setProperty(key, value);
                }
            }

            Iterable<Vertex> neighbors = vertex.getVertices(Direction.BOTH, "connectedTo"); 
            result = new MappableVertex(vertex, neighbors);
        } else {
            result = new MappableVertex("Could not find a valid schema for provided type!");
        }

        intelligenceGraph.commit();
        return result;
    }

    // linkVertices
    // requires: apiKey, vertexA, vertexB
    @POST
    @Path("create_edge")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> createEdge(Map<String, String> properties) {
        Map<String, String> result = new HashMap<String, String>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            result.put("status", "ERROR");
            result.put("ERROR", "Please provide an API Key!");
            return result;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result.put("status", "ERROR");
            result.put("ERROR", "API Key not found!");
            return result;
        }

        if(!properties.containsKey("vertexA") || !properties.containsKey("vertexB")) {
            result.put("status", "ERROR");
            result.put("ERROR", "You must provide 2 vertices to link!");
            return result;
        }

        Long idA, idB;

        try {
            idA = Long.parseLong(properties.get("vertexA"));
            idB = Long.parseLong(properties.get("vertexB"));
        } catch (NumberFormatException e) {
            result.put("status", "ERROR");
            result.put("ERROR", "Problem parsing vertex IDs!");
            return result;
        }

        result.put("vertexA", idA.toString());
        result.put("vertexB", idB.toString());

        Vertex vertexA = intelligenceGraph.getVertex(idA);
        Vertex vertexB = intelligenceGraph.getVertex(idB);

        if(vertexA != null && !vertexA.getProperty("owner").equals(username)) {
            vertexA = null;
        }

        if(vertexB != null && !vertexB.getProperty("owner").equals(username)) {
            vertexB = null;
        }

        if(vertexA == null || vertexB == null) {
            result.put("status", "ERROR");
            String error;
            int status = 0;

            status += vertexA == null ? 1 : 0;
            status += vertexB == null ? 2 : 0;

            switch(status) {
                case 1:
                    error = "The first vertex was not found!";
                    break;
                case 2:
                    error = "The second vertex was not found!";
                    break;
                case 3:
                    error = "Neither vertex was found!";
                    break;
                default:
                    error = "Please contact the administrator with this message: create_edge default case hit!";
                    break;
            }

            result.put("ERROR", error);
            intelligenceGraph.commit();
            return result;
        }

        String typeA = vertexA.getProperty("type");
        String typeB = vertexB.getProperty("type");

        result.put("typeA", typeA);
        result.put("typeB", typeB);

        Vertex schemaA = getSchemaVertexByType(intelligenceGraph, typeA);
        Vertex schemaB = getSchemaVertexByType(intelligenceGraph, typeB);
        Iterable<Vertex> neighborsSchemaA = schemaA.getVertices(Direction.OUT, "connects");
        boolean connects = false;
        System.out.println(schemaA.getId() + ":" + schemaA.getProperty("name"));
        System.out.println(schemaB.getId() + ":" + schemaB.getProperty("name"));
        for(Vertex v : neighborsSchemaA) {
            System.out.println("\t" + v.getId() + ":" + v.getProperty("name"));
            if(v.getId().equals(schemaB.getId())) {
                System.out.println("Match!");
                connects = true;
                break;
            }
        }

        if(!connects) {
            result.put("status", "ERROR");
            result.put("ERROR", "A connection between vertices of these types is not permitted!");
            intelligenceGraph.commit();
            return result;
        }

        Iterable<Vertex> neighborsA = vertexA.getVertices(Direction.OUT, "connectedTo");
        connects = false;
        for(Vertex v : neighborsA) {
            if(v.getId().equals(vertexB.getId())) {
                connects = true;
                break;
            }
        }

        if(connects) {
            result.put("status", "ERROR");
            result.put("ERROR", "Edge already exists between these vertices!");
            intelligenceGraph.commit();
            return result;
        }

        intelligenceGraph.addEdge(null, vertexA, vertexB, "connectedTo");
        intelligenceGraph.commit();
        result.put("status", "SUCCESS");
        result.put("SUCCESS", "Edge: [" + idA + " -- " + idB + "] successfully created!");
        // result.put("vertexA", idA.toString());
        // result.put("vertexB", idB.toString());
        return result;
    }

    // unlinkVertices
    // requires: apiKey, vertexA, vertexB
    @POST
    @Path("delete_edge")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> deleteEdge(Map<String, String> properties) {
        Map<String, String> result = new HashMap<String, String>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            result.put("status", "ERROR");
            result.put("ERROR", "Please provide an API Key!");
            return result;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result.put("status", "ERROR");
            result.put("ERROR", "API Key not found!");
            return result;
        }

        if(!properties.containsKey("vertexA") || !properties.containsKey("vertexB")) {
            result.put("status", "ERROR");
            result.put("ERROR", "You must provide 2 vertices to link!");
            return result;
        }

        Long idA, idB;

        try {
            idA = Long.parseLong(properties.get("vertexA"));
            idB = Long.parseLong(properties.get("vertexB"));
        } catch (NumberFormatException e) {
            result.put("status", "ERROR");
            result.put("ERROR", "Problem parsing vertex IDs!");
            return result;
        }

        result.put("vertexA", idA.toString());
        result.put("vertexB", idB.toString());

        Vertex vertexA = intelligenceGraph.getVertex(idA);
        Vertex vertexB = intelligenceGraph.getVertex(idB);

        if(vertexA != null && !vertexA.getProperty("owner").equals(username)) {
            vertexA = null;
        }

        if(vertexB != null && !vertexB.getProperty("owner").equals(username)) {
            vertexB = null;
        }

        if(vertexA == null || vertexB == null) {
            result.put("status", "ERROR");
            String error;
            int status = 0;

            status += vertexA == null ? 1 : 0;
            status += vertexB == null ? 2 : 0;

            switch(status) {
                case 1:
                    error = "The first vertex was not found!";
                    break;
                case 2:
                    error = "The second vertex was not found!";
                    break;
                case 3:
                    error = "Neither vertex was found!";
                    break;
                default:
                    error = "Please contact the administrator with this message: create_edge default case hit!";
                    break;
            }

            result.put("ERROR", error);
            intelligenceGraph.commit();
            return result;
        }

        String typeA = vertexA.getProperty("type");
        String typeB = vertexB.getProperty("type");

        result.put("typeA", typeA);
        result.put("typeB", typeB);

        Vertex schemaA = getSchemaVertexByType(intelligenceGraph, typeA);
        Vertex schemaB = getSchemaVertexByType(intelligenceGraph, typeB);
        Iterable<Vertex> neighborsSchemaA = schemaA.getVertices(Direction.OUT, "connects");
        boolean connects = false;
        System.out.println(schemaA.getId() + ":" + schemaA.getProperty("name"));
        System.out.println(schemaB.getId() + ":" + schemaB.getProperty("name"));
        for(Vertex v : neighborsSchemaA) {
            System.out.println("\t" + v.getId() + ":" + v.getProperty("name"));
            if(v.getId().equals(schemaB.getId())) {
                System.out.println("Match!");
                connects = true;
                break;
            }
        }

        if(!connects) {
            result.put("status", "ERROR");
            result.put("ERROR", "A connection between vertices of these types is not permitted!");
            intelligenceGraph.commit();
            return result;
        }

        Iterable<Edge> edgesA = vertexA.getEdges(Direction.OUT, "connectedTo");
        connects = false;
        Edge targetEdge = null;
        for(Edge e : edgesA) {
            Vertex v = e.getVertex(Direction.IN);
            if(v.getId().equals(vertexB.getId())) {
                System.out.println("Found!");
                targetEdge = e;
                connects = true;
                break;
            }
        }

        if(!connects || targetEdge == null) {
            result.put("status", "ERROR");
            result.put("ERROR", "An edge does not exist between these vertices!");
            intelligenceGraph.commit();
            return result;
        }

        intelligenceGraph.removeEdge(targetEdge);
        intelligenceGraph.commit();
        result.put("status", "SUCCESS");
        result.put("SUCCESS", "Edge: [" + idA + " -/- " + idB + "] successfully removed!");
        // result.put("vertexA", idA.toString());
        // result.put("vertexB", idB.toString());
        return result;
    }

    // searchConnectedTo
    // requires: apiKey, vertices, type, properties...
    @POST
    @Path("search_connected_to")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MappableVertex> searchConnectedTo(HashMap<String, String> properties) {
        System.out.println(properties);
        List<MappableVertex> results = new ArrayList<MappableVertex>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            results.add(new MappableVertex("Please provide an API Key!"));
            return results;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            results.add(new MappableVertex("API Key not found!"));
            return results;
        }

        if(!properties.containsKey("vertices")) {
            results.add(new MappableVertex("Please provide vertices!"));
            return results;
        }

        List<Long> vertexIds = new ArrayList();

        try {
            String[] stringVertexIds = properties.get("vertices").split(",");
            for(String stringVertexId : stringVertexIds) {
                Long vertexId = Long.parseLong(stringVertexId);
                if(vertexIds.indexOf(vertexId) != -1) {
                    results.add(new MappableVertex("Duplicate vertex IDs sent!"));
                    return results;
                }
                vertexIds.add(vertexId);
            }
        } catch (NumberFormatException e) {
            results.add(new MappableVertex("Problem parsing vertex ID!"));
            return results;
        }

        List<TitanVertex> connectedToVertices = new ArrayList();
        for(Long vertexId : vertexIds) {
            TitanVertex vertex = (TitanVertex)intelligenceGraph.getVertex(vertexId);
            if(vertex == null || !vertex.getProperty("owner").equals(username)) {
                connectedToVertices = null;
                break;
            }
            connectedToVertices.add(vertex);
        }

        if(connectedToVertices == null) {
            results.add(new MappableVertex("Some vertices were not found!"));
            intelligenceGraph.commit();
            return results;
        }        

        if(!properties.containsKey("type")) {
            intelligenceGraph.commit();
            results.add(new MappableVertex("Please provide a vertex type!"));
            return results;
        }

        String targetType = (String)properties.get("type");
        System.out.println(targetType);
        
        Vertex target = getSchemaVertexByType(intelligenceGraph, targetType);

        if(target != null) {
            for(Vertex connectedToVertex : connectedToVertices) {
                String vertexType = connectedToVertex.getProperty("type");

                if(targetType.equals("location") && vertexType.equals("person") || 
                    targetType.equals("person") && vertexType.equals("location") || 
                    targetType.equals(vertexType)) {
                    intelligenceGraph.commit();
                    results.add(new MappableVertex("Cannot get from " + vertexType + " to " + targetType + "!"));
                    return results;
                }
            }

            Map<String, String> validProperties = getValidProperties(intelligenceGraph, target);
            
            Query vertexQuery = intelligenceGraph.query();
            // vertexQuery.direction(direction);
            
            System.out.println(username);
            System.out.println(targetType);

            vertexQuery.has("owner", username);
            vertexQuery.has("type", targetType);
            
            for(String key : (Set<String>)properties.keySet()) {
                if(validProperties.containsKey(key)) {
                    String strValue = (String)properties.get(key);
                    if(!strValue.equals("")) {
                        String dataType = validProperties.get(key);
                        if(dataType.equals("geopoint"))
                            dataType = "geocircle";

                        Object value = convertProperty(dataType, strValue);
                        System.out.println(key);
                        System.out.println(value);
                        if(value == null)
                            continue;

                        if(dataType.equals("geocircle"))
                            vertexQuery.has(key, Geo.WITHIN, value);
                        else if(key.equals("notes"))
                            vertexQuery.has(key, Text.CONTAINS, value);
                        else
                            vertexQuery.has(key, value);
                    }
                }
            }

            // query based on labels for candidates
            Iterable<Vertex> vertices = vertexQuery.vertices();
            for(Vertex candidate : vertices) {
                Iterable<Vertex> candidateNeighbors = candidate.getVertices(Direction.BOTH, "connectedTo");
                int matched = 0;
                for(Vertex neighbor : candidateNeighbors) {
                    for(Vertex connectedToVertex : connectedToVertices) {
                        Long vertexId = (Long)connectedToVertex.getId();
                        if(neighbor.getId().equals(vertexId)) {
                            matched += 1;
                            break;
                        }
                    }
                }

                if(matched == connectedToVertices.size()) {
                    candidateNeighbors = candidate.getVertices(Direction.BOTH, "connectedTo"); 
                    results.add(new MappableVertex(candidate, candidateNeighbors));
                }
            }
        } else {
            results.add(new MappableVertex("Could not find a valid schema for provided type!"));
        }

        intelligenceGraph.commit();
        return results;
    }

     // getVertexNeighbors
    // requires: apiKey, vertex, direction
    @POST
    @Path("count_user_vertices")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MappableVertex> countUserVertices(HashMap<String, String> properties) {
        System.out.println(properties);
        List<MappableVertex> results = new ArrayList<MappableVertex>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            results.add(new MappableVertex("Please provide an API Key!"));
            return results;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            results.add(new MappableVertex("API Key not found!"));
            return results;
        }

        Query vertexQuery = intelligenceGraph.query();
        vertexQuery.has("owner", username);
        int count = Iterables.size(vertexQuery.vertices());
        results.add(new MappableVertex(count));

        intelligenceGraph.commit();
        return results;

    }

    // getVertexNeighbors
    // requires: apiKey, vertex, direction
    @POST
    @Path("get_neighbors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MappableVertex> getNeighbors(HashMap<String, String> properties) {
        System.out.println(properties);
        List<MappableVertex> results = new ArrayList<MappableVertex>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            results.add(new MappableVertex("Please provide an API Key!"));
            return results;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            results.add(new MappableVertex("API Key not found!"));
            return results;
        }

        if(!properties.containsKey("vertex")) {
            results.add(new MappableVertex("Please provide a vertex ID!"));
            return results;
        }

        Direction direction = Direction.BOTH;
        if(properties.containsKey("direction")) {
            String dir = properties.get("direction").toLowerCase();
            if(dir.equals("in")) {
                direction = Direction.IN;
            } else if(dir.equals("out")) {
                direction = Direction.OUT;
            }
        }

        Long vertexId;

        try {
            vertexId = Long.parseLong(properties.get("vertex"));
        } catch (NumberFormatException e) {
            results.add(new MappableVertex("Problem parsing vertex ID!"));
            return results;
        }

        Vertex vertex = intelligenceGraph.getVertex(vertexId);

        if(vertex != null && !vertex.getProperty("owner").equals(username)) {
            vertex = null;
        }

        if(vertex == null) {
            results.add(new MappableVertex("Vertex not found!"));
            intelligenceGraph.commit();
            return results;
        }

        Iterable<Vertex> vertices = vertex.getVertices(direction, "connectedTo"); 

        for(Vertex v : vertices) {
            Iterable<Vertex> vertexNeighbors = v.getVertices(Direction.BOTH, "connectedTo"); 
            results.add(new MappableVertex(v, vertexNeighbors));
        }

        results.add(new MappableVertex(vertex));

        intelligenceGraph.commit();
        return results;
    }

    // *getVertexNeighborsCount
    
}