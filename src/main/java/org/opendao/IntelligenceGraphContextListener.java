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
		
		// create or load graph
		Configuration conf = new BaseConfiguration();
		// conf.setProperty("storage.machine-id", "aws-graph");
		// conf.setProperty("storage.machine-id-appendix", "4747");

		conf.setProperty("storage.backend", "cassandra");
		conf.setProperty("storage.hostname", "172.31.37.42");

		conf.setProperty("storage.index.search.backend", "elasticsearch");
		conf.setProperty("storage.index.search.client-only", "elasticsearch");
		conf.setProperty("storage.index.search.hostname", "172.31.37.42");

		conf.setProperty("cache.db-cache", "true");
		conf.setProperty("cache.db-cache-clean-wait", "50");
		conf.setProperty("cache.db-cache-time", "10000");
		conf.setProperty("cache.db-cache-size", "0.25");

		conf.setProperty("storage.index.search.backend", "elasticsearch");
		conf.setProperty("storage.index.search.hostname", "172.31.37.42");
		conf.setProperty("storage.index.search.cluster-name", "socialite");
		conf.setProperty("storage.index.search.index-name", "titan");
		conf.setProperty("storage.index.search.client-only", "true");
		conf.setProperty("storage.index.search.sniff", "false");
		conf.setProperty("storage.index.search.local-mode", "false");

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