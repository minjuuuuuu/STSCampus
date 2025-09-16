package com.camp_us.dto;

import java.sql.Time;
import java.util.Date;

public class MemberVO {
	private String mem_id;
    private String mem_pass;
    private String mem_name;
    private String mem_email;
    private String mem_phone;
    private String mem_add;
    private Date mem_lastlogin_date;
    private Time mem_lastlogin_time;
    private String mem_sus;
    private String mem_auth;
    private String picture;
    private Date birthDate;
    
    public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	private int enabled;
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public String getMem_id() {
		return mem_id;
	}
	public void setMem_id(String mem_id) {
		this.mem_id = mem_id;
	}
	public String getMem_pass() {
		return mem_pass;
	}
	public void setMem_pass(String mem_pass) {
		this.mem_pass = mem_pass;
	}
	public String getMem_name() {
		return mem_name;
	}
	public void setMem_name(String mem_name) {
		this.mem_name = mem_name;
	}
	public String getMem_email() {
		return mem_email;
	}
	public void setMem_email(String mem_email) {
		this.mem_email = mem_email;
	}
	public String getMem_phone() {
		return mem_phone;
	}
	public void setMem_phone(String mem_phone) {
		this.mem_phone = mem_phone;
	}
	public String getMem_add() {
		return mem_add;
	}
	public void setMem_add(String mem_add) {
		this.mem_add = mem_add;
	}
	public Date getMem_lastlogin_date() {
		return mem_lastlogin_date;
	}
	public void setMem_lastlogin_date(Date mem_lastlogin_date) {
		this.mem_lastlogin_date = mem_lastlogin_date;
	}
	public Time getMem_lastlogin_time() {
		return mem_lastlogin_time;
	}
	public void setMem_lastlogin_time(Time mem_lastlogin_time) {
		this.mem_lastlogin_time = mem_lastlogin_time;
	}
	public String getMem_sus() {
		return mem_sus;
	}
	public void setMem_sus(String mem_sus) {
		this.mem_sus = mem_sus;
	}
	public String getMem_auth() {
		return mem_auth;
	}
	public void setMem_auth(String mem_auth) {
		this.mem_auth = mem_auth;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	
}