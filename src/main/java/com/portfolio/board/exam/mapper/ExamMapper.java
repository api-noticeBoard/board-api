package com.portfolio.board.exam.mapper;

import com.portfolio.board.exam.entity.ExamEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface  ExamMapper {
    List<ExamEntity> searchExams(@Param("keyword") String keyword);

    int updateExamByMyBatis(ExamEntity exam);

    List<ExamEntity> findAllExamsXml(); // 메서드명 변경
    ExamEntity findExamByIdXml(Long id); // 메서드명 변경
    int insertExam(ExamEntity exam); // 메서드명 변경
    int updateExam(ExamEntity exam); // 메서드명 변경 (XML과 맞춤)
    int deleteExam(Long id); // 메서드명 변경
}
