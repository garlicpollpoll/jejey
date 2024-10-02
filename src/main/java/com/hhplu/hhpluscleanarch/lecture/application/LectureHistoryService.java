package com.hhplu.hhpluscleanarch.lecture.application;

import com.hhplu.hhpluscleanarch.lecture.common.HistoryStatus;
import com.hhplu.hhpluscleanarch.lecture.controller.request.LectureRequest;
import com.hhplu.hhpluscleanarch.lecture.domain.Lecture;
import com.hhplu.hhpluscleanarch.lecture.domain.LectureHistory;
import com.hhplu.hhpluscleanarch.lecture.domain.User;
import com.hhplu.hhpluscleanarch.lecture.exception.CapacityFullException;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.LectureHistoryRepository;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.LectureRepository;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LectureHistoryService {


    private final LectureHistoryRepository lectureHistoryRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final EntityManager entityManager;
    private final LectureService lectureService;
    private Map<DuplicateDto, Boolean> isDuplicate = new ConcurrentHashMap<>();
    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    public LectureHistoryService(LectureHistoryRepository lectureHistoryRepository,
                                 UserRepository userRepository,
                                 LectureRepository lectureRepository,
                                 EntityManager entityManager, LectureService lectureService) {
        this.lectureHistoryRepository = lectureHistoryRepository;
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.entityManager = entityManager;
        this.lectureService = lectureService;
    }

    @Data
    @AllArgsConstructor
    static class DuplicateDto {
        private Long userId;
        private Long lectureId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DuplicateDto that = (DuplicateDto) o;
            return userId.equals(that.userId) && lectureId.equals(that.lectureId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, lectureId);
        }
    }

    /**
     * (핵심) 특강 신청 API
     * - 특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.
     * - 특강은 선착순 30명만 신청 가능합니다.
     * @param request
     * @return
     */
    @Transactional
    public LectureHistory apply(LectureRequest request) {
        Long userId = request.getUserId();
        Long lectureId = request.getLectureId();
        boolean isNewLectureHistory = false;

        threadLocal.set(false);

        // 사용자 검증
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 특강 검증
//        Lecture lecture = lectureRepository.findById(lectureId)
//                .orElseThrow(() -> new IllegalArgumentException("특강을 찾을 수 없습니다."));
        log.info("락 획득 전");
        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId).orElseThrow(() -> new IllegalArgumentException("특강을 찾을 수 없습니다."));
        log.info("락 획득 후");

        LectureHistory lectureHistory;

        DuplicateDto dto = new DuplicateDto(userId, lectureId);

        try {
            for (DuplicateDto key : isDuplicate.keySet()) {
                if (key.equals(dto)) {
                    throw new IllegalArgumentException("이미 존재하는 수강 목록이 있습니다.");
                }
                else {
                    isNewLectureHistory = true;
                }
            }

            if (!isNewLectureHistory && !threadLocal.get()) {
                lectureService.getLectureHistoryWithUserIdAndLectureId(userId, lectureId).ifPresent((lectures) -> {
                    throw new IllegalArgumentException("이미 존재하는 수강목록이 있습니다.");
                });
            }
//            isDuplicate.put(dto, true);

            lecture.addStudent();
            lectureHistory = LectureHistory.create(userId, lectureId, HistoryStatus.SUCCESS, LocalDateTime.now());
            log.info("객체 생성 " + lectureHistory.getHistoryStatus());
            isDuplicate.put(dto, false);
        } catch (CapacityFullException | IllegalArgumentException e) {
            lectureHistory = LectureHistory.create(userId, lectureId, HistoryStatus.FAIL, LocalDateTime.now());
            log.info("객체 생성 " + lectureHistory.getHistoryStatus());
            isDuplicate.put(dto, true);
            threadLocal.set(true);
        }

        // 신청 기록 저장
        log.info("객체 저장 전");
        LectureHistory save = lectureHistoryRepository.save(lectureHistory);
        entityManager.flush();
        log.info("객체 저장 후 : " + save.getHistoryStatus());

        return lectureHistory;
    }

}