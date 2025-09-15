package com.portfolio.board.exam.service;

import com.portfolio.board.exam.entity.ExamEntity;
import com.portfolio.board.exam.mapper.ExamMapper;
import com.portfolio.board.exam.repository.ExamRepository;
import com.portfolio.common.business.api.HolidayApiClient;
import com.portfolio.common.business.util.DateUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamMapper examMapper;

    @Autowired
    private HolidayApiClient holidayApiClient;

    @Transactional(readOnly = true)
    public List<ExamEntity> getAllExams() { // 메서드명 변경
        return examRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ExamEntity> getExamById(Long id) { // 메서드명 변경
        return examRepository.findById(id);
    }

    @Transactional
    public ExamEntity createExam(ExamEntity exam) {
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
        exam.setAuthor(updatedExam.getAuthor());

        // 3. save 호출 시 변경 감지(Dirty Checking)에 의해 UPDATE 쿼리가 실행되고,
        //    JPA Auditing이 updatedAt을 자동으로 갱신합니다.
        return examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Long id) {
        // 삭제하기 전에 데이터가 존재하는지 확인하는 것이 더 안전합니다.
        if (!examRepository.existsById(id)) {
            throw new EntityNotFoundException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }

    // --- MyBatis 관련 메서드 ---
    @Transactional(readOnly = true)
    public List<ExamEntity> searchExams(String keyword) { // 메서드명 변경
        return examMapper.searchExams(keyword);
    }

    @Transactional(readOnly = true)
    public List<ExamEntity> getAllExamsUsingMyBatisXml() { // 메서드명 변경
        return examMapper.findAllExamsXml();
    }

    @Transactional(readOnly = true)
    public Optional<ExamEntity> getExamByIdUsingMyBatisXml(Long id) { // 메서드명 변경
        ExamEntity result = (ExamEntity) examMapper.findExamByIdXml(id);

        return result == null ? Optional.ofNullable(examMapper.findExamByIdXml(id)) : Optional.of(result);
    }

    @Transactional
    public int updateExamByMyBatis(ExamEntity exam) { // 메서드명, 파라미터명 변경
        exam.setUpdatedAt(LocalDateTime.now());
        return examMapper.updateExamByMyBatis(exam);
    }

    @Transactional
    public void insertExamByMyBatis(ExamEntity exam) { // 메서드명, 파라미터명 변경
        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());
        examMapper.insertExam(exam);
    }

    @Transactional
    public void deleteExamByMyBatis(Long id) { // 메서드명 변경
        examMapper.deleteExam(id);
    }


    @Transactional
    public boolean isBizDay(String date) {
        LocalDate convertDate = DateUtils.parseDate(date);

        Set<LocalDate> holidays = holidayApiClient.getHoliday(convertDate.getYear());
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>> convertDate : {}", convertDate);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>> convertDate.getYear() : {}", convertDate.getYear());
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>> holidays : {}", holidays);

        return DateUtils.isBusinessDay(convertDate, holidays);
    }
}