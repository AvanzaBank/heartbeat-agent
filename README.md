# heartbeat-agent

Java agent for sending periodic heartbeats to an HTTP endpoint. Useful for keeping track of which applications are alive or keeping an inventory of all your applications.

The agent has no dependencies. Hence, there is no risk for version conflicts with any third party libraries you may be running.

## Installation

* Download the [latest release](https://github.com/AvanzaBank/heartbeat-agent/releases/latest)
* Run your Java application with the JVM argument `-javaagent:heartbeat-agent-<version>.jar[=<optional URL to properties file>]`

## Configuration

Configuration properties can be specified either as system properties or in an external properties file. If supplied, properties in the properties file take precedence. Available properties:

* `heartbeat.agent.application.name` - name of the application that is sending the heartbeats
* `heartbeat.agent.url` - URL to send heartbeats to
* `heartbeat.agent.application.version` - version of the application that is sending the heartbeats (optional)

## Data sent with the heartbeats

Heartbeats will be sent using a GET request to the URL specified in `heartbeat.agent.url`. The following request properties are sent as well:

* `name` - the value of the property `heartbeat.agent.application.name`
* `pid` - the PID of the process (0 if it could not be determined)
* `uid` - a unique UUID created on initialization - will change on restarts
* `revision` - the value of `heartbeat.agent.application.version` or "UNKNOWN" if not specified
* `jmx` - the JMX port the process is listening on or 0 if not listening for JMX connections

## Requirements

Requires Java 8.