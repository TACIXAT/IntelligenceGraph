package org.opendao.IntelligenceGraph;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
// import com.tinkerpop.blueprints.Edge;
// import com.tinkerpop.blueprints.Vertex;

import com.google.common.collect.Iterables;

public class IntelligenceGraphContextListener implements ServletContextListener{
	TitanGraph intelligenceGraph;
	ServletContext context;

    //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		System.out.println("ServletContextListener started");	
		String basePath = "/tmp/intelligence_graph/";
		
		// create or load graph
		Configuration conf = new BaseConfiguration();
		conf.setProperty("storage.directory", basePath);
		conf.setProperty("storage.backend", "berkeleyje");
		conf.setProperty("storage.index.search.backend", "lucene");
		conf.setProperty("storage.index.search.directory", "/tmp/searchindex");
		intelligenceGraph = TitanFactory.open(conf);

		if(Iterables.size(intelligenceGraph.getVertices("type", "user")) < 1) {
			LifeTemplate.initializeGraph(intelligenceGraph);
		}

		LifeTemplate.updateGraph(intelligenceGraph);

		intelligenceGraph.commit();
		context = contextEvent.getServletContext();
		context.setAttribute("INTELLIGENCE_GRAPH", intelligenceGraph);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		System.out.println("ServletContextListener destroyed");
		context = contextEvent.getServletContext();

		intelligenceGraph = (TitanGraph)context.getAttribute("INTELLIGENCE_GRAPH");
		if(intelligenceGraph != null) {
			System.out.println("Shutdown graph!");
			intelligenceGraph.shutdown();
		}

		context.removeAttribute("INTELLIGENCE_GRAPH");
	}
}