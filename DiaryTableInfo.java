package com.diary;

public interface DiaryTableInfo {
    
    // ==========================================
    // 테이블 및 컬럼 정보
    // ==========================================
    public static final String TABLE_NAME = "DIARIES"; // 메인 로직 쿼리와 대소문자 통일
    // 사용할 데이터베이스 테이블 이름이 DIARIES라고 정의

    // ==========================================
    // 회원가입 및 로그인 쿼리
    // ==========================================
    // ★ USERNAME은 문자열이므로 DAO에서도 setString으로 받아야 합니다.
    public static final String SQL_INSERT_USER = 
            "INSERT INTO USERS (USERNAME, PASSWORD) VALUES (?, ?)";
    // ? (물음표)의 의미: 자리 표시자, 지금 당장 값이 뭔지 모르지만, 나중에 사용자 입력을 받을 때 그 자리에 값을 채워 넣겠다는 약속(보안상 매우 중요한 방식)

    public static final String SQL_LOGIN_USER = 
            "SELECT USER_ID, USERNAME, PASSWORD FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";

    // ==========================================
    // 일기(Diary) CRUD 쿼리 (DAOImple 요구사항 반영)
    // ==========================================
 // ★ 수정: USER_DIARY_NO 컬럼 삽입 추가 (물음표 7개로 확장)
    public static final String INSERT_DIARY = 
            "INSERT INTO DIARIES (USER_ID, USER_NAME, USER_DIARY_NO, MOOD_ID, DIARY_DATE, TITLE, CONTENT) VALUES (?, ?, ?, ?, ?, ?, ?)";
    // ★ DAO의 getDiaryList에서 사용하는 이름으로 명칭 통일 및 MOOD_ICON 컬럼 조회 추가
    
    // * SQL은 우리가 글을 읽는 방식(위 -> 아래로)과 다르게, '어떤 테이블을 쓸지' 먼저 확정하고 나서 컬럼을 해석
    // SQL은 '한 줄'로 해석됨
    public static final String SELECT_DIARIES_WITH_BADGE = 
            "SELECT d.*, m.MOOD_ICON FROM DIARIES d " +
            // 1. FROM DIARIES d = DIARIES를 줄여서 d로 호칭하겠음(테이블 정의 완료)
            // 2. "준비된 d와 m 중 d의 모든 거랑 m의 MOOD_ICON을 가져와라"
            "LEFT JOIN MOOD_BADGE m ON d.MOOD_ID = m.MOOD_ID " +
            // 1. LEFT JOIN MOOD_BADGE m = MOOD_BADGE를 줄여서 m으로 호칭하겠음(테이블 정의 완료)
            // m.MOOD_ICON = "m(MOOD_BADGE 테이블)에 있는 'MOOD_ICON' 컬럼을 가져와라."
    		// LEFT JOIN: 일기 테이블(DIARIES)과 기분 아이콘 테이블(MOOD_BADGE)을 합쳐서, 일기 내용과 함께 기분 아이콘까지 한꺼번에 가져오겠다는 뜻
            "WHERE d.USER_ID = ? AND YEAR(d.DIARY_DATE) = 2026 " +
            // WHERE 절 = "조건을 걸어 데이터를 찾겠다"
            // d.USER_ID = ? : "로그인한 사람(사용자)의 일기만 가져와라"
            // YEAR(d.DIARY_DATE) = 2026 : "일기 날짜에서 '연도'만 뽑았을 때 2026년인 것만 골라라."
            "ORDER BY d.DIARY_DATE DESC";
    		// d.DIARY_DATE 기준으로 쿼리 결과를 내림차순(DESC; 최신순 -> 오래된순)으로 정렬하는 데 사용

    // 특정 날짜의 일기 상세 조회
    public static final String SQL_SELECT_DIARY_BY_DATE = 
    		"SELECT * FROM DIARIES WHERE USER_ID = ? AND DIARY_DATE = ?";
    // 달력 마킹을 위한 2026년 날짜 전체 조회
    public static final String SELECT_MARKED_DATES = 
    		"SELECT DIARY_DATE FROM DIARIES WHERE USER_ID = ? AND YEAR(DIARY_DATE) = 2026";
    // 일기 수정 및 삭제
    public static final String SQL_UPDATE_DIARY = 
    		"UPDATE DIARIES SET TITLE = ?, CONTENT = ?, MOOD_ID = ? WHERE DIARY_ID = ?";
    
    public static final String SELECT_MAX_USER_DIARY_NO = 
            "SELECT COALESCE(MAX(USER_DIARY_NO), 0) FROM DIARIES WHERE USER_ID = ?";
    // 일기 번호를 매길 때, 지금까지 쓴 가장 큰 번호를 찾되, 만약 아직 일기가 하나도 없다면 0을 가져오라는 뜻
    // (그래야 다음 일기 번호를 1부터 시작할 수 있음)
    
    public static final String SQL_DELETE_DIARY = 
    		"DELETE FROM DIARIES WHERE DIARY_ID = ?";
}