package com.mongo.entities;

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

@Entity
@Table(name="tem_server_response",schema="plato_db")
public class TemServerResponse {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="Id")
	private int id;
	
	@Column(name="Time")
	private Date time;
	
	@Column(name="Response")
	private int response;
	
	
	
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ServerID")  
	private TEMServerDetail temServerDetail;




	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public Date getTime() {
		return time;
	}




	public void setTime(Date time) {
		this.time = time;
	}




	public int getResponse() {
		return response;
	}




	public void setResponse(int response) {
		this.response = response;
	}




	public TEMServerDetail getTemServerDetail() {
		return temServerDetail;
	}




	public void setTemServerDetail(TEMServerDetail temServerDetail) {
		this.temServerDetail = temServerDetail;
	}
	
}
