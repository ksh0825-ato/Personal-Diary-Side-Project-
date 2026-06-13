package com.diary;

public class DoneVO {
    private int doneId;       // DB용 고유 번호
    private int userId;       // 사용자 ID
    private String doneDate;  // 작성 날짜
    private String content;   // 할 일 내용
    private boolean isDone;   // 완료 여부

    // 생성자, Getter, Setter 등을 포함합니다.
    public DoneVO() {}

    public int getDoneId() { return doneId; }
    public void setDoneId(int doneId) { this.doneId = doneId; }
    
    // ... 나머지 필드에 대한 Getter/Setter 작성

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getDoneDate() {
		return doneDate;
	}

	public void setDoneDate(String doneDate) {
		this.doneDate = doneDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

    
    
    
    
}