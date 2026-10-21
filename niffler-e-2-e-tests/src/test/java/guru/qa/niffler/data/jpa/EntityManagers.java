package guru.qa.niffler.data.jpa;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {
    private EntityManagers() {
    }

    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    public static EntityManager em(String jdbcUrl, String puName) {
        return new ThreadSafeEntityManager(
                emfs.computeIfAbsent(puName,
                        key -> {
                            DataSources.dataSource(jdbcUrl);
                            return Persistence.createEntityManagerFactory(
                                    puName,
                                    Map.of(
                                            "jakarta.persistence.jdbc.url", jdbcUrl,
                                            "jakarta.persistence.jdbc.user", Config.getInstance().dbUsername(),
                                            "jakarta.persistence.jdbc.password", Config.getInstance().dbPassword()
                                    )
                            );
                        }
                ).createEntityManager());
    }
}