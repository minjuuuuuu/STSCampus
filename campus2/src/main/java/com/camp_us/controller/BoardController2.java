package com.camp_us.controller;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import com.camp_us.command.PageMakerMJ;
import com.camp_us.dto.BoardVO;
import com.camp_us.dto.MemberVO;
import com.camp_us.service.BoardService;
import com.camp_us.service.ReplyService;

@RestController
@RequestMapping("/api/board")
public class BoardController2 {

    private final BoardService boardService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    public BoardController2(BoardService boardService) {
        this.boardService = boardService;
    }

    // (선택) 강의/전공 선택을 세션에 저장 - 필요하면 프론트에서 호출
    @PostMapping("/changeMajor")
    public Map<String, Object> changeMajor(@RequestParam("lec_id") String lecId, HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        if (lecId == null || lecId.trim().isEmpty()) {
            res.put("ok", false); res.put("reason", "lec_id is blank");
            return res;
        }
        session.setAttribute("selectedLecId", lecId);
        res.put("ok", true); res.put("lec_id", lecId);
        return res;
    }

    /** 목록(JSON) — 기존 PageMakerMJ만 사용, category/lecid 세터가 없어도 에러 안 나도록 처리 */
    @GetMapping("")
    public Map<String, Object> list(
            @RequestParam(value = "page",     defaultValue = "1")  int page,
            @RequestParam(value = "perPage",  defaultValue = "10") int perPage,
            @RequestParam(value = "searchType", defaultValue = "") String searchType,
            @RequestParam(value = "keyword",  defaultValue = "") String keyword,
            // 아래 둘은 선택적 파라미터. PageMaker/SQL에서 안 쓰면 무시됨.
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            @RequestParam(value = "lecId",    required = false, defaultValue = "") String lecId
    ) throws Exception {

        PageMakerMJ pm = new PageMakerMJ();
        pm.setPage(page);
        pm.setPerPageNum(perPage);
        pm.setSearchType(searchType);
        pm.setKeyword(keyword);

        // PageMaker에 해당 세터가 **있을 때만** 반영 (없어도 컴파일 OK)
        try { pm.getClass().getMethod("setCategory", String.class).invoke(pm, category); } catch (Exception ignore) {}
        try { pm.getClass().getMethod("setLecId", String.class).invoke(pm, lecId); }     catch (Exception ignore) {}

        int totalCount = boardService.getTotalCount(pm);
        List<BoardVO> items = boardService.selectBoardList(pm);
        int totalPage = (int)Math.ceil((double) totalCount / perPage);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", page);
        pagination.put("perPage", perPage);
        pagination.put("totalCount", totalCount);
        pagination.put("totalPage", totalPage);

        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("items", items);
        res.put("page", pagination);
        return res;
    }

