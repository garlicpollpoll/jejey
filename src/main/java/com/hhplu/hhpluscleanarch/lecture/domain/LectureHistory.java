package com.hhplu.hhpluscleanarch.lecture.domain;

import com.hhplu.hhpluscleanarch.lecture.common.HistoryStatus;
import com.hhplu.hhpluscleanarch.lecture.controller.response.ApplyResponse;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lecture_history")
public class LectureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long lectureId;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "history_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private HistoryStatus historyStatus;

    @Column(name = "history_time", nullable = false)
    private LocalDateTime historyTime;

    // ApplyResponse 생성 메서드 추가
    public ApplyResponse toApplyResponse() {
        ApplyResponse response = new ApplyResponse();
        response.setLectureId(this.lectureId);
        response.setUserId(this.userId);
        response.setAppliedAt(this.appliedAt);
        response.setStatus(this.historyStatus);
        return response;
    }

    // 정적 팩토리 메서드
    public static LectureHistory create(Long userId, Long lectureId, HistoryStatus historyStatus, LocalDateTime historyTime) {
        LectureHistory lectureHistory = new LectureHistory();
        lectureHistory.setUserId(userId);
        lectureHistory.setLectureId(lectureId);
        lectureHistory.setAppliedAt(LocalDateTime.now());
        lectureHistory.setHistoryStatus(historyStatus);
        lectureHistory.setHistoryTime(historyTime);
        return lectureHistory;
    }

}