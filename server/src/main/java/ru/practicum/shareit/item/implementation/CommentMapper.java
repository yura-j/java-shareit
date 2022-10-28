package ru.practicum.shareit.item.implementation;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.CreateCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static Comment fromCreateDto(CreateCommentDto dto, Item item, User author) {
        return Comment.builder()
                .author(author)
                .item(item)
                .text(dto.getText())
                .build();
    }

    public static CommentDto fromComment(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
