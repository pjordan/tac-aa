
package edu.umich.eecs.tac.util.config;

/**
 * @author Patrick Jordan
 */
public class ConfigProxyUtils {
    private ConfigProxyUtils() {
    }

    public static <T> T createObjectFromProperty(ConfigProxy proxy, String name, String defaultValue) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (T) Class.forName(proxy.getProperty(name,defaultValue)).newInstance();
    }
}
