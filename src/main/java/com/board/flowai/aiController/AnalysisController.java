package com.board.flowai.aiController;

import com.board.flowai.aop.FlowTrackingAspect;
import com.board.flowai.service.ApiScannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/analyze")
@RequiredArgsConstructor
public class AnalysisController {
    private final FlowTrackingAspect flowTrackingAspect;
    private final ApiScannerService apiScannerService;

    @GetMapping
    public List<Map<String, String>> analyzeApi() {
        List<Map<String, String>> result = new ArrayList<>();

        // AOP에서 추적된 API 가져오기 (필터링된 데이터만 저장됨)
        List<Map<String, String>> trackedApis = flowTrackingAspect.getFlowList();
        result.addAll(trackedApis);

        // Reflection으로 가져온 모든 API, Service, Repository, Entity 가져오기
        List<Map<String, String>> allApis = apiScannerService.getAllApis();

        Set<String> trackedApiNames = new HashSet<>();
        for (Map<String, String> trackedApi : trackedApis) {
            trackedApiNames.add(trackedApi.get("callee"));
        }

        Map<String, String> serviceImplMap = new HashMap<>();
        Map<String, String> repositoryMap = new HashMap<>();
        Map<String, String> entityMap = new HashMap<>();

        for (Map<String, String> api : allApis) {
            String className = api.get("controller");
            String methodName = api.get("method");
            String layer = api.get("layer");

            if (className.contains("ApiScannerService") || className.contains("AnalysisController")) {
                continue;
            }

            if (!trackedApiNames.contains(className)) {
                if (layer.equals("Controller")) {
                    result.add(Map.of("caller", "Auto_Discovered_API", "callee", className));
                } else if (layer.equals("Service") && className.endsWith("ServiceImpl")) {
                    serviceImplMap.put(className, "BookRepository"); // 바로 Repository로 연결
                } else if (layer.equals("Repository")) {
                    repositoryMap.put(className, className);
                } else if (layer.equals("Entity")) {
                    entityMap.put(className, className);
                }
            }
        }

        // 컨트롤러 → 서비스 Impl 연결
        for (String serviceImpl : serviceImplMap.keySet()) {
            String controllerName = serviceImpl.replace("ServiceImpl", "Controller");
            result.add(Map.of("caller", controllerName, "callee", serviceImpl));
        }

        // 서비스 Impl → 레포지토리 직접 연결
        for (String serviceImpl : serviceImplMap.keySet()) {
            result.add(Map.of("caller", serviceImpl, "callee", "Repository"));
        }

        // 레포지토리 → 엔티티 연결
        for (String entity : entityMap.keySet()) {
            String repositoryName = entity + "Repository";
            if (repositoryMap.containsKey(repositoryName)) {  // 존재하는 경우에만 추가
                result.add(Map.of("caller", repositoryName, "callee", entity));
            }
        }

        System.out.println("API 분석 결과: " + result);

        return result;
    }
}