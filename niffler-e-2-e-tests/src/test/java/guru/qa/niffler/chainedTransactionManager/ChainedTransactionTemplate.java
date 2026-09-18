package guru.qa.niffler.chainedTransactionManager;

import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.function.Supplier;

public class ChainedTransactionTemplate {

    private final TransactionTemplate transactionTemplate;

    public ChainedTransactionTemplate(String... jdbcUrls) {
        PlatformTransactionManager[] txManagers = new PlatformTransactionManager[jdbcUrls.length];
        for (int i = 0; i < jdbcUrls.length; i++) {
            DataSource ds = DataSources.dataSource(jdbcUrls[i]);
            txManagers[i] = new DataSourceTransactionManager(ds);
        }
        ChainedTransactionManager chainedTxManager = new ChainedTransactionManager(txManagers);
        this.transactionTemplate = new TransactionTemplate(chainedTxManager);
    }

    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }
}
