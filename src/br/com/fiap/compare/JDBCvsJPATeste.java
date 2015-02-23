package br.com.fiap.compare;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import br.com.fiap.entities.TabelaDado;
import br.com.fiap.message.ShowMessage;

/**
 * 
 * @author Marcel Levinspuhl Junior
 * 
 * Classe para teste de performance JDBC e JPA
 *
 */
public class JDBCvsJPATeste {
	protected static EntityManagerFactory entityManagerFactory;
	protected static EntityManager em;
	protected static EntityTransaction transaction;
	protected static final String URL = "jdbc:mysql://localhost:3306/dados";
	protected static Connection conn;
	protected static Statement stmt;
	protected static final int qtdItera = 100000;
	

	static {
		entityManagerFactory = Persistence
				.createEntityManagerFactory("JPAvsJDBC");
		em = entityManagerFactory.createEntityManager();
		transaction = em.getTransaction();
		try {
			conn = DriverManager.getConnection(URL, "med", "med");
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	protected static ShowMessage sm = new ShowMessage("Teste Comparativo de Performance JDBC / JPA");

	public static void main(String[] args) throws Exception {
		
		
		exibeMsg("Limpando dados da tabela_dados...");
		stmt.executeUpdate("TRUNCATE dados.tabela_dados");
		exibeMsg("OK\n");
		
		String msg = "Início do teste JDBC/STORE PROCEDURE "+Calendar.getInstance().getTime()+"\n";
		exibeMsg(msg);
		
		sm.exec();
		
		long tempoInicial = System.currentTimeMillis();
		
		String bulkInsert = "{call dados.BULK_INSERT(?)}";
		
		CallableStatement callableStatement = conn.prepareCall(bulkInsert);
		
		callableStatement.setInt(1, qtdItera);
		
		callableStatement.executeUpdate();
		
		// Calcula tempo
		long tempoFinal = System.currentTimeMillis();
		long tempoTotal = (tempoFinal - tempoInicial) / 1000;
		msg = "Tempo em segundos para inserir " + qtdItera
				+ " linhas \nna tabela_dados usando JDBC/STORE PROCEDURE :" + tempoTotal + "s\n";
		exibeMsg(msg);
		msg = "Fim do teste JDBC/STORE PROCEDURE \n\n";
		exibeMsg(msg);
		double tempoJDBCStore=tempoTotal;
		
		DecimalFormat dcm = new DecimalFormat("#.0");
		
		double tempoJDBC=0;
		double tempoJPA=0;
		
		exibeMsg("Limpando dados da tabela_dados...");
		stmt.executeUpdate("TRUNCATE dados.tabela_dados");
		exibeMsg("OK\n");
		
		msg = "Início do teste JDBC PreparedStatement "+Calendar.getInstance().getTime()+"\n";
		exibeMsg(msg);
		

		


		// query para inserir dados
		String query = "insert into tabela_dados values (?,?)";
		PreparedStatement pstmt = conn.prepareStatement(query);

		tempoInicial = System.currentTimeMillis();
		for (int i = 0; i < qtdItera; i++) {

			pstmt.setString(1, Integer.toString(i));
			pstmt.setString(2, "dado" + i);
			pstmt.addBatch();

		}

		pstmt.executeBatch();

		// Calcula tempo
		tempoFinal = System.currentTimeMillis();
		tempoTotal = (tempoFinal - tempoInicial) / 1000;
		msg = "Tempo em segundos para inserir " + qtdItera
				+ " linhas \nna tabela_dados usando JDBC :" + tempoTotal + "s\n";
		tempoJDBC=tempoTotal;
		exibeMsg(msg);
		msg = "Fim do teste JDBC PreparedStatement\n\n";
		exibeMsg(msg);


		// --------------------------- JPA Teste ---------------------------

		exibeMsg("Limpando dados da tabela_dados...");
		stmt.executeUpdate("TRUNCATE dados.tabela_dados");
		exibeMsg("OK\n");

		// fecha conexão
		pstmt.close();
		stmt.close();
		conn.close();
		
		msg = "Início do teste JPA "+Calendar.getInstance().getTime()+"\n";

		exibeMsg(msg);
		
		tempoInicial = System.currentTimeMillis();
		transaction.begin();
		for (int i = 0; i < qtdItera; i++) {

			em.persist(new TabelaDado(i, "dado" + i));

		}
		transaction.commit();
		// Calcula tempo JPA
		tempoFinal = System.currentTimeMillis();
		tempoTotal = (tempoFinal - tempoInicial) / 1000;

		msg = "Tempo em segundos para inserir " + qtdItera
				+ " linhas \nna tabela_dados usando JPA :" + tempoTotal + "s\n";
		
		tempoJPA=tempoTotal;

		exibeMsg(msg);
		
		msg = "Fim do teste JPA \n\n";
		exibeMsg(msg);
		
		
		exibeMsg("========= RESULTADO =========\n");
		
		
		if(tempoJDBC<tempoJPA){
			msg = "JDBC PreparedStatement foi mais rápido que o JPA => "+(dcm.format(tempoJPA/tempoJDBC))+" vezes\n";
		}else{
			msg = "JPA foi mais rápido que o JDBC PreparedStatement => "+(dcm.format(tempoJDBC/tempoJPA))+" vezes\n";
		}
		exibeMsg(msg);
		
		msg = "\nJDBC/STORE PROCEDURE é incrivelmente o mais rápido de todos, sendo:\n- Mais rápido que o JPA => "+(dcm.format(tempoJPA/tempoJDBCStore))+" vezes\n"
				+ "- Mais rápido que o JDBC PreparedStatement => "+(dcm.format(tempoJDBC/tempoJDBCStore))+" vezes\n";
		exibeMsg(msg);
	}
	
	public static void exibeMsg(String msg) throws Exception{
		if(sm==null){
			throw new Exception("Thread ShowMessage error");
		}
		
		msg=msg==null?"":msg;
		System.out.println(msg);
		sm.setMessage(msg);
	}
}
