package guru.qa.niffler.data.tpl;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.function.Supplier;

public class JdbcTransactionTemplate {

    private final TransactionTemplate transactionTemplate;

    public JdbcTransactionTemplate(String jdbcUrl) {
        DataSource ds = DataSources.dataSource(jdbcUrl);
        this.transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));
    }

    public JdbcTransactionTemplate withIsolationLevel(int isolationLevel) {
        this.transactionTemplate.setIsolationLevel(isolationLevel);
        return this;
    }

    public <T> T execute(Supplier<T> action, int isolationLevel) {
        Integer prevIsolation = transactionTemplate.getIsolationLevel();
        transactionTemplate.setIsolationLevel(isolationLevel);
        try {
            return transactionTemplate.execute(status -> action.get());
        } finally {
            transactionTemplate.setIsolationLevel(prevIsolation);
        }
    }

    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }
}