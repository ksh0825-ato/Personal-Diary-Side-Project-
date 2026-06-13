
package com.diary;

public class DiaryVO implements MySQLConnInfo {
// 일기 데이터 및 감정 상태를 담아 계층 간에 전달하는 객체
// Model 클래스. 순수하게 데이터 송수신만을 담당하는 객체(Getter/Setter 포함)

	private int diaryId; // 일기 인덱스 (PK)
	private int userId; // 작성자 인덱스 (FK)
	private int moodId; // 감정 인덱스 (FK)

	private String diaryDate; // 일기 작성 날짜 (YYYY-MM-DD)
	private String title; // 일기 제목
	private String content; // 일기 본문
	private String createdAt; // 최초 작성 시각
	private String updatedAt; // 최종 수정 시각 (최종 시각 표시 기능에 사용)

	// 2. ★ [조인 결과 저장용 필드 추가] MOOD_BADGE 테이블에서 가져올 데이터
    private String moodName; // 예: "기쁨", "우울"
    private String moodIcon; // 예: "😊", "😥"
	private String username;
	
	public int getDiaryId() {
		return diaryId;
	}
	
	private int userDiaryNo;
	public int getUserDiaryNo() { return userDiaryNo; }
	public void setUserDiaryNo(int userDiaryNo) { this.userDiaryNo = userDiaryNo; }

	public void setDiaryId(int diaryId) {
		this.diaryId = diaryId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public int getMoodId() {
		return moodId;
	}

	public void setMoodId(int moodId) {
		this.moodId = moodId;
	}

	public String getDiaryDate() {
		return diaryDate;
	}

	public void setDiaryDate(String diaryDate) {
		this.diaryDate = diaryDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public DiaryVO() {
	}

	public DiaryVO(int diaryId, int userId, int moodId, String diaryDate, String title, String content,
			String createdAt, String updatedAt) {
		super();
		this.diaryId = diaryId;
		this.userId = userId;
		this.moodId = moodId;
		this.diaryDate = diaryDate;
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	// 추가된 조인 필드의 Getter / Setter
    public String getMoodName() { return moodName; }
    public void setMoodName(String moodName) { this.moodName = moodName; }

    public String getMoodIcon() { return moodIcon; }
    public void setMoodIcon(String moodIcon) { this.moodIcon = moodIcon; }

}