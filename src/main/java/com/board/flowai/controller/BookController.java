package com.board.flowai.controller;

import com.board.flowai.aop.FlowTrackingAspect;
import com.board.flowai.entity.Book;
import com.board.flowai.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;
    private final FlowTrackingAspect flowTrackingAspect;  // AOP 직접 주입
    @GetMapping("/book")
    public Book getBooks(@RequestParam String title) {
         Book book = bookService.getBook(title);
        log.info("BookController - 트래킹 수동 실행");
        log.info("현재 트래킹된 데이터: {}", flowTrackingAspect.getFlowList()); // AOP 실행 여부 확인
        return book;
    }
}
