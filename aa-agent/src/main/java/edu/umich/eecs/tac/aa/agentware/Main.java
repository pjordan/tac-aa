package edu.umich.eecs.tac.aa.agentware;

import java.io.IOException;

import se.sics.isl.util.ArgumentManager;

public class Main {

    private static String DEFAULT_HOST = "localhost";
    private static int DEFAULT_PORT = 6502;

    private final static String DEFAULT_CONFIG = "config/aw.conf";

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        ArgumentManager config = new ArgumentManager("aa-aw.jar", args);
        config.addOption("config", "configfile", "set the config file to use");
        config.addOption("serverHost", "host", "set the TAC server host");
        config.addOption("serverPort", "port", "set the TAC server port");
        config.addOption("agentName", "name", "set the agent name");
        config.addOption("agentPassword", "password", "set the agent password");
        config.addOption("agentImpl", "class", "set the agent implementation");
        config.addOption("autojoin", "numberOfTimes", "set the number of times to automatically create and join simulations");
        config.addOption("log.consoleLevel", "level", "set the console log level");
        config.addOption("log.fileLevel", "level", "set the file log level");
        config.addHelp("h", "show this help message");
        config.addHelp("help");
        config.validateArguments();

        String configFile = config.getArgument("config", DEFAULT_CONFIG);
        try {
            config.loadConfiguration(configFile);
            config.removeArgument("config");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            config.usage(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        String agentImpl = config.getProperty("agentImpl");
        if (agentImpl == null || agentImpl.length() == 0) {
            System.err.println("No agent implementation specified!");
            config.usage(1);
        }

        // Make sure the class for the agent can be found before
        // connecting to the server (and joining simulations).
        try {
            Class c = Class.forName(agentImpl);
        } catch (Exception e) {
            System.err.println("Could not find the agent implementation '"
                    + agentImpl + '\'');
            e.printStackTrace();
            System.exit(1);
        }

        String host = config.getProperty("serverHost", DEFAULT_HOST);
        int port = config.getPropertyAsInt("serverPort", DEFAULT_PORT);
        String name = config.getProperty("agentName", null);
        String password = config.getProperty("agentPassword", null);
        if (name == null || name.length() == 0
                || password == null || password.length() == 0) {
            System.err.println("=============================================");
            System.err.println("You must specify a registered agent name");
            System.err.println("and password in the configuration file");
            System.err.println("or as arguments when starting the AgentWare");
            System.err.println("");
            System.err.println("You can register your agent at:");
            System.err.println("http://" + host + ":8080/");
            System.err.println("=============================================");
            System.exit(1);
        }

        // No more need for argument handling.  Lets free the memory
        config.finishArguments();

        SimClient client = new SimClient(config, host, port,
                name, password, agentImpl);
    }

} // Main
