package com.camp_us.service;

import java.sql.SQLException;
import java.util.List;

import com.camp_us.dto.StuLecVO;

public interface StuLecService {
    List<StuLecVO> selectLectureListByStudentId(String stu_id) throws SQLException;

}