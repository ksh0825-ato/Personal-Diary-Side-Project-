package com.diary;
public class MoodBadgeVO {
    // 감정 배지 정보를 담아 계층 간에 전달하는 객체
    // Model 클래스. 순수하게 데이터 송수신만을 담당하는 객체(Getter/Setter 포함)
    
    private int moodId;      // 감정 인덱스 (PK) - MOOD_ID INT
    private String moodName; // 감정 이름 (예: 기쁨, 평온) - MOOD_NAME VARCHAR(20)
    private String moodIcon; // 감정 이모지 아이콘 - MOOD_ICON VARCHAR(100)

    // 1. 기본 생성자 (기본 필수)
    public MoodBadgeVO() {
        super();
    }

    // 2. 모든 필드를 초기화하는 생성자
    public MoodBadgeVO(int moodId, String moodName, String moodIcon) {
        super();
        this.moodId = moodId;
        this.moodName = moodName;
        this.moodIcon = moodIcon;
    }

    // 3. Getter / Setter 메서드
    public int getMoodId() {
        return moodId;
    }

    public void setMoodId(int moodId) {
        this.moodId = moodId;
    }

    public String getMoodName() {
        return moodName;
    }

    public void setMoodName(String moodName) {
        this.moodName = moodName;
    }

    public String getMoodIcon() {
        return moodIcon;
    }

    public void setMoodIcon(String moodIcon) {
        this.moodIcon = moodIcon;
    }
    
    
    
}

