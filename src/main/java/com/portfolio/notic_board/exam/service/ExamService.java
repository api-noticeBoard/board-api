package com.portfolio.notic_board.exam.service;

import com.portfolio.notic_board.exam.entity.ExamEntity;
import com.portfolio.notic_board.exam.mapper.ExamMapper;
import com.portfolio.notic_board.exam.repository.ExamRepository;
import jakarta.persistence.EntityNotFoundException; // 표준 예외 사용
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository; // 레포지토리 객체명 변경
    private final ExamMapper examMapper; // 매퍼 객체명 변경

    @Transactional(readOnly = true)
    public List<ExamEntity> getAllExams() { // 메서드명 변경
        return examRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ExamEntity> getExamById(Long id) { // 메서드명 변경
        return examRepository.findById(id);
    }

    @Transactional
    public ExamEntity createExam(ExamEntity exam) { // 메서드명, 파라미터명 변경
        // JPA Auditing이 createdAt, updatedAt을 자동으로 설정해줍니다.
        return examRepository.save(exam);
    }

    @Transactional
    public ExamEntity updateExam(Long id, ExamEntity updatedExam) { // 메서드명, 파라미터명 변경
        // 1. ID로 기존 엔티티를 조회합니다.
        ExamEntity exam = examRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with id: " + id));

        // 2. DTO로부터 받은 데이터로 기존 엔티티의 필드를 업데이트합니다.
        exam.setTitle(updatedExam.getTitle());
        exam.setContent(updatedExam.getContent());
        exam.setAuthor(updatedExam.getAuthor()); // author 필드 업데이트 추가

        // 3. save 호출 시 변경 감지(Dirty Checking)에 의해 UPDATE 쿼리가 실행되고,
        //    JPA Auditing이 updatedAt을 자동으로 갱신합니다.
        return examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Long id) { // 메서드명 변경
        // 삭제하기 전에 데이터가 존재하는지 확인하는 것이 더 안전합니다.
        if (!examRepository.existsById(id)) {
            throw new EntityNotFoundException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ExamEntity> searchExams(String keyword) { // 메서드명 변경
        return examMapper.searchExams(keyword);
    }

    // --- MyBatis 관련 메서드는 변경 없음 ---
    @Transactional(readOnly = true)
    public List<ExamEntity> getAllExamsUsingMyBatisXml() { // 메서드명 변경
        return examMapper.findAllExamsXml();
    }

    @Transactional(readOnly = true)
    public ExamEntity getExamByIdUsingMyBatisXml(Long id) { // 메서드명 변경
        return examMapper.findExamByIdXml(id);
    }

    @Transactional
    public int updateExamByMyBatis(ExamEntity exam) { // 메서드명, 파라미터명 변경
        return examMapper.updateExamByMyBatis(exam);
    }

    @Transactional
    public void insertExamByMyBatis(ExamEntity exam) { // 메서드명, 파라미터명 변경
        examMapper.insertExam(exam);
    }

    @Transactional
    public void deleteExamByMyBatis(Long id) { // 메서드명 변경
        examMapper.deleteExam(id);
    }
}