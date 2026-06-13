package com.diary;
import java.util.List;

public interface DiaryDAO {
    // 1. 회원 관련 로직
    int registerUser(UserVO user); 
    UserVO loginUser(String username, String password); 

    // 2. 일기 CRUD 로직
    int insertDiary(DiaryVO diary); 
    int updateDiary(DiaryVO diary); 
    int deleteDiary(int diaryId); 

    // 3. 일기 조회 및 달력 마킹 로직
    DiaryVO getDiaryByDate(int userId, String date); 
    List<DiaryVO> getDiaryList(int userId); 
    List<String> getMarkedDates(int userId); 
    
    void updateDoneStatus(int doneId, boolean isDone);
    
    void updateDoneContent(int doneId, String newContent);
    
    // ★ [추가] DB에 저장된 모든 감정 배지 리스트를 가져오는 비즈니스 로직
    List<MoodBadgeVO> getAllMoodBadges(); 
    
    String findIdByPin(String pin);
    boolean verifyUserByPin(String username, String pin);
    void updatePassword(String username, String newPassword);
    UserVO getUserById(String username);    
    
    DoneVO insertDone(DoneVO done);
    DoneVO deleteDone(int doneId);
    public List<DoneVO> getDoneList(int userId);
    
    public void deleteDoneByDate(String doneDate, int userId);
	List<DoneVO> getDoneListByDate(int userId, String date);

}

