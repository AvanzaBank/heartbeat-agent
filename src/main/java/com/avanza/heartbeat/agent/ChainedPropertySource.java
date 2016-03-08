package com.avanza.heartbeat.agent;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ChainedPropertySource implements PropertySource {

	private List<PropertySource> sources;

	/**
	 * Chains several property sources. Latter sources will have precedence (i.e. latter sources will overwrite
	 * properties in earlier sources)
	 */
	public ChainedPropertySource(PropertySource... sources) {
		this.sources = Arrays.asList(sources);
	}

	@Override
	public Properties getProperties() {
		Properties result = new Properties();
		for (PropertySource propertySource : sources) {
			result.putAll(propertySource.getProperties());
		}
		return result;
	}

}
