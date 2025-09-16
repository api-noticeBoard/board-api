package com.portfolio.board.exam.controller;

import com.portfolio.board.exam.dto.ExamDto;
import com.portfolio.board.exam.entity.ExamEntity;
import com.portfolio.board.exam.service.ExamService;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Exam", description = "예제 관련 API (h2DB 사용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams")
public class ExamController {
    private final ExamService examService;

    /**
     * 영업일 확인
     *
     * @param date
     * @return
     */
    @GetMapping("/bizday")
    @Operation(summary = "영업일 확인", description = "날짜로 검색")
    public boolean bizDay(String date) {

        return examService.isBizDay(date);
    }

    /**
     * 특정 ID의 정보 조회.
     *
     * @param id 조회할 ID
     * @return   조회 데이터 (JSON) 또는 404 Not Found
     */
    @GetMapping("/{id}")
    @Operation(summary = "ID로 정보 조회", description = "ID로 검색.")
    public ResponseEntity<ExamDto> viewExam(@Parameter(description = "조회할 ID", example = "1")
                                                @PathVariable Long id) {

        if (id == null || id <= 0) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);

        return examService.getExamById(id)
                .map(ExamDto::new) // this::convertToDto -> ExamDto::new
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    @GetMapping("mybatis/{id}")
    @Operation(summary = "[Mybatis] ID 정보 조회 ", description = "[Mybatis] ID로 검색")
    public ResponseEntity<ExamDto> viewExamXml(@Parameter(description = "조회할 ID", example = "1")
                                                    @PathVariable Long id) {
        if (id == null || id <= 0) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, id);

        Optional<ExamEntity> entity = examService.getExamByIdUsingMyBatisXml(id);

        if (entity.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(new ExamDto(entity.orElse(null)));
    }

    /**
     * 모든 목록을 조회 및 키워드 검색.
     *
     * @param keyword 검색 키워드 (선택 사항)
     * @return        목록 (JSON)
     */
    @GetMapping
    @Operation(summary = "keyword 조회", description = "keyword로 검색.")
    public ResponseEntity<List<ExamDto>> listOrSearchExams(@Parameter(description = "검색어", example = "프로젝트1")
                                                               @RequestParam(value = "keyword", required = false) String keyword) {
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
     * 새로운 데이터를 생성.
     *
     * @param examDto 생성할 정보 (JSON)
     * @return        생성된 데이터와 201 Created 상태 코드
     */
    @PostMapping
    @Operation(summary = "데이터 생성", description = "새로운 데이터를 생성")
    public ResponseEntity<ExamDto> createExam(@RequestBody ExamDto examDto) {
        ExamEntity examToCreate = examDto.toEntity(); // convertToEntity(examDto) -> examDto.toEntity()
        ExamEntity createdExam = examService.createExam(examToCreate);

        ExamDto createdDto = new ExamDto(createdExam); // convertToDto(createdExam) -> new ExamDto(createdExam)
        URI location = URI.create("/exams/" + createdDto.getId());

        return ResponseEntity.created(location).body(createdDto);
    }

    /**
     * 특정 ID의 정보를 수정.
     *
     * @param id      수정할 ID
     * @param examDto 수정할 정보 (JSON)
     * @return        수정된 정보와 200 OK 상태 코드
     */
    @PutMapping("/{id}")
    @Operation(summary = "데이터 수정", description = "특정 ID의 데이터 수정")
    public ResponseEntity<ExamDto> updateExam(@Parameter(description = "수정할 ID", example = "1")
                                                  @PathVariable Long id, @RequestBody ExamDto examDto) {
        ExamEntity examToUpdate = examDto.toEntity(); // convertToEntity(examDto) -> examDto.toEntity()
        ExamEntity updatedExam = examService.updateExam(id, examToUpdate);

        return ResponseEntity.ok(new ExamDto(updatedExam)); // convertToDto(updatedExam) -> new ExamDto(updatedExam)
    }

    /**
     * 특정 ID의 데이터를 삭제.
     *
     * @param id 삭제할 ID
     * @return   204 No Content 상태 코드
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "데이터 삭제", description = "새로운 데이터를 생성")
    public ResponseEntity<Void> deleteExam(@Parameter(description = "삭제할 ID", example = "1")
                                               @PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}