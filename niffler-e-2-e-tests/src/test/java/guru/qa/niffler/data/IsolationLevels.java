package guru.qa.niffler.data;

import java.sql.Connection;

public final class IsolationLevels {
    private IsolationLevels() {}

    public static final int READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;
    public static final int READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;
    public static final int REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;
    public static final int SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;
}
