package br.com.caelum.contas.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.caelum.contas.modelo.Conta;
import br.com.caelum.contas.modelo.TipoDaConta;

@Repository
public class ContaDAO {

    @Autowired
    private DataSource ds;

    private Connection connection;

    public void novaConexao() throws SQLException{
        if(this.connection == null || this.connection.isClosed()){
            this.connection = this.ds.getConnection();
        }
    }

    public void adiciona(Conta conta) {
        String sql = "insert into contas (descricao, paga, valor, tipo) values (?,?,?,?)";
        PreparedStatement stmt;
        try {
            novaConexao();
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, conta.getDescricao());
            stmt.setBoolean(2, conta.isPaga());
            stmt.setDouble(3, conta.getValor());
            stmt.setString(4, conta.getTipo().name());
            stmt.execute();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void remove(Conta conta) {

        if (conta.getId() == null) {
            throw new IllegalStateException("Id da conta naoo deve ser nula.");
        }

        String sql = "delete from contas where id = ?";
        PreparedStatement stmt;
        try {
            novaConexao();
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, conta.getId());
            stmt.execute();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void altera(Conta conta) {
        String sql = "update contas set descricao = ?, paga = ?, dataPagamento = ?, tipo = ?, valor = ? where id = ?";
        PreparedStatement stmt;
        try {
            novaConexao();
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, conta.getDescricao());
            stmt.setBoolean(2, conta.isPaga());
            stmt.setDate(3, conta.getDataPagamento() != null ? new Date(conta
                    .getDataPagamento().getTimeInMillis()) : null);
            stmt.setString(4, conta.getTipo().name());
            stmt.setDouble(5, conta.getValor());
            stmt.setLong(6, conta.getId());
            stmt.execute();

            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Conta> lista() {
        try {
            novaConexao();
            List<Conta> contas = new ArrayList<Conta>();
            PreparedStatement stmt = this.connection
                    .prepareStatement("select * from contas");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // adiciona a conta na lista
                contas.add(populaConta(rs));
            }

            rs.close();
            stmt.close();
            connection.close();

            return contas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Conta buscaPorId(Long id) {
        Conta conta = null;
        if (id == null) {
            throw new IllegalStateException("Id da conta nao deve ser nula.");
        }

        try {
            novaConexao();
            PreparedStatement stmt = this.connection
                    .prepareStatement("select * from contas where id = ?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                conta = populaConta(rs);
            }

            rs.close();
            stmt.close();


            connection.close();
            return conta;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void paga(Long id) {

        if (id == null) {
            throw new IllegalStateException("Id da conta nao deve ser nula.");
        }

        String sql = "update contas set paga = ?, dataPagamento = ? where id = ?";
        PreparedStatement stmt;
        try {
            novaConexao();
            stmt = connection.prepareStatement(sql);
            stmt.setBoolean(1, true);
            stmt.setDate(2, new Date(Calendar.getInstance().getTimeInMillis()));
            stmt.setLong(3, id);
            stmt.execute();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Conta populaConta(ResultSet rs) throws SQLException {
        Conta conta = new Conta();

        conta.setId(rs.getLong("id"));
        conta.setDescricao(rs.getString("descricao"));
        conta.setPaga(rs.getBoolean("paga"));
        conta.setValor(rs.getDouble("valor"));

        Date data = rs.getDate("dataPagamento");
        if (data != null) {
            Calendar dataPagamento = Calendar.getInstance();
            dataPagamento.setTime(data);
            conta.setDataPagamento(dataPagamento);
        }

        conta.setTipo(Enum.valueOf(TipoDaConta.class, rs.getString("tipo")));

        return conta;
    }
}