    /** 상세(JSON) — 조회수 증가는 **요구 서비스가 없으면** 건드리지 않음 */
    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable("id") String boardId) throws Exception {
        BoardVO item = boardService.selectBoardByNo(boardId);
        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("item", item);
        return res;
    }

    /** 등록(JSON) — 파일 없음 */
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> createJson(@RequestBody BoardVO body, HttpSession session) throws Exception {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) {
            Map<String, Object> fail = new HashMap<>();
            fail.put("ok", false); fail.put("reason", "로그인 필요");
            return fail;
        }

        body.setMemId(loginUser.getMem_id());
        if (body.getBoardDesc() == null || body.getBoardDesc().trim().isEmpty()) {
            body.setBoardDesc(body.getBoardContent());
        }
        if (body.getBoardDate() == null) {
            body.setBoardDate(new Timestamp(System.currentTimeMillis()));
        }
        if (body.getPfileName() == null || body.getPfileName().trim().isEmpty()) body.setPfileName("none.pdf");
        if (body.getPfileDetail() == null) body.setPfileDetail("");

        boardService.insertBoard(body); // PK는 DB/시퀀스에 맡김

        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("boardId", body.getBoardId()); // 생성 후 키가 채워지면 프론트에서 사용
        return res;
    }

    /** 등록(Multipart) — 파일 포함, 저장 경로는 기존 MVC와 동일 */
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> createMultipart(
            @RequestParam("boardName") String boardName,
            @RequestParam(value = "boardContent", required = false, defaultValue = "") String boardContent,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            HttpSession session
    ) throws Exception {

        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) {
            Map<String, Object> fail = new HashMap<>();
            fail.put("ok", false); fail.put("reason", "로그인 필요");
            return fail;
        }

        String uploadDir = "C:/campus_upload";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String pfileName = "none.pdf";
        String pfileDetail = "";

        if (files != null && !files.isEmpty()) {
            MultipartFile mf = files.get(0);
            if (mf != null && !mf.isEmpty()) {
                String originalName = mf.getOriginalFilename();
                String saveName = UUID.randomUUID().toString() + "_" + originalName;
                mf.transferTo(new File(dir, saveName));
                pfileName = saveName;
                pfileDetail = originalName;
            }
        }

        BoardVO vo = new BoardVO();
        vo.setBoardName(boardName);
        vo.setBoardContent(boardContent);
        vo.setBoardDesc(boardContent);
        vo.setMemId(loginUser.getMem_id());
        vo.setBoardDate(new Timestamp(System.currentTimeMillis()));
        vo.setPfileName(pfileName);
        vo.setPfileDetail(pfileDetail);

        boardService.insertBoard(vo);

        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("boardId", vo.getBoardId());
        res.put("fileName", pfileName);
        res.put("fileDetail", pfileDetail);
        return res;
    }

    /** 수정(JSON) — 파일 없이 */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateJson(@PathVariable("id") String id, @RequestBody BoardVO body, HttpSession session) throws Exception {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return fail("로그인 필요");

        BoardVO origin = boardService.selectBoardByNo(id);
        if (origin == null) return fail("not found");
        if (!canModify(loginUser, origin)) return fail("권한 없음");

        if (body.getBoardName() != null)    origin.setBoardName(body.getBoardName());
        if (body.getBoardDesc() != null)    origin.setBoardDesc(body.getBoardDesc());
        if (body.getBoardContent() != null) origin.setBoardContent(body.getBoardContent());
        if (body.getPfileName() != null)    origin.setPfileName(body.getPfileName());
        if (body.getPfileDetail() != null)  origin.setPfileDetail(body.getPfileDetail());

        boardService.updateBoard(origin);
        return ok();
    }

    /** 수정(Multipart) — 파일 교체/제거 */
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> updateMultipart(
            @PathVariable("id") String id,
            @RequestParam("boardName") String boardName,
            @RequestParam(value = "boardContent", required = false, defaultValue = "") String boardContent,
            @RequestParam(value = "removeFile", required = false) String removeFile, // "on"이면 제거
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            HttpSession session
    ) throws Exception {

        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return fail("로그인 필요");

        BoardVO origin = boardService.selectBoardByNo(id);
        if (origin == null) return fail("not found");
        if (!canModify(loginUser, origin)) return fail("권한 없음");

        String uploadDir = "C:/campus_upload";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String pfileName = origin.getPfileName();
        String pfileDetail = origin.getPfileDetail();

        if ("on".equalsIgnoreCase(removeFile)) {
            pfileName = "none.pdf";
            pfileDetail = "";
        }

        if (files != null && !files.isEmpty()) {
            MultipartFile mf = files.get(0);
            if (mf != null && !mf.isEmpty()) {
                String originalName = mf.getOriginalFilename();
                String saveName = UUID.randomUUID().toString() + "_" + originalName;
                mf.transferTo(new File(dir, saveName));
                pfileName = saveName;
                pfileDetail = originalName;
            }
        }

        origin.setBoardName(boardName);
        origin.setBoardDesc(boardContent);
        origin.setBoardContent(boardContent);
        origin.setPfileName(pfileName);
        origin.setPfileDetail(pfileDetail);

        boardService.updateBoard(origin);
        return ok();
    }

    /** 삭제(댓글 포함) */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable("id") String id, HttpSession session) throws Exception {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return fail("로그인 필요");

        BoardVO origin = boardService.selectBoardByNo(id);
        if (origin == null) return fail("not found");
        if (!canModify(loginUser, origin)) return fail("권한 없음");

        try { replyService.deleteAllReplies(id); } catch (Exception ignore) {}
        boardService.deleteBoard(id);
        return ok();
    }

    // ===== 유틸 =====
    private boolean canModify(MemberVO user, BoardVO post) {
        if (user == null || post == null) return false;
        String uid = user.getMem_id();
        String auth = user.getMem_auth() == null ? "" : user.getMem_auth();
        boolean owner = uid != null && uid.equals(post.getMemId());
        boolean staffOrAdmin =
                auth.contains("ROLE03") || auth.contains("ROLE04") ||
                auth.toUpperCase().contains("STAFF") || auth.toUpperCase().contains("ADMIN");
        return owner || staffOrAdmin;
    }
    private Map<String, Object> ok() { Map<String, Object> m = new HashMap<>(); m.put("ok", true); return m; }
    private Map<String, Object> fail(String r) { Map<String, Object> m = new HashMap<>(); m.put("ok", false); m.put("reason", r); return m; }
}
