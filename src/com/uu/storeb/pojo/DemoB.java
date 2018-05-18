package com.uu.storeb.pojo;

import java.util.Date;

public class DemoB {
	private String id;

	private String name;

	private Integer age;

	private Date createtime;

	private String info;

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id=id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name=name;
	}

	public Integer getAge(){
		return age;
	}

	public void setAge(Integer age){
		this.age=age;
	}

	public Date getCreatetime(){
		return createtime;
	}

	public void setCreatetime(Date createtime){
		this.createtime=createtime;
	}

	public String getInfo(){
		return info;
	}

	public void setInfo(String info){
		this.info=info;
	}

}