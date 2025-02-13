package com.board.flowai.service;

import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ApiScannerService {

    public List<Map<String, String>> getAllApis() {
        List<Map<String, String>> apiList = new ArrayList<>();

        // 프로젝트 패키지 전체 탐색 (Controller, Service, Repository, Entity 포함)
        Reflections reflections = new Reflections("com.board.flowai");

        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);
        Set<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class);
        Set<Class<?>> repositories = reflections.getTypesAnnotatedWith(Repository.class);
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);

        // 각 레이어별 API 추출
        extractApis(controllers, apiList, "Controller");
        extractApis(services, apiList, "Service");
        extractApis(repositories, apiList, "Repository");
        extractEntities(entities, apiList);

        System.out.println(" 탐색된 API 및 서비스 목록: " + apiList);
        return apiList;
    }

    private void extractApis(Set<Class<?>> classes, List<Map<String, String>> apiList, String layer) {
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                Map<String, String> apiInfo = new HashMap<>();
                apiInfo.put("controller", clazz.getSimpleName());
                apiInfo.put("method", method.getName());
                apiInfo.put("layer", layer);
                apiList.add(apiInfo);
            }
        }
    }

    private void extractEntities(Set<Class<?>> entities, List<Map<String, String>> apiList) {
        for (Class<?> entity : entities) {
            Map<String, String> entityInfo = new HashMap<>();
            entityInfo.put("controller", entity.getSimpleName()); // 엔티티 이름
            entityInfo.put("method", ""); // 엔티티에는 메서드 없음
            entityInfo.put("layer", "Entity");
            apiList.add(entityInfo);
        }
    }
}
