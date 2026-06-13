package com.diary;

public class UserVO {
// 회원 정보를 담아 계층 간에 전달하는 객체
// Model 클래스. 순수하게 데이터 송수신만을 담당하는 객체(Getter/Setter 포함)
	
	private int userId; // 사용자 인덱스 (PK)
	private String username; // 사용자 로그인 ID
	private String password; // 사용자 비밀번호
	private String pin; // 개인숫자(4자리)(ID/PW 찾기용)
	
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public UserVO() {}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public UserVO(int userId, String username, String password) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
	}
	
	
	
	
	
}
