package com.hhplu.hhpluscleanarch.lecture.application;

import com.hhplu.hhpluscleanarch.lecture.domain.dto.LectureHistoryWithLecture;
import com.hhplu.hhpluscleanarch.lecture.exception.LectureHistoryNotFoundException;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.LectureHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    final private LectureHistoryRepository lectureHistoryRepository;

    public UserService(LectureHistoryRepository lectureHistoryRepository) {
        this.lectureHistoryRepository = lectureHistoryRepository;
    }

    /**
     * 특강 신청 완료 목록 조회 API
     * - 특정 userId 로 신청 완료된 특강 목록을 조회하는 API 를 작성합니다.
     * - 각 항목은 특강 ID 및 이름, 강연자 정보를 담고 있어야 합니다.
     */
    public List<LectureHistoryWithLecture> getApplyStatus(Long userId) {
        // 특강 신청 완료 목록을 조회합니다.
        List<LectureHistoryWithLecture> histories = lectureHistoryRepository.findCompletedLecturesByUserId(userId);

        if (histories == null || histories.isEmpty()) {
            throw new LectureHistoryNotFoundException("Lecture history not found");
        }

        return histories;
    }
}
