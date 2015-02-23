package br.com.fiap.entities;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="tabela_dados")
public class TabelaDado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_tabela_dados")
	private int idTabelaDados;

	private String descricao;

	public TabelaDado() {
	}

	
	public TabelaDado(int idTabelaDados, String descricao) {
		this.idTabelaDados = idTabelaDados;
		this.descricao = descricao;
	}



	public int getIdTabelaDados() {
		return this.idTabelaDados;
	}

	public void setIdTabelaDados(int idTabelaDados) {
		this.idTabelaDados = idTabelaDados;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}