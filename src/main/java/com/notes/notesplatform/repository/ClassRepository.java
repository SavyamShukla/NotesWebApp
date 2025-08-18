package com.notes.notesplatform.repository;
import java.util.List;
import com.notes.notesplatform.model.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import com.notes.notesplatform.model.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {


 List<ClassEntity> findByCourseIdAndDeletedFalse(Long courseId);
 List<ClassEntity> findByDeletedTrue();
}