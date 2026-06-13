package com.diary;

public interface MySQLConnInfo {
    String DRIVER = "com.mysql.cj.jdbc.Driver";
    String URL = "jdbc:mysql://localhost:3306/side_project?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    String ID = "root";
    String PW = "1234";
    
    String sql = "SELECT * FROM MOOD_BADGE";
    String INSERT_USER = "INSERT INTO USERS (USERNAME, PASSWORD, PIN) VALUES (?, ?, ?);";
    String LOGIN_USER = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
    		
    
    String INSERT_DIARY = "INSERT INTO DIARIES (USER_ID, USER_NAME, MOOD_ID, DIARY_DATE, TITLE, CONTENT) VALUES (?, ?, ?, ?, ?, ?)";
    String SELECT_DIARIES_WITH_BADGE = 
        "SELECT d.*, m.MOOD_ICON FROM DIARIES d " +
        "LEFT JOIN MOOD_BADGE m ON d.MOOD_ID = m.MOOD_ID " +
        "WHERE d.USER_ID = ? ORDER BY d.DIARY_DATE DESC";

    // 💡 [추가] ID/PW 찾기 기능을 위한 DB SQL 쿼리 스트링 상수
    String FIND_ID_BY_PIN = "SELECT USERNAME FROM USERS WHERE PIN = ?";
    String VERIFY_USER_BY_PIN = "SELECT COUNT(*) FROM USERS WHERE USERNAME = ? AND PIN = ?";
    String UPDATE_PASSWORD = "UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ?";
}
