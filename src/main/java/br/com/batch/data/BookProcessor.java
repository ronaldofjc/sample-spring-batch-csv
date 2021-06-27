package br.com.batch.data;

import br.com.batch.model.BookModel;
import org.springframework.batch.item.ItemProcessor;

public class BookProcessor implements ItemProcessor<BookInput, BookModel> {

    @Override
    public BookModel process(BookInput bookInput) throws Exception {
        return BookModel.builder()
                .id(Long.parseLong(bookInput.getId()))
                .title(bookInput.getTitle())
                .author(bookInput.getAuthor())
                .pages(Integer.parseInt(bookInput.getPages()))
                .build();
    }
}
