package com.hhplu.hhpluscleanarch;

import com.hhplu.hhpluscleanarch.lecture.common.LectureStatus;
import com.hhplu.hhpluscleanarch.lecture.domain.Lecture;
import com.hhplu.hhpluscleanarch.lecture.domain.User;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.LectureRepository;
import com.hhplu.hhpluscleanarch.lecture.infrastructure.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DBinit {

    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        Lecture lecture1 = Lecture.builder()
                .title("lecture1")
                .lecturerName("강사 1")
                .capacity(0)
                .createdAt(LocalDateTime.now())
                .lectureStatus(LectureStatus.OPENED)
                .build();
        Lecture lecture2 = Lecture.builder()
                .title("lecture1")
                .lecturerName("강사 1")
                .capacity(0)
                .createdAt(LocalDateTime.now())
                .lectureStatus(LectureStatus.OPENED)
                .build();

        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);

        for (int i = 1; i <= 40; i++) {
            User user = User.builder()
                    .id(Long.valueOf(i))
                    .userId("user" + i)
                    .password("password")
                    .build();
            userRepository.save(user);

        }
    }
}
