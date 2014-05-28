package com.oracle.staffmanagement.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

/**
 * 
 * Block Entity mapping the Block DB Table and relationships
 *
 */
@Entity
@Table(name="block")
@XmlRootElement
public class Block implements java.io.Serializable
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Expose
	private int block_id;
	@Expose
	private String block_code;
	@Expose
	private String block_description;
	
	public Block()
	{
		// constructor used by the JPA
	}
	
	//constructor used to instantiate a new Block Object.
	public Block(String blockCode, String blockDesc)
	{
		this.block_code = blockCode;
		this.block_description = blockDesc;
	}
    
	
	// Getters and setters
	public String getBlock_code() {
		return block_code;
	}


	public void setBlock_code(String block_code) {
		this.block_code = block_code;
	}


	public String getBlock_description() {
		return block_description;
	}


	public void setBlock_description(String block_description) {
		this.block_description = block_description;
	}


	public int getBlock_id() {
		return block_id;
	}


	public void setBlock_id(int block_id) {
		this.block_id = block_id;
	}
	        
	
}


