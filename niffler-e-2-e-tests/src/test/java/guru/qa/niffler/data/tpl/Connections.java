package guru.qa.niffler.data.tpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {

    public Connections() {
    }

    private static final Map<String, JdbcConnectionHolder> holders = new ConcurrentHashMap<>();
    private static final Map<String, JdbcConnectionHolder> xaHolders = new ConcurrentHashMap<>();

    public static JdbcConnectionHolder holder(String jdbcUrl) {
        return holders.computeIfAbsent(
                jdbcUrl,
                key -> new JdbcConnectionHolder(DataSources.dataSource(jdbcUrl))
        );
    }

    public static JdbcConnectionHolder xaHolder(String jdbcUrl) {
        return xaHolders.computeIfAbsent(
                jdbcUrl,
                key -> new JdbcConnectionHolder(DataSources.xaDataSource(jdbcUrl))
        );
    }

    public static JdbcConnectionHolders holders(String... jdbcUrl) {
        List<JdbcConnectionHolder> result = new ArrayList<>();
        for (String url : jdbcUrl) {
            result.add(holder(url));
        }
        return new JdbcConnectionHolders(result);
    }

    public static JdbcConnectionHolders xaHolders(String... jdbcUrl) {
        List<JdbcConnectionHolder> result = new ArrayList<>();
        for (String url : jdbcUrl) {
            result.add(xaHolder(url));
        }
        return new JdbcConnectionHolders(result);
    }

    public static void closeAllConnections() {
        holders.values().forEach(JdbcConnectionHolder::closeAllConnections);
        xaHolders.values().forEach(JdbcConnectionHolder::closeAllConnections);
    }
}