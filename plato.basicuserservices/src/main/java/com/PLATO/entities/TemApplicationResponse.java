package com.PLATO.entities;

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
@Table(name="tem_application_response",schema="plato_db")
public class TemApplicationResponse {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private int id;
	
	@Column(name="Time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;
	
	@Column(name="Response")
	private int response;
	
	@Column(name="ResponseTime")
	private double responseTime;
	
	@Column(name="Memory")
	private int memory;
	
	private long CPU;
	
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ApplicationId")  
	private TEMApplicationDetail temApplicationDetail;
}
