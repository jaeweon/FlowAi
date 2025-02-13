package com.board.flowai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Table
@Entity
@ToString
@Getter
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
}
