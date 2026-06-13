
package com.diary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiaryDAOImple implements DiaryDAO {
    
    // DB 드라이버 로딩을 위한 공통 검증 메소드
    private void loadDriver() {
        try {
            Class.forName(MySQLConnInfo.DRIVER);
        } catch (ClassNotFoundException e) {
            // 콘솔창을 더럽히지 않도록 조용히 처리
        }
    }

    // ------------------------------------------------------------------
    // 1. 회원 관리 기능 (회원가입, 로그인, 중복체크, ID/PW 찾기)
    // ------------------------------------------------------------------

    @Override
    public int registerUser(UserVO user) {
        loadDriver();
        // MySQLConnInfo에 등록된 쿼리 사용: "INSERT INTO USERS (USERNAME, PASSWORD, PIN) VALUES (?, ?, ?);"
        String sql = MySQLConnInfo.INSERT_USER; 
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername().trim());
            pstmt.setString(2, user.getPassword().trim());
            pstmt.setString(3, user.getPin().trim()); // 화면에서 넘어온 4자리 개인숫자 안전하게 안착
            
            return pstmt.executeUpdate(); // 성공 시 1 리턴
            
        } catch (SQLException e) {
        	e.printStackTrace();
            // 중복 ID 가입 실패 시 자바 콘솔에 빨간색 에러 로그(e.printStackTrace)가 찍히는 것을 완벽 차단!
        }
        return 0; // 실패 시 0 리턴
    }

    @Override
    public UserVO loginUser(String username, String password) {
        System.out.println("디버깅용 쿼리 확인: " + MySQLConnInfo.LOGIN_USER); 
        String sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
        loadDriver();
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	
        	System.out.println("Input Username: " + username);
            System.out.println("Input Password: " + password);
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserVO user = new UserVO();
                    user.setUserId(rs.getInt("USER_ID"));
                    
                    int dbUserId = rs.getInt("USER_ID");
                    System.out.println("🚨 [긴급 점검] DB에서 막 꺼낸 유저 번호: " + dbUserId);
                    
                    user.setUsername(rs.getString("USERNAME"));
                    user.setPassword(rs.getString("PASSWORD"));
                    user.setPin(rs.getString("PIN"));
                   
                    System.out.println("🚨 [긴급 점검] UserVO 세팅 후 유저 번호: " + user.getUserId());
                    
                    return user; // 로그인 성공 시 작성자 정보가 가득 찬 VO 객체 리턴
                }
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return null; // 실패 시 null 리턴
    }

    @Override
    public UserVO getUserById(String username) {
        loadDriver();
        String sql = "SELECT * FROM USERS WHERE USERNAME = ?";
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserVO user = new UserVO();
                    user.setUserId(rs.getInt("USER_ID"));
                    user.setUsername(rs.getString("USERNAME"));
                    user.setPassword(rs.getString("PASSWORD"));
                    user.setPin(rs.getString("PIN"));
                    return user;
                }
            }
        } catch (SQLException e) {
            // 예외 로그 은닉
        }
        return null;
    }

    @Override
    public String findIdByPin(String pin) {
        loadDriver();
        String sql = MySQLConnInfo.FIND_ID_BY_PIN; 

        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pin.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("USERNAME"); 
                }
            }
        } catch (SQLException e) {
            // 예외 로그 은닉
        }
        return null;
    }

    @Override
    public boolean verifyUserByPin(String username, String pin) {
        loadDriver();
        String sql = MySQLConnInfo.VERIFY_USER_BY_PIN; 

        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, pin.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            // 예외 로그 은닉
        }
        return false;
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        loadDriver();
        String sql = MySQLConnInfo.UPDATE_PASSWORD; 

        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword.trim()); 
            pstmt.setString(2, username.trim());    
            pstmt.executeUpdate(); 
            
        } catch (SQLException e) {
            // 예외 로그 은닉
        }
    }

    // ------------------------------------------------------------------
    // 2. 일기(DIARIES) 및 감정 데이터 비즈니스 로직 연동 완료
    // ------------------------------------------------------------------

    @Override
    public int insertDiary(DiaryVO diary) {
        loadDriver();
        // 회원의 기존 일기 개수를 조회하여 USER_DIARY_NO(회원별 일기 번호 순번) 발행
        int nextNo = 1;
        String countSql = "SELECT IFNULL(MAX(USER_DIARY_NO), 0) + 1 FROM DIARIES WHERE USER_ID = ?";
        String insertSql = "INSERT INTO DIARIES (USER_ID, USER_NAME, USER_DIARY_NO, MOOD_ID, DIARY_DATE, TITLE, CONTENT) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW)) {
            try (PreparedStatement pstmt1 = conn.prepareStatement(countSql)) {
                pstmt1.setInt(1, diary.getUserId());
                try (ResultSet rs = pstmt1.executeQuery()) {
                    if (rs.next()) nextNo = rs.getInt(1);
                }
            }
            try (PreparedStatement pstmt2 = conn.prepareStatement(insertSql)) {
                pstmt2.setInt(1, diary.getUserId());
                pstmt2.setString(2, diary.getUserName());
                pstmt2.setInt(3, nextNo);
                if (diary.getMoodId() == 0) pstmt2.setNull(4, java.sql.Types.INTEGER);
                else pstmt2.setInt(4, diary.getMoodId());
                pstmt2.setString(5, diary.getDiaryDate());
                pstmt2.setString(6, diary.getTitle());
                pstmt2.setString(7, diary.getContent());
                return gexecute(pstmt2);
            }
        } catch (SQLException e) {e.printStackTrace();}
        return 0;
    }

    @Override
    public int updateDiary(DiaryVO diary) {
        loadDriver();
        String sql = "UPDATE DIARIES SET MOOD_ID = ?, TITLE = ?, UPDATED_AT = NOW(), CONTENT = ? WHERE DIARY_ID = ?";
        // NOW() 또는 시스템 현재 시간을 문자열로 변환하여 전달해야 합니다.
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (diary.getMoodId() == 0) pstmt.setNull(1, java.sql.Types.INTEGER);
            else pstmt.setInt(1, diary.getMoodId());
            pstmt.setString(2, diary.getTitle());
            pstmt.setString(3, diary.getContent());
            pstmt.setInt(4, diary.getDiaryId());
            return gexecute(pstmt);
        } catch (SQLException e) {
        	e.printStackTrace(); // 2. 에러가 나면 반드시 확인해야 함 }
        }
        return 0;
    }

    @Override
    public int deleteDiary(int diaryId) {
        loadDriver();
        String sql = "DELETE FROM DIARIES WHERE DIARY_ID = ?";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, diaryId);
            return gexecute(pstmt);
        } catch (SQLException e) {}
        return 0;
    }

    @Override
    public DiaryVO getDiaryByDate(int userId, String date) {
        loadDriver();
        String sql = "SELECT * FROM DIARIES WHERE USER_ID = ? AND DIARY_DATE = ?";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DiaryVO d = new DiaryVO();
                    d.setDiaryId(rs.getInt("DIARY_ID"));
                    d.setUserId(rs.getInt("USER_ID"));
                    d.setUserName(rs.getString("USER_NAME"));
                    d.setUserDiaryNo(rs.getInt("USER_DIARY_NO"));
                    d.setMoodId(rs.getInt("MOOD_ID"));
                    d.setDiaryDate(rs.getString("DIARY_DATE"));
                    d.setTitle(rs.getString("TITLE"));
                    d.setContent(rs.getString("CONTENT"));
                    d.setCreatedAt(rs.getString("CREATED_AT")); // 추가
                    d.setUpdatedAt(rs.getString("UPDATED_AT")); // 추가
                    return d;
                }
            }
        } catch (SQLException e) {}
        return null;
    }

    @Override
    public List<DiaryVO> getDiaryList(int userId) {
        loadDriver();
        List<DiaryVO> list = new ArrayList<>();
        String sql = "SELECT * FROM DIARIES WHERE USER_ID = ? ORDER BY DIARY_DATE DESC";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DiaryVO d = new DiaryVO();
                    d.setDiaryId(rs.getInt("DIARY_ID"));
                    d.setUserId(rs.getInt("USER_ID"));
                    d.setUserName(rs.getString("USER_NAME"));
                    d.setUserDiaryNo(rs.getInt("USER_DIARY_NO"));
                    d.setMoodId(rs.getInt("MOOD_ID"));
                    d.setDiaryDate(rs.getString("DIARY_DATE"));
                    d.setTitle(rs.getString("TITLE"));
                    d.setCreatedAt(rs.getString("CREATED_AT")); // 추가
                    d.setUpdatedAt(rs.getString("UPDATED_AT")); // 추가
                    d.setContent(rs.getString("CONTENT"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {}
        return list;
    }

    @Override
    public List<String> getMarkedDates(int userId) {
        loadDriver();
        List<String> dates = new ArrayList<>();
        String sql = "SELECT DIARY_DATE FROM DIARIES WHERE USER_ID = ?";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getString("DIARY_DATE"));
                }
            }
        } catch (SQLException e) {}
        return dates;
    }

    @Override
    public List<MoodBadgeVO> getAllMoodBadges() {
        loadDriver();
        List<MoodBadgeVO> list = new ArrayList<>();
        String sql = "SELECT * FROM MOOD_BADGE ORDER BY MOOD_ID ASC";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MoodBadgeVO m = new MoodBadgeVO();
                m.setMoodId(rs.getInt("MOOD_ID"));
                m.setMoodName(rs.getString("MOOD_NAME"));
                m.setMoodIcon(rs.getString("MOOD_ICON"));
                list.add(m);
            }
        } catch (SQLException e) {}
        return list;
    }

    private int gexecute(PreparedStatement pstmt) throws SQLException {
        return pstmt.executeUpdate();
    }

    @Override
    public DoneVO insertDone(DoneVO done) {
        loadDriver();
        // 쿼리문 예시: INSERT INTO DONE_LIST (USER_ID, DONE_DATE, CONTENT, IS_DONE) VALUES (?, ?, ?, ?)
        String sql = "INSERT INTO DONE_LIST (USER_ID, DONE_DATE, CONTENT, IS_DONE) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, done.getUserId());
            pstmt.setString(2, done.getDoneDate());
            pstmt.setString(3, done.getContent());
            pstmt.setBoolean(4, done.isDone());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return done; // 저장이 완료된 객체를 반환
    }

    @Override
    public DoneVO deleteDone(int doneId) {
        String sql = "DELETE FROM DONE_LIST WHERE DONE_ID = ?";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doneId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 삭제 성공 후 특별히 반환할 객체가 없으면 null
    }

 // 기존 getDoneListByDate의 VO 세팅 로직 보완
    @Override
    public List<DoneVO> getDoneListByDate(int userId, String date) {
        List<DoneVO> list = new ArrayList<>();
        String sql = "SELECT * FROM DONE_LIST WHERE USER_ID = ? AND DONE_DATE = ? ORDER BY DONE_DATE";
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DoneVO vo = new DoneVO();
                    vo.setDoneId(rs.getInt("DONE_ID"));
                    vo.setUserId(rs.getInt("USER_ID"));
                    vo.setDoneDate(rs.getString("DONE_DATE"));
                    vo.setContent(rs.getString("CONTENT"));
                    vo.setDone(rs.getBoolean("IS_DONE"));
                    list.add(vo);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void deleteDoneByDate(String doneDate, int userId) {
        loadDriver();
        String sql = "DELETE FROM DONE_LIST WHERE DONE_DATE = ? AND USER_ID = ?";
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doneDate);
            pstmt.setInt(2, userId);
            
            pstmt.executeUpdate(); // 쿼리 실행
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<DoneVO> getDoneList(int userId) {
        loadDriver();
        List<DoneVO> list = new ArrayList<>(); // 반드시 빈 리스트라도 생성해서 반환
        String sql = "SELECT * FROM DONE_LIST WHERE USER_ID = ? ORDER BY DONE_DATE";
        
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DoneVO vo = new DoneVO();
                    vo.setDoneId(rs.getInt("DONE_ID"));
                    vo.setUserId(rs.getInt("USER_ID"));
                    vo.setDoneDate(rs.getString("DONE_DATE"));
                    vo.setContent(rs.getString("CONTENT"));
                    vo.setDone(rs.getBoolean("IS_DONE"));
                    list.add(vo);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void updateDoneStatus(int doneId, boolean isDone) {
        String sql = "UPDATE DONE_LIST SET IS_DONE = ? WHERE DONE_ID = ?";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isDone);
            pstmt.setInt(2, doneId);
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
    
    @Override
    public void updateDoneContent(int doneId, String newContent) {
        String sql = "UPDATE DONE_LIST SET CONTENT = ? WHERE DONE_ID = ?";
        try (Connection conn = DriverManager.getConnection(MySQLConnInfo.URL, MySQLConnInfo.ID, MySQLConnInfo.PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newContent.trim());
            pstmt.setInt(2, doneId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}