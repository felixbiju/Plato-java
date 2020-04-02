package com.plato.tem.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="tem_database_response",schema="plato_db")
public class TemDatabaseResponse {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="database_response_id")
	private int databaseresponseId;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	private int response;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "database_id")  
	private TEMDatabaseDetail temDatabaseDetail;

	public int getDatabaseresponseId() {
		return databaseresponseId;
	}

	public void setDatabaseresponseId(int databaseresponseId) {
		this.databaseresponseId = databaseresponseId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getResponse() {
		return response;
	}

	public void setResponse(int response) {
		this.response = response;
	}

	public TEMDatabaseDetail getTemDatabaseDetail() {
		return temDatabaseDetail;
	}

	public void setTemDatabaseDetail(TEMDatabaseDetail temDatabaseDetail) {
		this.temDatabaseDetail = temDatabaseDetail;
	}
	
	
}
