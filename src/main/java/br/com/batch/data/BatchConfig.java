package br.com.batch.data;

import br.com.batch.model.BookModel;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public BookProcessor getProcessor() {
        return new BookProcessor();
    }

    @Bean
    public FlatFileItemReader<BookInput> reader() {
        String[] fieldNames = { "id", "title", "author", "pages" };

        return new FlatFileItemReaderBuilder<BookInput>().name("bookItemReader")
                .resource(new ClassPathResource("./books.csv")).delimited().names(fieldNames)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(BookInput.class);
                    }
                }).build();
    }

    @Bean
    public JdbcBatchItemWriter<BookModel> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<BookModel>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO book (id, title, author, pages) VALUES (:id,:title,:author,:pages)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<BookModel> writer){
        return stepBuilderFactory.get("step1")
                .<BookInput,BookModel> chunk(20)
                .reader(reader())
                .processor(getProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

}
