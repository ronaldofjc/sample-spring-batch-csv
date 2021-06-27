package br.com.batch.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInput implements Serializable {
    private String id;
    private String title;
    private String author;
    private String pages;
}
