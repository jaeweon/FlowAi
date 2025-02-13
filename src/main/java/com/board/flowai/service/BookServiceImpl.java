package com.board.flowai.service;

import com.board.flowai.entity.Book;
import com.board.flowai.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public Book getBook(String title) {
        return bookRepository.findByTitle(title);
    }
}
