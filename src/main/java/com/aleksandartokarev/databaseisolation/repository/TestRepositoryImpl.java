package com.aleksandartokarev.databaseisolation.repository;

import com.aleksandartokarev.databaseisolation.domain.AccountModel;
import com.aleksandartokarev.databaseisolation.domain.TransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TestRepositoryImpl implements TestRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void addBalance(Integer iban, Double amount) {
        String sqlQuery = "UPDATE account SET balance = balance + :amount WHERE iban = :iban";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("iban", iban);
        parameters.addValue("amount", amount);
        namedParameterJdbcTemplate.update(sqlQuery, parameters);
    }

    @Override
    public Double getBalance(Integer iban) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("iban", iban);
        final List<AccountModel> result = namedParameterJdbcTemplate.query("SELECT * FROM account WHERE iban = :iban", parameters, new AccountMapper());
        if (result.size() != 0) {
            return result.get(0).getBalance();
        }
        return 0.0;
    }


    public class AccountMapper implements RowMapper<AccountModel> {
        @Override
        public AccountModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            AccountModel accountModel = new AccountModel();
            accountModel.setIban(rs.getInt("iban"));
            accountModel.setBalance(rs.getDouble("balance"));
            return accountModel;
        }
    }
}
