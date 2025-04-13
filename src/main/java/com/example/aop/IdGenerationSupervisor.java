package com.example.aop;

import com.example.model.Person;
import com.example.model.Task;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class IdGenerationSupervisor {

    private static final String TASK_STATUS_ID = "taskID";
    private static final String PERSON_ID = "personID";

    private final ValueOperations<String, Long> longValueOperations;

    @Autowired
    public IdGenerationSupervisor(RedisTemplate<String, Long> redisTemplate, RedisOperations<String, Long> operations) {
        this.longValueOperations = redisTemplate.opsForValue();
    }

    @Before("execution(* com.example.repository.PersonRepository.save(*))")
    void ensurePersonHasCorrectID(JoinPoint joinPoint) {
        if (joinPoint.getArgs()[0] instanceof Person person && person.getId() == null){
            Long id = longValueOperations.increment(PERSON_ID);
            log.info("Generated person id: {}", id);
            person.setId(id);
        }
    }

    @Before("execution(* com.example.repository.TaskRepository.save(*))")
    void ensureTaskHasCorrectID(JoinPoint joinPoint) {
        if (joinPoint.getArgs()[0] instanceof Task task && task.getId() == null){
            Long id = longValueOperations.increment(TASK_STATUS_ID);
            log.info("Generated task id: {}", id);
            task.setId(id);
        }
    }
}
