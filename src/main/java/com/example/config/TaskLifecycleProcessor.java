package com.example.config;

import com.example.repository.TaskRepository;
import com.example.service.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
        // Проверяем, является ли бин нужным нам типом (или реализует ли интерфейс TaskRepository)
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            System.out.println(
                ">>> [BPP Before Init]: Создан бин " + beanName + " типа " + bean.getClass()
                    .getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            System.out.println(
                ">>> [BPP After Init]: Бин " + beanName + " Полностью инициализирован");
        }
        return bean;
    }
}