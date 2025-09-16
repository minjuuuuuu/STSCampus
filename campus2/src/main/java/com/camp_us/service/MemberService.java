package com.camp_us.service;

import java.sql.SQLException;
import java.util.List;

import com.camp_us.dto.MemberVO;

public interface MemberService {

	MemberVO getMemberById(String mem_id) throws SQLException;
	MemberVO getMember(String id) throws SQLException;
	void updateMemLastLogin(String mem_id) throws Exception;
	
	// 회원목록
	List<MemberVO> list() throws SQLException;

	// 회원등록
	void regist(MemberVO accout) throws SQLException;

	// 회원수정
	void modify(MemberVO account) throws SQLException;

	// 회원삭제
	void remove(String mem_id) throws SQLException;

	// 권한수정
	void modifyAuthority(String mem_id, List<String> mem_auth) throws SQLException;

	void updatePicture(String memId, String picture);
	
	boolean changePassword(String memId, String currentPw, String newPw) throws Exception;
}