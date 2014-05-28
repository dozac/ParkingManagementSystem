package com.oracle.staffmanagement.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.annotations.Expose;

/**
 * 
 * 
 * Parking Spaces Entity mapping the parking_space DB Table and relationships
 *
 */

@Entity
@XmlRootElement
@Table(name="parking_space")
public class ParkingSpace implements java.io.Serializable
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Expose
	private int pspace_id;
	
	@Expose
	private int space_no;
	
	@Expose
	private int block_block_id;
	
	@Expose
	private Boolean is_visitor;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="block_block_id", insertable=false, updatable=false)
	@Expose
	private Block block;
	
	@XmlTransient
	@OneToMany(cascade=CascadeType.PERSIST)
	@JoinColumn(name="parking_space_pspace_id")
	private List<ParkingAllocation> parkingallocation;
	
	public ParkingSpace()
	{
		// constructor used by the JPA
	}
 
	
	// Getters and setters
	public int getPspace_id() {
		return pspace_id;
	}
    
	public  ParkingAllocation getPermanentParkingAllocation() {
		for(ParkingAllocation pa : parkingallocation) {
			if( pa.getIs_permanent()  == true )
			   return pa;

			}
		      return null;
	}

	public void setPspace_id(int pspace_id) {
		this.pspace_id = pspace_id;
	}


	public int getBlock_block_id() {
		return block_block_id;
	}


	public void setBlock_block_id(int block_block_id) {
		this.block_block_id = block_block_id;
	}


	public Block getBlock() {
		return block;
	}


	public void setBlock(Block block) {
		this.block = block;
	}


	public ParkingSpace(int spaceNo)
	{
		this.space_no = spaceNo;
	}
    
	public int getSpace_no() {
		return space_no;
	}


	public void setSpace_no(int space_no) {
		this.space_no = space_no;
	}


	public Boolean getIs_visitor() {
		return is_visitor;
	}


	public void setIs_visitor(Boolean is_visitor) {
		this.is_visitor = is_visitor;
	}

	
	
}


