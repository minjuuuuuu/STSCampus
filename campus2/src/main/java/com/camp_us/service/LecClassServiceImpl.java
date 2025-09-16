package com.camp_us.service;

import java.sql.SQLException;
import java.util.List;

import com.camp_us.dao.LecClassDAO;
import com.camp_us.dto.LecClassVO;
import com.camp_us.dto.ProLecVO;

public class LecClassServiceImpl implements LecClassService {

    private LecClassDAO lecClassDAO;

    public LecClassServiceImpl(LecClassDAO lecClassDAO) {
        this.lecClassDAO = lecClassDAO;
    }

    @Override
    public List<LecClassVO> getLecClassList() throws SQLException {
        return lecClassDAO.selectLecClassList();
    }

    @Override
    public LecClassVO getLecClassById(String lec_id) throws SQLException {
        return lecClassDAO.selectLecClassById(lec_id);
    }

	@Override
	public List<ProLecVO> selectLecClassByProfessorId(String mem_id) throws SQLException {
		return lecClassDAO.selectLecClassByProfessorId(mem_id);
	}
}
