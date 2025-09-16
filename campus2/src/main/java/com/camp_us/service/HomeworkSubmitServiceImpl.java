package com.camp_us.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camp_us.dao.HomeworkDAO;
import com.camp_us.dao.HomeworkSubmitDAO;
import com.camp_us.dto.HomeworkSubmitVO;
import com.camp_us.dto.HomeworkVO;

public class HomeworkSubmitServiceImpl implements HomeworkSubmitService {
	
	private HomeworkDAO homeworkDAO;

    private HomeworkSubmitDAO homeworksubmitDAO;
    @Autowired
    public HomeworkSubmitServiceImpl(HomeworkSubmitDAO homeworksubmitDAO, HomeworkDAO homeworkDAO) {
        this.homeworksubmitDAO = homeworksubmitDAO;
        this.homeworkDAO = homeworkDAO;
    }

    @Override
    public void regist(HomeworkSubmitVO submitVO) throws SQLException {
        homeworksubmitDAO.insertHomeworkSubmit(submitVO);
    }

    @Override
    public void modify(HomeworkSubmitVO submitVO) throws SQLException {
        homeworksubmitDAO.updateHomeworkSubmit(submitVO);
    }
    @Override
    public HomeworkSubmitVO getSubmitByHwNoAndStdId(int hwNo, String stuId) throws SQLException {
        return homeworksubmitDAO.selectSubmitByStuIdAndHwNo(hwNo, stuId);
    }

    @Override
    public void remove(String hwNo) throws SQLException {
        homeworksubmitDAO.deleteHomeworkSubmit(hwNo);
    }

    @Override
    public HomeworkSubmitVO getSubmitByHsno(String hwsubHsno) throws SQLException {
        return homeworksubmitDAO.selectHomeworkSubmitByPk(hwsubHsno);
    }

    @Override
    public HomeworkVO getHomeworkDetail(int hwNo) throws SQLException {
        return homeworkDAO.selectHomeworkByNo(hwNo); // DAO 메서드 호출
    }
    
    @Override
    public void updateSubmit(HomeworkSubmitVO submitVO) throws SQLException {
        homeworksubmitDAO.updateHomeworkSubmit(submitVO);
    }
    @Override
    public HomeworkSubmitVO getSubmitById(String submitId) {
        return homeworksubmitDAO.selectSubmitById(submitId);
    }

    @Override
    public void updateFeedback(HomeworkSubmitVO submit) {
        homeworksubmitDAO.updateFeedback(submit);
    }
}
