package com.diary;

import javax.swing.*;
import java.awt.*;

public class FindAccountDialog extends JDialog {
    private final DiaryDAO diaryDAO;

    public FindAccountDialog(JFrame parent, DiaryDAO dao) {
        super(parent, "아이디 / 비밀번호 찾기", true);
        this.diaryDAO = dao;
        
        setSize(440, 360);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(DiaryMain.FONT_BOLD);

        tabbedPane.addTab("아이디 찾기", createFindIdPanel());
        tabbedPane.addTab("비밀번호 재설정", createResetPwPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // [1] 개인숫자로 실제 가입된 ID 매칭 확인 패널
    private JPanel createFindIdPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        panel.add(new JLabel("등록한 개인숫자(4자리):"), gbc);

        JTextField pinField = new JTextField();
        pinField.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        panel.add(pinField, gbc);

        JButton findIdBtn = new JButton("아이디 확인하기");
        findIdBtn.setFont(DiaryMain.FONT_BOLD);
        findIdBtn.setBackground(DiaryMain.COLOR_PRIMARY);
        findIdBtn.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 12, 12, 12);
        panel.add(findIdBtn, gbc);

        findIdBtn.addActionListener(e -> {
            String pin = pinField.getText().trim();
            if (pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "개인숫자를 입력해 주세요.", "안내", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ⭐ [해결] 더미데이터 제거! 실제 DAO의 연동 메서드를 수행합니다.
            String foundId = diaryDAO.findIdByPin(pin);

            if (foundId != null && !foundId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "회원님의 아이디는 [" + foundId + "] 입니다.", "아이디 찾기 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "해당 개인숫자로 등록된 아이디가 존재하지 않습니다.", "찾기 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // [2] 정보 정밀 매칭 검증 및 패스워드 실시간 데이터 변경 패널
    private JPanel createResetPwPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID 입력창
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        panel.add(new JLabel("아이디 (ID):"), gbc);
        
        JTextField idField = new JTextField();
        idField.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        panel.add(idField, gbc); // 💡 오타 수정 완료! (mainPanel -> panel)

        // 개인숫자 입력창
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        panel.add(new JLabel("등록한 개인숫자(4자리):"), gbc);
        
        JTextField pinField = new JTextField();
        pinField.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.65;
        panel.add(pinField, gbc);

        // 변경 지정용 새 비밀번호 입력창
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        panel.add(new JLabel("새로운 비밀번호:"), gbc);
        
        JPasswordField newPwField = new JPasswordField();
        newPwField.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.65;
        panel.add(newPwField, gbc);

        JButton resetPwBtn = new JButton("비밀번호 변경 및 재설정");
        resetPwBtn.setFont(DiaryMain.FONT_BOLD);
        resetPwBtn.setBackground(DiaryMain.COLOR_PRIMARY);
        resetPwBtn.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 12, 12, 12);
        panel.add(resetPwBtn, gbc);

        resetPwBtn.addActionListener(e -> {
            String username = idField.getText().trim();
            String pin = pinField.getText().trim();
            String newPassword = new String(newPwField.getPassword()).trim();

            if (username.isEmpty() || pin.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "재설정을 위해 모든 칸을 빠짐없이 입력하세요.", "안내", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ⭐ [해결] 무조건 통과되던 논리 오류 수정. 
            // 실제 입력한 ID와 개인숫자가 정확히 일치하는지 데이터 레이어에서 판단합니다.
            boolean isVerified = diaryDAO.verifyUserByPin(username, pin);

            if (isVerified) {
                // ⭐ [해결] 무조건 이전 비밀번호로만 로그인되던 치명적인 현상 완벽 방어!
                // DAO 데이터 관리 컴포넌트 내부의 비밀번호를 동기화하여 변경 연동을 강제 지시합니다.
                diaryDAO.updatePassword(username, newPassword);
                
                JOptionPane.showMessageDialog(this, "비밀번호가 성공적으로 변경되었습니다!\n새로운 비밀번호로 로그인해 주세요.", "재설정 완료", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "입력하신 아이디와 개인숫자 정보가 유효하지 않거나 일치하지 않습니다.", "인증 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }
}