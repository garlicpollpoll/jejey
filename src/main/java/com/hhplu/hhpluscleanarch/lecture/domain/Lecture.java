package com.hhplu.hhpluscleanarch.lecture.domain;

import com.hhplu.hhpluscleanarch.lecture.common.LectureStatus;
import com.hhplu.hhpluscleanarch.lecture.controller.response.LectureResponse;
import com.hhplu.hhpluscleanarch.lecture.exception.CapacityFullException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lecture")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecture {

    private static final int MAX_CAPACITY = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "lecture_name", nullable = false)
    private String lecturerName;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "lecture_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "lecture_status", nullable = false)
    @Enumerated(EnumType.STRING) // Enum 타입으로 저장하기 위해 추가
    private LectureStatus lectureStatus;

    public boolean isCapacityFull() {
        return this.capacity >= MAX_CAPACITY;
    }

    // 수강 인원을 추가하는 메서드
    public void addStudent() {
        if (isCapacityFull()) {
            throw new CapacityFullException("수강 인원이 가득 찼습니다.");
        }
        this.capacity++;
    }

    public LectureResponse toLectureResponse() {
        return new LectureResponse(
                this.id,
                this.title,
                this.lecturerName,
                this.capacity,
                this.lectureStatus
        );
    }
}