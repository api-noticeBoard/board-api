package com.portolio.notic_board.controller;

import com.portolio.notic_board.dto.ExamDto;
import com.portolio.notic_board.entity.ExamEntity;
import com.portolio.notic_board.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exams")
public class ExamController {
    private final ExamService examService;

    /**
     * 모든 시험 목록을 조회하거나 키워드로 검색합니다.
     *
     * @param keyword 검색 키워드 (선택 사항)
     * @return 시험 목록 (JSON)
     */
    @GetMapping
    public ResponseEntity<List<ExamDto>> listOrSearchExams(@RequestParam(value = "keyword", required = false) String keyword) {
        List<ExamEntity> exams;
        if (keyword != null && !keyword.trim().isEmpty()) {
            exams = examService.searchExams(keyword);
        } else {
            exams = examService.getAllExams();
        }

        List<ExamDto> examDtos = exams.stream()
                .map(ExamDto::new) // this::convertToDto -> ExamDto::new
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDtos);
    }

    /**
     * 특정 ID의 시험 정보를 조회합니다.
     *
     * @param id 시험 ID
     * @return 시험 정보 (JSON) 또는 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamDto> viewExam(@PathVariable Long id) {
        return examService.getExamById(id)
                .map(ExamDto::new) // this::convertToDto -> ExamDto::new
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 새로운 시험을 생성합니다.
     *
     * @param examDto 생성할 시험 정보 (JSON)
     * @return 생성된 시험 정보와 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<ExamDto> createExam(@RequestBody ExamDto examDto) {
        ExamEntity examToCreate = examDto.toEntity(); // convertToEntity(examDto) -> examDto.toEntity()
        ExamEntity createdExam = examService.createExam(examToCreate);

        ExamDto createdDto = new ExamDto(createdExam); // convertToDto(createdExam) -> new ExamDto(createdExam)
        URI location = URI.create("/exams/" + createdDto.getId());

        return ResponseEntity.created(location).body(createdDto);
    }

    /**
     * 특정 ID의 시험 정보를 수정합니다.
     *
     * @param id      수정할 시험 ID
     * @param examDto 수정할 시험 정보 (JSON)
     * @return 수정된 시험 정보와 200 OK 상태 코드
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamDto> updateExam(@PathVariable Long id, @RequestBody ExamDto examDto) {
        ExamEntity examToUpdate = examDto.toEntity(); // convertToEntity(examDto) -> examDto.toEntity()
        ExamEntity updatedExam = examService.updateExam(id, examToUpdate);

        return ResponseEntity.ok(new ExamDto(updatedExam)); // convertToDto(updatedExam) -> new ExamDto(updatedExam)
    }

    /**
     * 특정 ID의 시험을 삭제합니다.
     *
     * @param id 삭제할 시험 ID
     * @return 204 No Content 상태 코드
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}