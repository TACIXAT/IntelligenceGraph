
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

import java.lang.Iterable;
import java.lang.NumberFormatException;
import com.google.common.collect.Iterables;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Query;
import com.tinkerpop.blueprints.Direction;
import com.thinkaurelius.titan.core.TitanGraph;

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
    public MappableVertex createVertex(HashMap properties) {
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
            Vertex newVertex = intelligenceGraph.addVertex(null);
            newVertex.setProperty("owner", username);
            newVertex.setProperty("type", vertexType);

            Map<String, String> validProperties = getValidProperties(intelligenceGraph, target);
            
            for(String key : (Set<String>)properties.keySet()) {
                if(validProperties.containsKey(key)) {
                    newVertex.setProperty(key, (String)properties.get(key));
                }
            }

            result = new MappableVertex(newVertex);
        } else {
            result = new MappableVertex("Could not find a valid schema for provided type!");
        }

        intelligenceGraph.commit();
        return result;
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
                System.out.println("Name Match!");
                target = v;
                break;
            }
        }
        intelligenceGraph.commit();
        return target;
    }

    // *deleteVertex
    // requires: apiKey, vertex
    @POST
    @Path("delete_vertex")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> deleteVertex(Map<String, String> properties) {
        Map<String, String> result = new HashMap<String, String>();
        TitanGraph intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");

        if(!properties.containsKey("apiKey")) {
            result.put("status", "error");
            result.put("error", "Please provide an API Key!");
            return result;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result.put("status", "error");
            result.put("error", "API Key not found!");
            return result;
        }

        if(!properties.containsKey("vertex")) {
            result.put("status", "error");
            result.put("error", "You must provide a vertex to delete!");
            return result;
        }

        Long vertexId;

        try {
            vertexId = Long.parseLong(properties.get("vertex"));
        } catch (NumberFormatException e) {
            result.put("status", "error");
            result.put("error", "Problem parsing vertex IDs!");
            return result;
        }

        result.put("vertex", vertexId.toString());
        Vertex vertex = intelligenceGraph.getVertex(vertexId);

        if(vertex != null && !vertex.getProperty("owner").equals(username)) {
            vertex = null;
        }

        if(vertex == null) {
            result.put("status", "error");
            result.put("error", "Vertex not found!");
            intelligenceGraph.commit();
            return result;
        }

        Iterable<Edge> edges = vertex.getEdges(Direction.BOTH);
        for(Edge e : edges) {
            intelligenceGraph.removeEdge(e);
        }
        intelligenceGraph.removeVertex(vertex);
        intelligenceGraph.commit();

        result.put("status", "success");
        result.put("success", "Vertex " + vertexId + " successfully deleted!");
        return result;
    }


    // *updateVertex
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
                    vertex.setProperty(key, (String)properties.get(key));
                }
            }

            result = new MappableVertex(vertex);
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
            result.put("status", "error");
            result.put("error", "Please provide an API Key!");
            return result;
        }

        String apiKey = (String)properties.get("apiKey");
        String username = getUsername(intelligenceGraph, apiKey);

        if(username == null) {
            result.put("status", "error");
            result.put("error", "API Key not found!");
            return result;
        }

        if(!properties.containsKey("vertexA") || !properties.containsKey("vertexB")) {
            result.put("status", "error");
            result.put("error", "You must provide 2 vertices to link!");
            return result;
        }

        Long idA, idB;

        try {
            idA = Long.parseLong(properties.get("vertexA"));
            idB = Long.parseLong(properties.get("vertexB"));
        } catch (NumberFormatException e) {
            result.put("status", "error");
            result.put("error", "Problem parsing vertex IDs!");
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
            result.put("status", "error");
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

            result.put("error", error);
            intelligenceGraph.commit();
            return result;
        }

        String typeA = vertexA.getProperty("type");
        String typeB = vertexB.getProperty("type");

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
            result.put("status", "error");
            result.put("error", "A connection between vertices of these types is not permitted!");
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
            result.put("status", "error");
            result.put("error", "Edge already exists between these vertices!");
            intelligenceGraph.commit();
            return result;
        }

        intelligenceGraph.addEdge(null, vertexA, vertexB, "connectedTo");
        intelligenceGraph.commit();
        result.put("status", "success");
        result.put("success", "Edge: [" + idA + " -- " + idB + "] successfully created!");
        return result;
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
            Query vertexQuery = intelligenceGraph.query();
            vertexQuery.has("owner", username);
            vertexQuery.has("type", vertexType);

            Map<String, String> validProperties = getValidProperties(intelligenceGraph, target);
            
            for(String key : (Set<String>)properties.keySet()) {
                if(validProperties.containsKey(key)) {
                    String value = (String)properties.get(key);
                    if(!value.equals("")) {
                        vertexQuery.has(key, value);
                    }
                }
            }

            Iterable<Vertex> vertices = vertexQuery.vertices();
            for(Vertex v : vertices) {
                results.add(new MappableVertex(v));
            }
        } else {
            results.add(new MappableVertex("Could not find a valid schema for provided type!"));
        }

        intelligenceGraph.commit();
        return results;
    }

    // getVertexNeighbors
    // requires: apiKey, vertex
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

        Iterable<Vertex> vertices = vertex.getVertices(Direction.BOTH, "connectedTo"); 

        for(Vertex v : vertices) {
            results.add(new MappableVertex(v));
        }

        intelligenceGraph.commit();
        return results;
    }

    // *getVertexNeighborsCount
    
}