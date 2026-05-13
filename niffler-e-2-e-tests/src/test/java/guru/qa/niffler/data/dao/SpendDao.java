package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {

    SpendEntity create(SpendEntity spend) throws SQLException;

    Optional<SpendEntity> findSpendById(UUID uuid) throws SQLException;

    List<SpendEntity> findAllByUsername(String username) throws SQLException;

    void deleteSpend(SpendEntity spend) throws SQLException;
}
