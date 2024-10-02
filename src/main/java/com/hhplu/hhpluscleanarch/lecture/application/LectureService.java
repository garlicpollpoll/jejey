package com.hhplu.hhpluscleanarch.lecture.application;

import com.hhplu.hhpluscleanarch.lecture.common.HistoryStatus;
import com.hhplu.hhpluscleanarch.lecture.common.LectureStatus;
import com.hhplu.hhpluscleanarch.lecture.domain.Lecture;
import com.hhplu.hhpluscleanarch.lecture.domain.LectureHistory;
import com.hhplu.hhpluscleanarch.lecture.domain.dto.LectureHistoryWithLecture;
import com.hhplu.hhpluscleanarch.lecture.exception.LectureNotFoundException;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.LectureHistoryRepository;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.LectureRepository;
import com.hhplu.hhpluscleanarch.lecture.controller.response.LectureResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LectureService {

    private final LectureHistoryRepository lectureHistoryRepository;
    private LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }

    /**
     * 날짜별로 현재 신청 가능한 특강 목록을 조회하는 API
     * @return
     */
    public List<Lecture> getLectures() {
        // 강의 목록 조회
        List<Lecture> lectures = lectureRepository.findByCapacityLessThanAndLectureStatus(30, LectureStatus.OPENED);

        if (lectures == null || lectures.isEmpty()) {
            throw new LectureNotFoundException("Lecture not found");
        }

        return lectures;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<LectureHistory> getLectureHistoryWithUserIdAndLectureId(Long userId, Long lectureId) {
        return lectureHistoryRepository.findByUserIdAndLectureId(userId, lectureId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<LectureHistoryWithLecture> getLectureHistoryWithAnotherTransaction(Long userId, Long lectureId) {
        return lectureHistoryRepository.findCompletedLectureByUserIdAndLectureId(userId, lectureId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<LectureHistory> findAll() {
        return lectureHistoryRepository.findAll();
    }
}
