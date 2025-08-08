package com.portfolio.notic_board.exam.mapper;

import com.portfolio.notic_board.exam.entity.ExamEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface  ExamMapper {
    @Select("SELECT * FROM exam WHERE title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')")
    List<ExamEntity> searchExams(@Param("keyword") String keyword);

    @Update("UPDATE exam SET title = #{title}, content = #{content}, updated_at = NOW() WHERE id = #{id}")
    int updateExamByMyBatis(ExamEntity exam);

    List<ExamEntity> findAllExamsXml(); // 메서드명 변경
    ExamEntity findExamByIdXml(Long id); // 메서드명 변경
    int insertExam(ExamEntity exam); // 메서드명 변경
    int updateExam(ExamEntity exam); // 메서드명 변경 (XML과 맞춤)
    int deleteExam(Long id); // 메서드명 변경
}
