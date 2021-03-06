package org.gty.demo.service.impl;

import org.gty.demo.constant.DeleteMark;
import org.gty.demo.mapper.StudentStudentVoMapper;
import org.gty.demo.model.entity.Student;
import org.gty.demo.model.vo.StudentVo;
import org.gty.demo.repository.StudentRepository;
import org.gty.demo.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final StudentStudentVoMapper studentStudentVoMapper;

    public StudentServiceImpl(@Nonnull final StudentRepository studentRepository,
                              @Nonnull final StudentStudentVoMapper studentStudentVoMapper) {
        this.studentRepository = Objects.requireNonNull(studentRepository,
            "studentRepository must not be null");
        this.studentStudentVoMapper = Objects.requireNonNull(studentStudentVoMapper,
            "studentStudentVoMapper must not be null");
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true, rollbackFor = Throwable.class)
    @Cacheable(cacheNames = "students", keyGenerator = "keyGenerator")
    @Nonnull
    @Override
    public Optional<StudentVo> findById(final long id) {
        return studentRepository.findByIdAndDeleteMark(id, DeleteMark.NOT_DELETED)
            .map(studentStudentVoMapper::studentToStudentVo);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true, rollbackFor = Throwable.class)
    @Cacheable(cacheNames = "students", keyGenerator = "keyGenerator")
    @Nonnull
    @Override
    public Collection<StudentVo> findByName(@Nonnull final String name) {
        return studentRepository
            .findByNameContainingAndDeleteMark(Objects.requireNonNull(name, "name must not be null"), DeleteMark.NOT_DELETED)
            .stream()
            .map(studentStudentVoMapper::studentToStudentVo)
            .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true, rollbackFor = Throwable.class)
    @Cacheable(cacheNames = "students", keyGenerator = "keyGenerator")
    @Nonnull
    @Override
    public Page<StudentVo> findByPage(@Nonnull final Pageable pageable) {
        return studentRepository
            .findByDeleteMark(DeleteMark.NOT_DELETED, Objects.requireNonNull(pageable, "pageable must not be null"))
            .map(studentStudentVoMapper::studentToStudentVo);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = "students", keyGenerator = "keyGenerator", allEntries = true)
    @Override
    public void save(@Nonnull final Student student) {
        studentRepository.saveAndFlush(Objects.requireNonNull(student, "student must not be null"));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = "students", keyGenerator = "keyGenerator", allEntries = true)
    @Override
    public void delete(final long id) {
        final var student = studentRepository.findByIdAndDeleteMark(id, DeleteMark.NOT_DELETED).orElseThrow();

        student.setDeleteMark(DeleteMark.DELETED);

        studentRepository.saveAndFlush(student);
    }
}
