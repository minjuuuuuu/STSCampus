package com.camp_us.controller;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.camp_us.dto.AcademicVO;
import com.camp_us.dto.MemberVO;
import com.camp_us.service.AcademicService;
import com.camp_us.service.DashboardService;
import com.camp_us.service.LecClassService;
import com.camp_us.service.MemberService;
import com.camp_us.service.NoticeService;
import com.camp_us.service.StuLecService;

@RestController
@RequestMapping("/api")
public class AdminLTEController2 {

	@Autowired
	private StuLecService stuLecService;
	@Autowired
	private LecClassService lecClassService;
	@Autowired
	private AcademicService academicService;
	@Autowired 
	private NoticeService noticeService;        // 최근 공지 5건
	@Autowired 
	private DashboardService dashboardService;  // 과제 마감 이벤트/달력 점
	@Autowired
	private MemberService memberService;
	
	
	@GetMapping(value = "/student", produces = "application/json; charset=UTF-8")
    public Map<String, Object> studentLayout(@RequestParam String memId) throws Exception {
        
        Map<String, Object> result = new HashMap<>();
        MemberVO member = memberService.getMemberById(memId); // mem_auth 포함된 회원 조회

        boolean isStudent = member.getMem_auth().contains("ROLE01");
        boolean isProfessor = member.getMem_auth().contains("ROLE02");
        System.out.println("memId: " + memId);
        System.out.println("mem_auth: " + member.getMem_auth());
        if (isStudent) {
            result.put("stulectureList", stuLecService.selectLectureListByStudentId(memId));
        } else {
            result.put("stulectureList", Collections.emptyList());
        }

        if (isProfessor) {
            result.put("prolectureList", lecClassService.selectLecClassByProfessorId(memId));
        } else {
            result.put("prolectureList", Collections.emptyList());
        }

        return result;
    }

    // ✅ 대시보드 콘텐츠 (iframe이 이걸 로드)
	@GetMapping("/student/home")
    public Map<String, Object> studentHome(HttpSession session) throws Exception {
        MemberVO member = (MemberVO) session.getAttribute("loginUser");
        if (member == null) throw new RuntimeException("로그인 필요");

        String memId = member.getMem_id();
        String auth  = member.getMem_auth();
        boolean isStudent   = auth != null && (auth.contains("ROLE01") || auth.contains("ROLE_ROLE01"));
        boolean isProfessor = auth != null && (auth.contains("ROLE02") || auth.contains("ROLE_ROLE02"));

        Map<String, Object> result = new HashMap<>();
        result.put("member", member);

        // 강의 목록
        if (isStudent) {
            result.put("stulectureList", stuLecService.selectLectureListByStudentId(memId));
        } else if (isProfessor) {
            result.put("stulectureList", lecClassService.selectLecClassByProfessorId(memId));
        } else {
            result.put("stulectureList", Collections.emptyList());
        }

        // 최근 공지
        result.put("noticeList", noticeService.getRecentNotices());

        // 이벤트/달력 점
        if (isProfessor) {
            result.put("eventList", dashboardService.getUpcomingHwEventsForProfessor(memId));
            result.put("hwDots",    dashboardService.getHwDotsForProfessor(memId));
        } else {
            result.put("eventList", dashboardService.getUpcomingHwEvents(memId));
            result.put("hwDots",    dashboardService.getHwDots(memId));
        }

        return result;
    }

	@GetMapping("/mypage")
    public Map<String, Object> mypage(HttpSession session) throws SQLException {
        MemberVO member = (MemberVO) session.getAttribute("loginUser");
        if (member == null) throw new RuntimeException("로그인 필요");

        String memId = member.getMem_id();
        AcademicVO academic = academicService.getByMemId(memId);
        if (academic == null) academic = new AcademicVO();

        Map<String, Object> result = new HashMap<>();
        result.put("member", member);
        result.put("student", academic);

        return result;
    }

}