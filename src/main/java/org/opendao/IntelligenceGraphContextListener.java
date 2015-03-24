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
		String basePath = "/graph/";
		
		// create or load graph
		Configuration conf = new BaseConfiguration();
		conf.setProperty("storage.machine-id", "aws-graph");
		conf.setProperty("storage.directory", basePath);
		conf.setProperty("storage.backend", "cassandra");
		conf.setProperty("storage.hostname", "127.0.0.1");
		conf.setProperty("storage.index.search.backend", "elasticsearch");
		conf.setProperty("storage.index.search.client-only", "elasticsearch");
		conf.setProperty("storage.index.search.hostname", "127.0.0.1");
		intelligenceGraph = TitanFactory.open(conf);

		if(Iterables.size(intelligenceGraph.getVertices("type", "schema")) < 1) {
			intelligenceGraph.commit();
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