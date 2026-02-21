package com.example.Library_Management_System.config;

import com.example.Library_Management_System.dto.BorrowRecordDTO;
import com.example.Library_Management_System.entity.BorrowRecord;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Custom mapping for BorrowRecord to BorrowRecordDTO
        modelMapper.addMappings(new PropertyMap<BorrowRecord, BorrowRecordDTO>() {
            @Override
            protected void configure() {
                // Map nested objects to flat DTO fields
                map().setBookId(source.getBook().getBookId());
                map().setBookTitle(source.getBook().getBookTitle());
                map().setBookAuthor(source.getBook().getBookAuthor());

                map().setMemberId(source.getMember().getMemId());
                map().setMemberName(source.getMember().getMemFullName());
                map().setMemberEmail(source.getMember().getMemEmail());

                if (source.getAdmin() != null) {
                    map().setAdminId(source.getAdmin().getAdminId());
                    map().setAdminName(source.getAdmin().getAdminFullName());
                }
            }
        });

        return modelMapper;
    }
}